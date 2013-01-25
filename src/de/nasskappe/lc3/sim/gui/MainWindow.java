package de.nasskappe.lc3.sim.gui;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;
import javax.swing.table.TableCellRenderer;

import de.nasskappe.lc3.sim.gui.renderer.Binary16TableCellRenderer;
import de.nasskappe.lc3.sim.gui.renderer.BreakpointTableCellRenderer;
import de.nasskappe.lc3.sim.gui.renderer.Hex16TableCellRenderer;
import de.nasskappe.lc3.sim.gui.renderer.LabelTableCellRenderer;
import de.nasskappe.lc3.sim.maschine.CPU;
import de.nasskappe.lc3.sim.maschine.ICPUListener;
import de.nasskappe.lc3.sim.maschine.Register;
import de.nasskappe.lc3.sim.maschine.cmds.ICommand;

public class MainWindow extends JFrame implements ICPUListener {

	private JPanel contentPane;
	private JTable registerTable;
	private CPU cpu;
	private JTable codeTable;
	private JFileChooser fc;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainWindow frame = new MainWindow();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public MainWindow() {
		cpu = new CPU();
		cpu.addCpuListener(this);
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		
		fc = new JFileChooser();
		
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		
		JMenu mnFile = new JMenu("File");
		menuBar.add(mnFile);
		
		JMenuItem mntmLoadobjFile = new JMenuItem("Load file ...");
		mntmLoadobjFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				selectAndLoadFile();
			}
		});
		mnFile.add(mntmLoadobjFile);
		
		JSeparator separator = new JSeparator();
		mnFile.add(separator);
		
		JMenuItem mntmExit = new JMenuItem("Exit");
		mnFile.add(mntmExit);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
		JPanel topPanel = new JPanel();
		contentPane.add(topPanel, BorderLayout.NORTH);
		GridBagLayout gbl_topPanel = new GridBagLayout();
		gbl_topPanel.columnWidths = new int[]{0, 0};
		gbl_topPanel.rowHeights = new int[]{0, 0, 0};
		gbl_topPanel.columnWeights = new double[]{1.0, Double.MIN_VALUE};
		gbl_topPanel.rowWeights = new double[]{1.0, 1.0, Double.MIN_VALUE};
		topPanel.setLayout(gbl_topPanel);
		
		JPanel registerPanel = new JPanel();
		GridBagConstraints gbc_registerPanel = new GridBagConstraints();
		gbc_registerPanel.insets = new Insets(0, 0, 5, 0);
		gbc_registerPanel.fill = GridBagConstraints.BOTH;
		gbc_registerPanel.gridx = 0;
		gbc_registerPanel.gridy = 0;
		topPanel.add(registerPanel, gbc_registerPanel);
		registerPanel.setLayout(new BorderLayout(0, 0));
		
		JLabel lblRegister = new JLabel("Register:");
		registerPanel.add(lblRegister, BorderLayout.NORTH);
		
		RegisterTableModel model = new RegisterTableModel();
		cpu.addCpuListener(model);
		model.registerChanged(cpu, Register.IR, (short) 0);
		
		registerTable = new JTable();
		registerTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		registerTable.setRowSelectionAllowed(false);
		registerTable.setFillsViewportHeight(true);
		registerTable.setModel(model);
	
		TableCellRenderer cellLabelRenderer = new LabelTableCellRenderer();
		registerTable.getColumnModel().getColumn(0).setCellRenderer(cellLabelRenderer);
		registerTable.getColumnModel().getColumn(2).setCellRenderer(cellLabelRenderer);
		registerTable.getColumnModel().getColumn(4).setCellRenderer(cellLabelRenderer);
		registerTable.setDefaultRenderer(Integer.class, new Hex16TableCellRenderer());
		registerPanel.add(registerTable, BorderLayout.CENTER);
		
		JPanel panel = new JPanel();
		GridBagConstraints gbc_panel = new GridBagConstraints();
		gbc_panel.fill = GridBagConstraints.BOTH;
		gbc_panel.gridx = 0;
		gbc_panel.gridy = 1;
		topPanel.add(panel, gbc_panel);
		panel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		JButton btnStep = new JButton("step");
		btnStep.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				stepToNextInstruction();
			}
		});
		panel.add(btnStep);
		
		JPanel codePanel = new JPanel();
		contentPane.add(codePanel, BorderLayout.CENTER);
		codePanel.setLayout(new BorderLayout(0, 0));
		
		JScrollPane scrollPane = new JScrollPane();
		codePanel.add(scrollPane);
		
		codeTable = new JTable();
		codeTable.setFillsViewportHeight(true);
		CodeTableModel codeModel = new CodeTableModel(cpu);
		cpu.addCpuListener(codeModel);
		codeTable.setModel(codeModel);
		codeTable.getColumnModel().getColumn(0).setCellRenderer(new BreakpointTableCellRenderer(codeTable));
		codeTable.getColumnModel().getColumn(1).setCellRenderer(new Hex16TableCellRenderer());
		codeTable.getColumnModel().getColumn(2).setCellRenderer(new Binary16TableCellRenderer());
		codeTable.getColumnModel().getColumn(3).setCellRenderer(new Hex16TableCellRenderer());
		codeTable.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					toggleBreakpointAtSelectedAddress();
				}
			}
		});
		scrollPane.setViewportView(codeTable);
		
		// ctrl-B toggles breakpoint
		codeTable.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
			.put(KeyStroke.getKeyStroke(KeyEvent.VK_B, InputEvent.CTRL_DOWN_MASK), "toggleBreakpoint");
		codeTable.getActionMap().put("toggleBreakpoint", new AbstractAction("toggleBreakpoint") {
			@Override
			public void actionPerformed(ActionEvent e) {
				toggleBreakpointAtSelectedAddress();
			}
		});
	}

	protected void toggleBreakpointAtSelectedAddress() {
		int selectedRow = codeTable.getSelectedRow();
		cpu.toggleAddressBreakpoint(selectedRow);
	}

	protected void selectAndLoadFile() {
		fc.setMultiSelectionEnabled(false);
		//TODO add FileFilter
		int result = fc.showOpenDialog(this);
		if (result == JFileChooser.APPROVE_OPTION) {
			try {
				loadDataFromFile(fc.getSelectedFile());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private void loadDataFromFile(File selectedFile) throws IOException {
		FileInputStream input = new FileInputStream(selectedFile);
		int addr = input.read() << 8 | input.read();
		
		cpu.loadData(addr, input);
		cpu.setPC(addr);
		scrollToPC();
	}

	private void scrollToPC() {
		int row = cpu.getPC();
		Rectangle rect = codeTable.getCellRect(row, 0, true);
		codeTable.scrollRectToVisible(rect);
	}

	protected void stepToNextInstruction() {
		cpu.step();
	}

	@Override
	public void registerChanged(CPU cpu, Register r, short value) {
	}

	@Override
	public void instructionExecuted(CPU cpu, ICommand cmd) {
		int row = cpu.getPC();
		codeTable.getSelectionModel().setSelectionInterval(row, row);
		scrollToPC();
	}

	@Override
	public void memoryChanged(CPU cpu, int addr, short value) {
	}

}
