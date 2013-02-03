package de.nasskappe.lc3.sim.gui.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;

import de.nasskappe.lc3.sim.gui.MainWindow;
import de.nasskappe.lc3.sim.maschine.LC3;

public class ResetAction extends AbstractAction {

	private LC3 lc3;

	public ResetAction(LC3 lc3) {

		this.lc3 = lc3;
		
		ImageIcon icon = new ImageIcon(MainWindow.class.getResource("/de/nasskappe/lc3/sim/gui/icons/reset.gif"));

		putValue(NAME, "reset");
		putValue(SHORT_DESCRIPTION, "resets the lc3s memory & register values");
		putValue(SMALL_ICON, icon);
		putValue(ACTION_COMMAND_KEY, "reset");
		putValue(MNEMONIC_KEY, (int)'s');
	}

	
	@Override
	public void actionPerformed(ActionEvent e) {
		lc3.reset();
	}

}
