package de.nasskappe.lc3.sim.gui.action;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.ImageIcon;

import de.nasskappe.lc3.sim.gui.Lc3Utils;
import de.nasskappe.lc3.sim.gui.MainWindow;
import de.nasskappe.lc3.sim.maschine.LC3;
import de.nasskappe.lc3.sim.maschine.LC3.State;
import de.nasskappe.lc3.sim.maschine.ILC3Listener;
import de.nasskappe.lc3.sim.maschine.Register;
import de.nasskappe.lc3.sim.maschine.cmds.ICommand;

public class DebuggerStepIntoAction extends AbstractAction implements ILC3Listener {

	private Icon icon;
	private Lc3Utils utils;
	private Runnable postExecute;

	public DebuggerStepIntoAction(Lc3Utils utils, Runnable postExecute) {
		this.utils = utils;
		this.postExecute = postExecute;

		icon = new ImageIcon(MainWindow.class.getResource("/de/nasskappe/lc3/sim/gui/icons/stepinto.gif"));

		putValue(NAME, "step into");
		putValue(SHORT_DESCRIPTION, "step into");
		putValue(SMALL_ICON, icon);
		putValue(ACTION_COMMAND_KEY, "debugger_step_into");
		putValue(MNEMONIC_KEY, (int)'N');
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		utils.step(postExecute);
	}

	@Override
	public void registerChanged(LC3 lc3, Register r, short oldValue, short value) {
	}

	@Override
	public void instructionExecuted(LC3 lc3, ICommand cmd) {
	}

	@Override
	public void memoryChanged(LC3 lc3, int addr, short value) {
	}

	@Override
	public void stateChanged(LC3 lc3, State oldState, final State newState) {
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				setEnabled(newState == State.STOPPED);
			}
		});
	}

	@Override
	public void memoryRead(LC3 lc3, int addr, short value) {
	}

}
