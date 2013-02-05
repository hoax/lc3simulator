package de.nasskappe.lc3.sim.gui;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableCellRenderer;

import de.nasskappe.lc3.sim.gui.action.DebuggerRunAction;
import de.nasskappe.lc3.sim.gui.action.DebuggerStepIntoAction;
import de.nasskappe.lc3.sim.gui.action.DebuggerStepOverAction;
import de.nasskappe.lc3.sim.gui.action.DebuggerStepReturnAction;
import de.nasskappe.lc3.sim.gui.action.DebuggerStopAction;
import de.nasskappe.lc3.sim.gui.action.LoadFileAction;
import de.nasskappe.lc3.sim.gui.action.ResetAction;
import de.nasskappe.lc3.sim.gui.action.SetPCToCurrentSelectionAction;
import de.nasskappe.lc3.sim.gui.action.ShowConsoleAction;
import de.nasskappe.lc3.sim.gui.console.ConsoleWindow;
import de.nasskappe.lc3.sim.gui.editor.NumberCellEditor;
import de.nasskappe.lc3.sim.gui.renderer.Hex16TableCellRenderer;
import de.nasskappe.lc3.sim.gui.renderer.LabelTableCellRenderer;
import de.nasskappe.lc3.sim.maschine.ILC3Listener;
import de.nasskappe.lc3.sim.maschine.LC3;
import de.nasskappe.lc3.sim.maschine.Lc3State;
import de.nasskappe.lc3.sim.maschine.Register;
import de.nasskappe.lc3.sim.maschine.cmds.ICommand;

public class MainWindow extends JFrame implements ILC3Listener {

	private JPanel contentPane;
	private JTable registerTable;
	private LC3 lc3;
	private CodePanel codePanel;
	private LoadFileAction loadFileAction;
	private DebuggerRunAction runAction;
	private DebuggerStopAction stopAction;
	private DebuggerStepIntoAction stepIntoAction;
	private DebuggerStepOverAction stepOverAction;
	private DebuggerStepReturnAction stepReturnAction;
	private ShowConsoleAction showConsoleAction;
	private SetPCToCurrentSelectionAction setPcToCurrentSelectionAction;
	private ResetAction resetAction;
	
	private JButton btnGo;
	private JComboBox goToAddressBox;
	private HexNumberComboBoxModel addressModel;
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
	private AddressValueDisplay numberDisplay;
	
	/**
	 * Create the frame.
	 */
	public MainWindow() {
		updateTitleWithVersion();
		
		lc3 = new LC3();
		lc3.addListener(this);
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 484);
		
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
		createMainPanel();
		
		console = createConsole();
		
		showConsoleAction = new ShowConsoleAction(console);
		loadFileAction = new LoadFileAction(this, lc3);
		resetAction = new ResetAction(lc3);
		runAction = new DebuggerRunAction(lc3.getUtils(), scrollToPcRunnable);
		stopAction = new DebuggerStopAction(lc3.getUtils());
		stepIntoAction = new DebuggerStepIntoAction(lc3.getUtils(), scrollToPcRunnable);
		stepOverAction = new DebuggerStepOverAction(lc3.getUtils(), scrollToPcRunnable);
		stepReturnAction = new DebuggerStepReturnAction(lc3.getUtils(), scrollToPcRunnable);
		setPcToCurrentSelectionAction = new SetPCToCurrentSelectionAction(lc3, codePanel.getTable());
		
		lc3.addListener(loadFileAction);
		lc3.addListener(runAction);
		lc3.addListener(stopAction);
		lc3.addListener(stepIntoAction);
		lc3.addListener(stepOverAction);
		lc3.addListener(stepReturnAction);
		
		JMenuBar menuBar = createMenuBar();
		setJMenuBar(menuBar);
				
		createTopPanel();

		scrollToPC();

		codePanel.setSelectedRow(0x3000);
		
