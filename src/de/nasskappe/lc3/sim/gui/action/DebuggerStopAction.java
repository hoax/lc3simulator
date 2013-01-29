package de.nasskappe.lc3.sim.gui.action;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.ImageIcon;

import de.nasskappe.lc3.sim.gui.CpuUtils;
import de.nasskappe.lc3.sim.gui.MainWindow;
import de.nasskappe.lc3.sim.maschine.CPU;
import de.nasskappe.lc3.sim.maschine.CPU.State;
import de.nasskappe.lc3.sim.maschine.ICPUListener;
import de.nasskappe.lc3.sim.maschine.Register;
import de.nasskappe.lc3.sim.maschine.cmds.ICommand;

public class DebuggerStopAction extends AbstractAction implements ICPUListener {

	private Icon icon;
	private CpuUtils utils;

	public DebuggerStopAction(CpuUtils utils) {
		this.utils = utils;

		icon = new ImageIcon(MainWindow.class.getResource("/de/nasskappe/lc3/sim/gui/icons/pause.gif"));

		putValue(NAME, "pause");
		putValue(SHORT_DESCRIPTION, "stops cpu");
		putValue(SMALL_ICON, icon);
		putValue(ACTION_COMMAND_KEY, "debugger_stop");
		putValue(MNEMONIC_KEY, (int)'P');
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		utils.pause();
	}

	@Override
	public void registerChanged(CPU cpu, Register r, short oldValue, short value) {
	}

	@Override
	public void instructionExecuted(CPU cpu, ICommand cmd) {
	}

	@Override
	public void memoryChanged(CPU cpu, int addr, short value) {
	}

	@Override
	public void stateChanged(CPU cpu, State oldState, final State newState) {
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				setEnabled(newState == State.RUNNING);
			}
		});
	}

	@Override
	public void memoryRead(CPU cpu, int addr, short value) {
	}

}
