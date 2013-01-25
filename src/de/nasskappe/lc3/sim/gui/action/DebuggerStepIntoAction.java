package de.nasskappe.lc3.sim.gui.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.ImageIcon;

import de.nasskappe.lc3.sim.gui.MainWindow;
import de.nasskappe.lc3.sim.maschine.CPU;

public class DebuggerStepIntoAction extends AbstractAction {

	private CPU cpu;
	private Icon icon;

	public DebuggerStepIntoAction(CPU cpu) {
		this.cpu = cpu;

		icon = new ImageIcon(MainWindow.class.getResource("/de/nasskappe/lc3/sim/gui/icons/stepinto.gif"));

		putValue(NAME, "step into");
		putValue(SHORT_DESCRIPTION, "step into");
		putValue(SMALL_ICON, icon);
		putValue(ACTION_COMMAND_KEY, "debugger_step_into");
		putValue(MNEMONIC_KEY, (int)'N');
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		cpu.step();
	}

}
