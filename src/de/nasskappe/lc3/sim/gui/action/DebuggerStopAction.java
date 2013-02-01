package de.nasskappe.lc3.sim.gui.action;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.ImageIcon;

import de.nasskappe.lc3.sim.gui.Lc3Utils;
import de.nasskappe.lc3.sim.gui.MainWindow;
import de.nasskappe.lc3.sim.maschine.ILC3Listener;
import de.nasskappe.lc3.sim.maschine.LC3;
import de.nasskappe.lc3.sim.maschine.Lc3State;
import de.nasskappe.lc3.sim.maschine.Register;
import de.nasskappe.lc3.sim.maschine.cmds.ICommand;

public class DebuggerStopAction extends AbstractAction implements ILC3Listener {

	private Icon icon;
	private Lc3Utils utils;

	public DebuggerStopAction(Lc3Utils utils) {
		this.utils = utils;

		icon = new ImageIcon(MainWindow.class.getResource("/de/nasskappe/lc3/sim/gui/icons/pause.gif"));

		putValue(NAME, "pause");
		putValue(SHORT_DESCRIPTION, "stops lc3");
		putValue(SMALL_ICON, icon);
		putValue(ACTION_COMMAND_KEY, "debugger_stop");
		putValue(MNEMONIC_KEY, (int)'P');
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		utils.pause();
	}

	@Override
	public void registerChanged(LC3 lc3, Register r, short oldValue, short value) {
	}

	@Override
	public void instructionExecuted(LC3 lc3, ICommand cmd) {
	}

	@Override
	public void stateChanged(LC3 lc3, Lc3State oldState, final Lc3State newState) {
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				setEnabled(newState == Lc3State.RUNNING);
			}
		});
	}

	@Override
	public void breakpointChanged(LC3 lc3, int address, boolean set) {
	}
}
