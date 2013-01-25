package de.nasskappe.lc3.sim.gui.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.ImageIcon;

import de.nasskappe.lc3.sim.gui.MainWindow;
import de.nasskappe.lc3.sim.maschine.CPU;

public class DebuggerStepReturnAction extends AbstractAction {

	private CPU cpu;
	private Icon icon;

	public DebuggerStepReturnAction(CPU cpu) {
		this.cpu = cpu;

		icon = new ImageIcon(MainWindow.class.getResource("/de/nasskappe/lc3/sim/gui/icons/stepreturn.gif"));

		putValue(NAME, "step return");
		putValue(SHORT_DESCRIPTION, "step return");
		putValue(SMALL_ICON, icon);
		putValue(ACTION_COMMAND_KEY, "debugger_step_return");
		putValue(MNEMONIC_KEY, (int)'E');
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		cpu.stepReturn();
	}

}
