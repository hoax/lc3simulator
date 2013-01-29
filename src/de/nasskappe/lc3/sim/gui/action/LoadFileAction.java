package de.nasskappe.lc3.sim.gui.action;

import java.awt.EventQueue;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;

import de.nasskappe.lc3.sim.gui.MainWindow;
import de.nasskappe.lc3.sim.maschine.CPU;
import de.nasskappe.lc3.sim.maschine.CPU.State;
import de.nasskappe.lc3.sim.maschine.ICPUListener;
import de.nasskappe.lc3.sim.maschine.Register;
import de.nasskappe.lc3.sim.maschine.cmds.ICommand;

public class LoadFileAction extends AbstractAction implements ICPUListener {

	JFileChooser fc;
	Window window;
	private CPU cpu;
	private Icon icon;
	
	public LoadFileAction(Window parentWindow, CPU cpu) {
		fc = new JFileChooser(new File("."));
		
		window = parentWindow;
		this.cpu = cpu;
		
		icon = new ImageIcon(MainWindow.class.getResource("/de/nasskappe/lc3/sim/gui/icons/open.gif"));
		
		putValue(NAME, "Load file...");
		putValue(SHORT_DESCRIPTION, "Load .obj file into memory of machine.");
		putValue(SMALL_ICON, icon);
		putValue(ACTION_COMMAND_KEY, "loadFile");
		putValue(MNEMONIC_KEY, (int)'O');
	}
	
	@Override
	public void actionPerformed(ActionEvent event) {
		fc.setMultiSelectionEnabled(false);
		//TODO add FileFilter
		int result = fc.showOpenDialog(window);
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
	}

	@Override
	public void registerChanged(CPU cpu, Register r, short oldValue, short value) {
	}

	@Override
	public void instructionExecuted(CPU cpu, ICommand cmd) {
	}

	@Override
	public void memoryChanged(CPU cpu, int addr, short value) {
	}

	@Override
	public void stateChanged(CPU cpu, State oldState, final State newState) {
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				setEnabled(newState == State.STOPPED);
			}
		});
	}

	@Override
	public void memoryRead(CPU cpu, int addr, short value) {
	}

}
