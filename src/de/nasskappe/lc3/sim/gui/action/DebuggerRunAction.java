package de.nasskappe.lc3.sim.gui.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.ImageIcon;

import de.nasskappe.lc3.sim.gui.CpuUtils;
import de.nasskappe.lc3.sim.gui.MainWindow;

public class DebuggerRunAction extends AbstractAction {

	private Icon icon;
	private CpuUtils utils;

	public DebuggerRunAction(CpuUtils utils) {
		this.utils = utils;

		icon = new ImageIcon(MainWindow.class.getResource("/de/nasskappe/lc3/sim/gui/icons/run.gif"));

		putValue(NAME, "run");
		putValue(SHORT_DESCRIPTION, "runs until next breakpoint or halt-trap is reached.");
		putValue(SMALL_ICON, icon);
		putValue(ACTION_COMMAND_KEY, "debugger_run");
		putValue(MNEMONIC_KEY, (int)'R');
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		utils.run();
	}

}
