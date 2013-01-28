package de.nasskappe.lc3.sim.gui.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.ImageIcon;

import de.nasskappe.lc3.sim.gui.CpuUtils;
import de.nasskappe.lc3.sim.gui.MainWindow;

public class DebuggerStepIntoAction extends AbstractAction {

	private Icon icon;
	private CpuUtils utils;

	public DebuggerStepIntoAction(CpuUtils utils) {
		this.utils = utils;

		icon = new ImageIcon(MainWindow.class.getResource("/de/nasskappe/lc3/sim/gui/icons/stepinto.gif"));

		putValue(NAME, "step into");
		putValue(SHORT_DESCRIPTION, "step into");
		putValue(SMALL_ICON, icon);
		putValue(ACTION_COMMAND_KEY, "debugger_step_into");
		putValue(MNEMONIC_KEY, (int)'N');
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		utils.step();
	}

}
