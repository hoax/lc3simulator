package de.nasskappe.lc3.sim.gui.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.ImageIcon;

import de.nasskappe.lc3.sim.gui.CpuUtils;
import de.nasskappe.lc3.sim.gui.MainWindow;

public class DebuggerStepOverAction extends AbstractAction {

	private Icon icon;
	private CpuUtils utils;

	public DebuggerStepOverAction(CpuUtils utils) {
		this.utils = utils;

		icon = new ImageIcon(MainWindow.class.getResource("/de/nasskappe/lc3/sim/gui/icons/stepover.gif"));

		putValue(NAME, "step over");
		putValue(SHORT_DESCRIPTION, "step over");
		putValue(SMALL_ICON, icon);
		putValue(ACTION_COMMAND_KEY, "debugger_step_over");
		putValue(MNEMONIC_KEY, (int)'V');
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		utils.stepOver();
	}

}
