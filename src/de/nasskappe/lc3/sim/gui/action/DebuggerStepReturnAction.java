package de.nasskappe.lc3.sim.gui.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.ImageIcon;

import de.nasskappe.lc3.sim.gui.CpuUtils;
import de.nasskappe.lc3.sim.gui.MainWindow;

public class DebuggerStepReturnAction extends AbstractAction {

	private Icon icon;
	private CpuUtils utils;

	public DebuggerStepReturnAction(CpuUtils utils) {
		this.utils = utils;

		icon = new ImageIcon(MainWindow.class.getResource("/de/nasskappe/lc3/sim/gui/icons/stepreturn.gif"));

		putValue(NAME, "step return");
		putValue(SHORT_DESCRIPTION, "step return");
		putValue(SMALL_ICON, icon);
		putValue(ACTION_COMMAND_KEY, "debugger_step_return");
		putValue(MNEMONIC_KEY, (int)'E');
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		utils.stepReturn();
	}

}
