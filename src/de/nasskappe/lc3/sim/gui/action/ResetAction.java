package de.nasskappe.lc3.sim.gui.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;

import de.nasskappe.lc3.sim.gui.MainWindow;
import de.nasskappe.lc3.sim.maschine.CPU;

public class ResetAction extends AbstractAction {

	private CPU cpu;


	public ResetAction(CPU cpu) {

		this.cpu = cpu;
		ImageIcon icon = new ImageIcon(MainWindow.class.getResource("/de/nasskappe/lc3/sim/gui/icons/pause.gif"));

		putValue(NAME, "reset");
		putValue(SHORT_DESCRIPTION, "resets the cpus memory & register values");
		putValue(SMALL_ICON, icon);
		putValue(ACTION_COMMAND_KEY, "reset");
		putValue(MNEMONIC_KEY, (int)'s');
	}

	
	@Override
	public void actionPerformed(ActionEvent e) {
		cpu.reset();
	}

}