		lc3.reset();
	}
	
	private void updateTitleWithVersion() {
		setTitle("LC3 Simulator");

		URL url = getClass().getProtectionDomain().getCodeSource().getLocation();
		try {
			String file = url.getFile();
			JarFile jf = new JarFile(file);
			Manifest mf = jf.getManifest();
			
			String version = mf.getMainAttributes().getValue("SCM-Revision");
			setTitle(getTitle() + " - " + version);
		} catch (IOException e) {
		}
	}

	private ConsoleWindow createConsole() {
		ConsoleWindow console = new ConsoleWindow(this, lc3);
		console.setAlwaysOnTop(true);
				
		return console;
	}
	
	private JPanel createMainPanel() {
		JPanel panel = new JPanel();
		contentPane.add(panel, BorderLayout.CENTER);
		panel.setLayout(new BorderLayout(0, 0));
		
		numberDisplay = new AddressValueDisplay();
		panel.add(numberDisplay, BorderLayout.NORTH);
		
		codePanel = createCodeTable();
		panel.add(codePanel, BorderLayout.CENTER);
		
		codePanel.getTable().getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				if (!e.getValueIsAdjusting()) {
					updateNumberDisplay();
				}
			}
		});
		
		codePanel.getTableModel().addTableModelListener(new TableModelListener() {
			@Override
			public void tableChanged(TableModelEvent e) {
				int row = codePanel.getTable().getSelectedRow();
				
				if (e.getFirstRow() >= row && e.getLastRow() <= row) {
					updateNumberDisplay();
				}
			}
		});
		
		return panel;
	}
	
	private void updateNumberDisplay() {
		int row = codePanel.getTable().getSelectedRow();
		if (row != -1) {
			Number n = (Number) codePanel.getTableModel().getValueAt(row, 2);
			numberDisplay.setNumber(row, n.shortValue());
		}
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
		goToAddressBox = new JComboBox(addressModel);
		goToAddressBox.setEditable(true);
		GridBagConstraints gbc_currentAddressBox = new GridBagConstraints();
		gbc_currentAddressBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_currentAddressBox.insets = new Insets(0, 0, 5, 5);
		gbc_currentAddressBox.gridx = 1;
		gbc_currentAddressBox.gridy = 0;
		panel.add(goToAddressBox, gbc_currentAddressBox);
		
		ActionListener goToAddressAction = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (goToAddress(goToAddressBox.getSelectedItem().toString())) {
					addressModel.addAddress(goToAddressBox.getSelectedItem());
				}
			}
		};

		goToAddressBox.addActionListener(goToAddressAction);
		
		btnGo = new JButton("go");
		btnGo.addActionListener(goToAddressAction);
		GridBagConstraints gbc_btnGo = new GridBagConstraints();
		gbc_btnGo.anchor = GridBagConstraints.WEST;
		gbc_btnGo.insets = new Insets(0, 0, 5, 0);
		gbc_btnGo.gridx = 2;
		gbc_btnGo.gridy = 0;
		panel.add(btnGo, gbc_btnGo);
		
		return panel;
	}

	protected boolean goToAddress(String addressString) {
		try {
			Integer address = lc3.getSymbolTable().findAddressBySymbol(addressString);
			if (address == null) {
				address = NumberUtils.stringToInt(addressString);
			}
			address = address & 0xffff;
			
			addressModel.addAddress(addressString);
			
			codePanel.setSelectedRow(address);
			codePanel.scrollTo(address);
			codePanel.requestFocus();
			
			return true;
		} catch (NumberFormatException e) {
			JOptionPane.showMessageDialog(this, e.getMessage());
			goToAddressBox.requestFocus();
			goToAddressBox.getEditor().selectAll();
			
			return false;
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
		
		JMenuItem reset = new JMenuItem(resetAction);
		reset.setAccelerator(KeyStroke.getKeyStroke("control R"));
		file.add(reset);
		
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

		debugMenu.add(new JSeparator());
		
		JMenuItem setPcBtn = new JMenuItem(setPcToCurrentSelectionAction);
		setPcBtn.setAccelerator(KeyStroke.getKeyStroke("F10"));
		debugMenu.add(setPcBtn);
	}

	private JTable createRegisterTable() {
		RegisterTableModel model = new RegisterTableModel(lc3);
		lc3.addListener(model);
		model.registerChanged(lc3, Register.IR, (short) 0, (short) 0);

		final JTable table = new JTable();
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
		table.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (SwingUtilities.isLeftMouseButton(e) && e.getClickCount() == 2) {
					System.out.println(table.columnAtPoint(e.getPoint()));
					if (table.columnAtPoint(e.getPoint()) == 4
							&& table.rowAtPoint(e.getPoint()) == 1) {
						scrollToPC();
					}
				}
			}
		});
		return table;
	}

	private CodePanel createCodeTable() {
		CodePanel panel = new CodePanel(lc3);
		return panel;
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

	private void scrollToPC() {
		int row = lc3.getPC();
		codePanel.scrollTo(row);
	}
	
	@Override
	public void registerChanged(LC3 lc3, Register r, short oldValue, short value) {
	}

	@Override
	public void instructionExecuted(LC3 lc3, ICommand cmd) {
	}

	@Override
	public void stateChanged(LC3 lc3, Lc3State oldState, Lc3State newState) {
	}

	@Override
	public void breakpointChanged(LC3 lc3, int address, boolean set) {
	}

}