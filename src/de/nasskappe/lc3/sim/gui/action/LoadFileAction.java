package de.nasskappe.lc3.sim.gui.action;

import java.awt.EventQueue;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;

import de.nasskappe.lc3.sim.gui.MainWindow;
import de.nasskappe.lc3.sim.maschine.ILC3Listener;
import de.nasskappe.lc3.sim.maschine.LC3;
import de.nasskappe.lc3.sim.maschine.Lc3State;
import de.nasskappe.lc3.sim.maschine.Register;
import de.nasskappe.lc3.sim.maschine.cmds.ICommand;

public class LoadFileAction extends AbstractAction implements ILC3Listener {

	private class PatternFileFilter extends javax.swing.filechooser.FileFilter {
		
		private String desc;
		private String pattern;

		public PatternFileFilter(String desc, String pattern) {
			this.desc = desc;
			this.pattern = pattern;
		}
		
		@Override
		public boolean accept(File pathname) {
			return pathname.isDirectory() 
					|| pathname.getName().matches(pattern);
		}

		@Override
		public String getDescription() {
			return desc;
		}
		
	}
	
	Preferences prefs;
	
	JFileChooser fc;
	Window window;
	private LC3 lc3;
	private Icon icon;
	
	public LoadFileAction(Window parentWindow, LC3 lc3) {
		prefs = Preferences.userNodeForPackage(MainWindow.class);
		String lastOpenDir = prefs.get("lastOpenDir", ".");
		
		PatternFileFilter objFilter = new PatternFileFilter("Object Files (*.obj)", "(?i).*\\.obj");
		PatternFileFilter allFilter = new PatternFileFilter("All Files (*.*)", ".*");
		
		fc = new JFileChooser(new File(lastOpenDir));
		fc.setAcceptAllFileFilterUsed(false);
		fc.addChoosableFileFilter(objFilter);
		fc.addChoosableFileFilter(allFilter);
		fc.setFileFilter(objFilter);
		
		window = parentWindow;
		this.lc3 = lc3;
		
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
				File file = fc.getSelectedFile();
				loadDataFromFile(file);
				lc3.getSymbolTable().addSymbolsFromFile(file);
				
				prefs.put("lastOpenDir", file.getParentFile().getAbsolutePath());
				prefs.sync();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (BackingStoreException e) {
			}
		}
	}

	private void loadDataFromFile(File selectedFile) throws IOException {
		FileInputStream input = new FileInputStream(selectedFile);
		int addr = input.read() << 8 | input.read();
		
		lc3.getUtils().loadData(addr, input);
		lc3.setPC(addr);
	}

	@Override
	public void registerChanged(LC3 lc3, Register r, short oldValue, short value) {
	}

	@Override
	public void instructionExecuted(LC3 lc3, ICommand cmd) {
	}

	@Override
	public void stateChanged(LC3 lc3, Lc3State oldState, final Lc3State newState) {
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				setEnabled(newState == Lc3State.STOPPED);
			}
		});
	}

	@Override
	public void breakpointChanged(LC3 lc3, int address, boolean set) {
	}
}
