package de.nasskappe.lc3.sim.gui;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.table.TableCellRenderer;

import de.nasskappe.lc3.sim.gui.action.LoadFileAction;
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
	private LoadFileAction loadFileAction;

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
		setTitle("Lc3 Simulator");
		
		cpu = new CPU();
		cpu.addCpuListener(this);
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		
		loadFileAction = new LoadFileAction(this, cpu);
		
		JMenuBar menuBar = createMenuBar();
		setJMenuBar(menuBar);
		
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
		createTopPanel();
		createMainPanel();
		
		scrollToPC();
		codeTable.requestFocus();
	}
	
	private JPanel createMainPanel() {
		JPanel panel = new JPanel();
		contentPane.add(panel, BorderLayout.CENTER);
		panel.setLayout(new BorderLayout(0, 0));
		
		JScrollPane scrollPane = new JScrollPane();
		panel.add(scrollPane);
		
		codeTable = createCodeTable();
		scrollPane.setViewportView(codeTable);
		
		return panel;
	}

	private JPanel createTopPanel() {
		JPanel topPanel = new JPanel();
		contentPane.add(topPanel, BorderLayout.NORTH);
		GridBagLayout gbl_topPanel = new GridBagLayout();
		gbl_topPanel.columnWidths = new int[]{0, 0};
		gbl_topPanel.rowHeights = new int[]{0, 0, 0, 0};
		gbl_topPanel.columnWeights = new double[]{1.0, Double.MIN_VALUE};
		gbl_topPanel.rowWeights = new double[]{0.0, 1.0, 1.0, Double.MIN_VALUE};
		topPanel.setLayout(gbl_topPanel);
		
		JToolBar toolBar = createToolbar();
		GridBagConstraints gbc_toolBar = new GridBagConstraints();
		gbc_toolBar.insets = new Insets(0, 0, 5, 0);
		gbc_toolBar.gridx = 0;
		gbc_toolBar.gridy = 0;
		topPanel.add(toolBar, gbc_toolBar);

		JPanel registerPanel = createRegisterPanel();
		GridBagConstraints gbc_registerPanel = new GridBagConstraints();
		gbc_registerPanel.insets = new Insets(0, 0, 5, 0);
		gbc_registerPanel.fill = GridBagConstraints.BOTH;
		gbc_registerPanel.gridx = 0;
		gbc_registerPanel.gridy = 1;
		topPanel.add(registerPanel, gbc_registerPanel);

		return topPanel;
	}

	private JPanel createRegisterPanel() {
		JPanel registerPanel = new JPanel();
		registerPanel.setLayout(new BorderLayout(0, 0));
		
		JLabel lblRegister = new JLabel("Register:");
		registerPanel.add(lblRegister, BorderLayout.NORTH);
		
		registerTable = createRegisterTable();
		registerPanel.add(registerTable, BorderLayout.CENTER);
		
		return registerPanel;
	}

	private JMenuBar createMenuBar() {
		JMenuBar menuBar = new JMenuBar();
		
		JMenu mnFile = new JMenu("File");
		menuBar.add(mnFile);
		
		JMenuItem mntmLoadobjFile = new JMenuItem("Load file ...");
		mntmLoadobjFile.setAction(loadFileAction);
		mnFile.add(mntmLoadobjFile);
		
		JSeparator separator = new JSeparator();
		mnFile.add(separator);
		
		JMenuItem mntmExit = new JMenuItem("Exit");
		mnFile.add(mntmExit);

		return menuBar;
	}

	private JTable createRegisterTable() {
		RegisterTableModel model = new RegisterTableModel();
		cpu.addCpuListener(model);
		model.registerChanged(cpu, Register.IR, (short) 0, (short) 0);

		JTable table = new JTable();
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.setRowSelectionAllowed(false);
		table.setFillsViewportHeight(true);
		table.setModel(model);
	
		TableCellRenderer cellLabelRenderer = new LabelTableCellRenderer();
		table.getColumnModel().getColumn(0).setCellRenderer(cellLabelRenderer);
		table.getColumnModel().getColumn(2).setCellRenderer(cellLabelRenderer);
		table.getColumnModel().getColumn(4).setCellRenderer(cellLabelRenderer);
		table.setDefaultRenderer(Integer.class, new Hex16TableCellRenderer(null));

		return table;
	}

	private JTable createCodeTable() {
		final JTable table = new JTable();
		table.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.setRowSelectionAllowed(true);
		table.setFillsViewportHeight(true);
		CodeTableModel codeModel = new CodeTableModel(cpu);
		cpu.addCpuListener(codeModel);
		table.setModel(codeModel);
		table.getColumnModel().getColumn(0).setCellRenderer(new BreakpointTableCellRenderer(cpu, table));
		table.getColumnModel().getColumn(1).setCellRenderer(new Hex16TableCellRenderer(cpu));
		table.getColumnModel().getColumn(2).setCellRenderer(new Binary16TableCellRenderer(cpu));
		table.getColumnModel().getColumn(3).setCellRenderer(new Hex16TableCellRenderer(cpu));
		table.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (SwingUtilities.isLeftMouseButton(e) && e.getClickCount() == 2) {
					toggleBreakpointAtSelectedAddress();
				}
				else if (SwingUtilities.isRightMouseButton(e) && e.getClickCount() == 2) {
					int row = table.rowAtPoint(e.getPoint());
					cpu.setPC(row);
				}
			}
		});	
		
		// ctrl-B toggles breakpoint
		table.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
			.put(KeyStroke.getKeyStroke(KeyEvent.VK_B, InputEvent.CTRL_DOWN_MASK), "toggleBreakpoint");
		table.getActionMap().put("toggleBreakpoint", new AbstractAction("toggleBreakpoint") {
			@Override
			public void actionPerformed(ActionEvent e) {
				toggleBreakpointAtSelectedAddress();
			}
		});

		return table;
	}

	JToolBar createToolbar() {
		JToolBar toolBar = new JToolBar();
		
		JButton btnOpenFile = new JButton();
		btnOpenFile.setHideActionText(true);
		btnOpenFile.setAction(loadFileAction);
		toolBar.add(btnOpenFile);
		
		JSeparator separator_1 = new JSeparator();
		toolBar.add(separator_1);
		
		JButton btnRun = new JButton();
		btnRun.setToolTipText("run");
		btnRun.setIcon(new ImageIcon(MainWindow.class.getResource("/de/nasskappe/lc3/sim/gui/icons/run.gif")));
		toolBar.add(btnRun);
		
		JButton btnStepInto = new JButton();
		btnStepInto.setToolTipText("step into");
		btnStepInto.setIcon(new ImageIcon(MainWindow.class.getResource("/de/nasskappe/lc3/sim/gui/icons/stepinto.gif")));
		toolBar.add(btnStepInto);
		
		JButton btnStepOver = new JButton();
		btnStepOver.setToolTipText("step over");
		btnStepOver.setIcon(new ImageIcon(MainWindow.class.getResource("/de/nasskappe/lc3/sim/gui/icons/stepover.gif")));
		toolBar.add(btnStepOver);
		
		JButton btnStepReturn = new JButton();
		btnStepReturn.setToolTipText("step return");
		btnStepReturn.setIcon(new ImageIcon(MainWindow.class.getResource("/de/nasskappe/lc3/sim/gui/icons/stepreturn.gif")));
		toolBar.add(btnStepReturn);
		
		return toolBar;
	}

	protected void toggleBreakpointAtSelectedAddress() {
		int selectedRow = codeTable.getSelectedRow();
		cpu.toggleAddressBreakpoint(selectedRow);
	}

	private void scrollToPC() {
		int row = cpu.getPC();
		Rectangle rect = codeTable.getCellRect(row, 0, true);
		rect.y = rect.y - 2* rect.height;
		rect.height = 5 * rect.height;
		codeTable.scrollRectToVisible(rect);
	}

	@Override
	public void registerChanged(CPU cpu, Register r, short oldValue, short value) {
	}

	@Override
	public void instructionExecuted(CPU cpu, ICommand cmd) {
		scrollToPC();
	}

	@Override
	public void memoryChanged(CPU cpu, int addr, short value) {
	}

}
