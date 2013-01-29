package de.nasskappe.lc3.sim.gui;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.Font;
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
import java.lang.reflect.InvocationTargetException;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableCellRenderer;

import de.nasskappe.lc3.sim.gui.action.DebuggerRunAction;
import de.nasskappe.lc3.sim.gui.action.DebuggerStepIntoAction;
import de.nasskappe.lc3.sim.gui.action.DebuggerStepOverAction;
import de.nasskappe.lc3.sim.gui.action.DebuggerStepReturnAction;
import de.nasskappe.lc3.sim.gui.action.DebuggerStopAction;
import de.nasskappe.lc3.sim.gui.action.LoadFileAction;
import de.nasskappe.lc3.sim.gui.action.ShowConsoleAction;
import de.nasskappe.lc3.sim.gui.console.ConsoleWindow;
import de.nasskappe.lc3.sim.gui.editor.NumberCellEditor;
import de.nasskappe.lc3.sim.gui.renderer.ASMTableCellRenderer;
import de.nasskappe.lc3.sim.gui.renderer.Binary16TableCellRenderer;
import de.nasskappe.lc3.sim.gui.renderer.BreakpointTableCellRenderer;
import de.nasskappe.lc3.sim.gui.renderer.Hex16TableCellRenderer;
import de.nasskappe.lc3.sim.gui.renderer.LabelTableCellRenderer;
import de.nasskappe.lc3.sim.maschine.CPU;
import de.nasskappe.lc3.sim.maschine.CPU.State;
import de.nasskappe.lc3.sim.maschine.ICPUListener;
import de.nasskappe.lc3.sim.maschine.Register;
import de.nasskappe.lc3.sim.maschine.cmds.ICommand;

public class MainWindow extends JFrame implements ICPUListener {

	private JPanel contentPane;
	private JTable registerTable;
	private CPU cpu;
	private JTable codeTable;
	private LoadFileAction loadFileAction;
	private DebuggerRunAction runAction;
	private DebuggerStopAction stopAction;
	private DebuggerStepIntoAction stepIntoAction;
	private DebuggerStepOverAction stepOverAction;
	private DebuggerStepReturnAction stepReturnAction;
	private ShowConsoleAction showConsoleAction;
	
	private JNumberField currentValueField;
	private JButton btnGo;
	private JComboBox<String> currentAddressBox;
	private HexNumberComboBoxModel addressModel;
	private CpuUtils cpuUtils;
	private ConsoleWindow console;
	
