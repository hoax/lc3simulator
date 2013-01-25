package de.nasskappe.lc3.sim.gui.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.ImageIcon;

import de.nasskappe.lc3.sim.gui.MainWindow;
import de.nasskappe.lc3.sim.maschine.CPU;

public class DebuggerStepOverAction extends AbstractAction {

	private CPU cpu;
	private Icon icon;

	public DebuggerStepOverAction(CPU cpu) {
		this.cpu = cpu;

		icon = new ImageIcon(MainWindow.class.getResource("/de/nasskappe/lc3/sim/gui/icons/stepover.gif"));

		putValue(NAME, "step over");
		putValue(SHORT_DESCRIPTION, "step over");
		putValue(SMALL_ICON, icon);
		putValue(ACTION_COMMAND_KEY, "debugger_step_over");
		putValue(MNEMONIC_KEY, (int)'V');
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		cpu.stepOver();
	}

}
