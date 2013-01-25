package de.nasskappe.lc3.sim.gui.action;

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

public class LoadFileAction extends AbstractAction {

	JFileChooser fc = new JFileChooser();
	Window window;
	private CPU cpu;
	private Icon icon;
	
	public LoadFileAction(Window parentWindow, CPU cpu) {
		window = parentWindow;
		this.cpu = cpu;
		
		icon = new ImageIcon(MainWindow.class.getResource("/de/nasskappe/lc3/sim/gui/icons/open.gif"));
		
		putValue(NAME, "Load file...");
		putValue(SHORT_DESCRIPTION, "Load .obj file into memory of machine.");
		putValue(SMALL_ICON, icon);
		putValue(ACTION_COMMAND_KEY, "loadFile");
		putValue(MNEMONIC_KEY, (int)'O');
		putValue("hideActionText", true);
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

}