	private Runnable scrollToPcRunnable = new Runnable() {
		@Override
		public void run() {
			if (EventQueue.isDispatchThread()) {
				scrollToPC();
			} else {
				try {
					EventQueue.invokeAndWait(this);
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	};
	
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
		cpuUtils = new CpuUtils(cpu);
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 484);
		
		console = new ConsoleWindow(this);
		console.setAlwaysOnTop(true);
		
		showConsoleAction = new ShowConsoleAction(console);
		loadFileAction = new LoadFileAction(this, cpu);
		runAction = new DebuggerRunAction(cpuUtils, scrollToPcRunnable);
		stopAction = new DebuggerStopAction(cpuUtils);
		stepIntoAction = new DebuggerStepIntoAction(cpuUtils, scrollToPcRunnable);
		stepOverAction = new DebuggerStepOverAction(cpuUtils, scrollToPcRunnable);
		stepReturnAction = new DebuggerStepReturnAction(cpuUtils, scrollToPcRunnable);
		
		cpu.addCpuListener(loadFileAction);
		cpu.addCpuListener(runAction);
		cpu.addCpuListener(stopAction);
		cpu.addCpuListener(stepIntoAction);
		cpu.addCpuListener(stepOverAction);
		cpu.addCpuListener(stepReturnAction);
		
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
		gbl_topPanel.rowHeights = new int[]{0, 0, 0, 0, 0};
		gbl_topPanel.columnWeights = new double[]{1.0, Double.MIN_VALUE};
		gbl_topPanel.rowWeights = new double[]{0.0, 1.0, 1.0, 1.0, Double.MIN_VALUE};
		topPanel.setLayout(gbl_topPanel);
		
		JToolBar toolBar = createToolbar();
		GridBagConstraints gbc_toolBar = new GridBagConstraints();
		gbc_toolBar.fill = GridBagConstraints.HORIZONTAL;
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
		
		JPanel addressPanel = createAddressPanel();
		GridBagConstraints gbc_addressPanel = new GridBagConstraints();
		gbc_addressPanel.insets = new Insets(0, 0, 5, 0);
		gbc_addressPanel.fill = GridBagConstraints.BOTH;
		gbc_addressPanel.gridx = 0;
		gbc_addressPanel.gridy = 2;
		topPanel.add(addressPanel, gbc_addressPanel);
		return topPanel;
	}
	
	private JPanel createAddressPanel() {
		JPanel panel = new JPanel();
		
		GridBagLayout gbl_addressPanel = new GridBagLayout();
		gbl_addressPanel.columnWidths = new int[]{0, 170, 0, 0};
		gbl_addressPanel.rowHeights = new int[]{0, 0, 0};
		gbl_addressPanel.columnWeights = new double[]{0.0, 0.0, 1.0, Double.MIN_VALUE};
		gbl_addressPanel.rowWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
		panel.setLayout(gbl_addressPanel);
		
		final JLabel lblCurrentAddress = new JLabel("address:");
		GridBagConstraints gbc_lblCurrentAddress = new GridBagConstraints();
		gbc_lblCurrentAddress.insets = new Insets(0, 0, 5, 5);
		gbc_lblCurrentAddress.anchor = GridBagConstraints.EAST;
		gbc_lblCurrentAddress.gridx = 0;
		gbc_lblCurrentAddress.gridy = 0;
		panel.add(lblCurrentAddress, gbc_lblCurrentAddress);
		
		addressModel = new HexNumberComboBoxModel();
		addressModel.addAddress("0x3000");
		currentAddressBox = new JComboBox<String>(addressModel);
		currentAddressBox.setEditable(true);
		GridBagConstraints gbc_currentAddressBox = new GridBagConstraints();
		gbc_currentAddressBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_currentAddressBox.insets = new Insets(0, 0, 5, 5);
		gbc_currentAddressBox.gridx = 1;
		gbc_currentAddressBox.gridy = 0;
		panel.add(currentAddressBox, gbc_currentAddressBox);
		
		btnGo = new JButton("go");
		btnGo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				addressModel.addAddress(currentAddressBox.getSelectedItem());
				goToAddress(currentAddressBox.getSelectedItem().toString());
			}
		});
		GridBagConstraints gbc_btnGo = new GridBagConstraints();
		gbc_btnGo.anchor = GridBagConstraints.WEST;
		gbc_btnGo.insets = new Insets(0, 0, 5, 0);
		gbc_btnGo.gridx = 2;
		gbc_btnGo.gridy = 0;
		panel.add(btnGo, gbc_btnGo);
		
		JLabel lblValue = new JLabel("value:");
		GridBagConstraints gbc_lblValue = new GridBagConstraints();
		gbc_lblValue.anchor = GridBagConstraints.EAST;
		gbc_lblValue.insets = new Insets(0, 0, 0, 5);
		gbc_lblValue.gridx = 0;
		gbc_lblValue.gridy = 1;
		panel.add(lblValue, gbc_lblValue);
		
		currentValueField = new JNumberField();
		currentValueField.setFont(new Font("Courier New", Font.PLAIN, currentValueField.getFont().getSize()));
		GridBagConstraints gbc_currentValueField = new GridBagConstraints();
		gbc_currentValueField.fill = GridBagConstraints.HORIZONTAL;
		gbc_currentValueField.insets = new Insets(0, 0, 0, 5);
		gbc_currentValueField.gridx = 1;
		gbc_currentValueField.gridy = 1;
		panel.add(currentValueField, gbc_currentValueField);
		currentValueField.setColumns(10);
		
		JButton btnSetValue = new JButton("set value");
		btnSetValue.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setCurrentValue();
			}
		});
		GridBagConstraints gbc_btnSetValue = new GridBagConstraints();
		gbc_btnSetValue.anchor = GridBagConstraints.WEST;
		gbc_btnSetValue.gridx = 2;
		gbc_btnSetValue.gridy = 1;
		panel.add(btnSetValue, gbc_btnSetValue);
		
		return panel;
	}

	protected void setCurrentValue() {
		try {
			Integer value = NumberUtils.stringToInt(currentValueField.getText());
			int row = codeTable.getSelectedRow();
			if (row >= 0) {
				cpu.writeMemory(row, value.shortValue());
			}
		} catch (Exception e) {
			JOptionPane.showMessageDialog(this, e.getMessage());
		}
	}

	protected void goToAddress(String addressString) {
		try {
			Integer address = NumberUtils.stringToInt(addressString);
			
			address = address & 0xffff;
			
			addressString = String.format("%04x", address);
			addressModel.addAddress(address);
			
			codeTable.getSelectionModel().setSelectionInterval(address, address);
			scrollTo(address);
		} catch (NumberFormatException e) {
			JOptionPane.showMessageDialog(this, e.getMessage());
			currentAddressBox.requestFocus();
			currentAddressBox.getEditor().selectAll();
		}
		
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
		
		createFileMenu(menuBar);
		createDebugMenu(menuBar);
		createWindowMenu(menuBar);

		return menuBar;
	}
	
	private void createWindowMenu(JMenuBar menuBar) {
		JMenu window = new JMenu("Window");
		menuBar.add(window);
		
		JMenuItem console = new JMenuItem(showConsoleAction);
		console.setAccelerator(KeyStroke.getKeyStroke("control alt C"));
		window.add(console);
	}
	
	private void createFileMenu(JMenuBar menuBar) {
		JMenu file = new JMenu("File");
		menuBar.add(file);
		
		JMenuItem loadFile = new JMenuItem(loadFileAction);
		loadFile.setAccelerator(KeyStroke.getKeyStroke("control O"));
		file.add(loadFile);
		
		JSeparator separator = new JSeparator();
		file.add(separator);
		
		JMenuItem exit = new JMenuItem("Exit");
		exit.setMnemonic('X');
		exit.setAccelerator(KeyStroke.getKeyStroke("control X"));
		exit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
			}
		});
		file.add(exit);
	}
	
	private void createDebugMenu(JMenuBar menuBar) {
		JMenu debugMenu = new JMenu("Debug");
		menuBar.add(debugMenu);
		
		JMenuItem runBtn = new JMenuItem(runAction);
		runBtn.setAccelerator(KeyStroke.getKeyStroke("F8"));
		debugMenu.add(runBtn);
		
		JMenuItem stopBtn = new JMenuItem(stopAction);
		stopBtn.setAccelerator(KeyStroke.getKeyStroke("F9"));
		debugMenu.add(stopBtn);
		
		JMenuItem stepIntoBtn = new JMenuItem(stepIntoAction);
		stepIntoBtn.setAccelerator(KeyStroke.getKeyStroke("F5"));
		debugMenu.add(stepIntoBtn);

		JMenuItem stepOverBtn = new JMenuItem(stepOverAction);
		stepOverBtn.setAccelerator(KeyStroke.getKeyStroke("F6"));
		debugMenu.add(stepOverBtn);

		JMenuItem stepReturnBtn = new JMenuItem(stepReturnAction);
		stepReturnBtn.setAccelerator(KeyStroke.getKeyStroke("F7"));
		debugMenu.add(stepReturnBtn);
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

		table.setDefaultEditor(Integer.class, new NumberCellEditor());
		
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
		table.getColumnModel().getColumn(4).setCellRenderer(new ASMTableCellRenderer(cpu));
		
		table.getColumnModel().getColumn(0).setResizable(false);
		table.getColumnModel().getColumn(0).setMaxWidth(22);
		table.getColumnModel().getColumn(0).setMinWidth(22);

		table.getColumnModel().getColumn(1).setResizable(false);
		table.getColumnModel().getColumn(1).setMaxWidth(80);
		table.getColumnModel().getColumn(1).setMinWidth(80);

		table.getColumnModel().getColumn(2).setResizable(false);
		table.getColumnModel().getColumn(2).setMaxWidth(150);
		table.getColumnModel().getColumn(2).setMinWidth(150);

		table.getColumnModel().getColumn(3).setResizable(false);
		table.getColumnModel().getColumn(3).setMaxWidth(60);
		table.getColumnModel().getColumn(3).setMinWidth(60);

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
		
		table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				if (!e.getValueIsAdjusting()) {
					updateAddressField();
					updateCurrentValueField();
				}
			}
		});

		return table;
	}
	
	private void updateAddressField() {
		int row = codeTable.getSelectedRow();
		addressModel.setSelectedItem(""+row);
	}

	private void updateCurrentValueField() {
		int row = codeTable.getSelectedRow();
		if (row != -1) {
			int value = ((int)cpu.readMemory(row)) & 0xffff;
			
			currentValueField.setNumber(value);
		}
	}

	JToolBar createToolbar() {
		JToolBar toolBar = new JToolBar();
		
		JButton btnOpenFile = new JButton();
		btnOpenFile.setHideActionText(true);
		btnOpenFile.setAction(loadFileAction);
		toolBar.add(btnOpenFile);
		
		JSeparator separator = new JSeparator();
		toolBar.add(separator);
		
		JButton btnRun = new JButton(runAction);
		btnRun.setHideActionText(true);
		toolBar.add(btnRun);
		
		JButton btnStop = new JButton(stopAction);
		btnStop.setHideActionText(true);
		toolBar.add(btnStop);
		
		JButton btnStepInto = new JButton(stepIntoAction);
		btnStepInto.setHideActionText(true);
		toolBar.add(btnStepInto);
		
		JButton btnStepOver = new JButton(stepOverAction);
		btnStepOver.setHideActionText(true);
		toolBar.add(btnStepOver);
		
		JButton btnStepReturn = new JButton(stepReturnAction);
		btnStepReturn.setHideActionText(true);
		toolBar.add(btnStepReturn);
		
		return toolBar;
	}

	protected void toggleBreakpointAtSelectedAddress() {
		int selectedRow = codeTable.getSelectedRow();
		cpu.toggleAddressBreakpoint(selectedRow);
	}

	private void scrollToPC() {
		int row = cpu.getPC();
		scrollTo(row);
	}
	
	private void scrollTo(int row) {
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
	}

	@Override
	public void memoryChanged(CPU cpu, int addr, short value) {
		if (addr == codeTable.getSelectedRow()) {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					updateCurrentValueField();
				}
			});
		}
	}

	@Override
	public void stateChanged(CPU cpu, State oldState, State newState) {
	}

}