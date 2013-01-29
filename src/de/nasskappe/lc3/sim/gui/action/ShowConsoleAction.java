package de.nasskappe.lc3.sim.gui.action;

import java.awt.Window;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.ImageIcon;

import de.nasskappe.lc3.sim.gui.MainWindow;

public class ShowConsoleAction extends AbstractAction {

	Window window;
	private Icon icon;
	
	public ShowConsoleAction(Window window) {
		this.window = window;
		
		icon = new ImageIcon(MainWindow.class.getResource("/de/nasskappe/lc3/sim/gui/icons/console.gif"));
		
		putValue(NAME, "Show console...");
		putValue(SHORT_DESCRIPTION, "Open console window");
		putValue(SMALL_ICON, icon);
		putValue(ACTION_COMMAND_KEY, "openConsoleWindow");
		putValue(MNEMONIC_KEY, (int)'C');
	}
	
	@Override
	public void actionPerformed(ActionEvent event) {
		window.setVisible(true);
		window.requestFocus();
	}

}
