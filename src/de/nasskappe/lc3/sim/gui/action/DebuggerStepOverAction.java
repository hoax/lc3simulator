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

public class DebuggerStepOverAction extends AbstractAction implements ICPUListener {

	private Icon icon;
	private CpuUtils utils;
	private Runnable postExecute;

	public DebuggerStepOverAction(CpuUtils utils, Runnable postExecute) {
		this.utils = utils;
		this.postExecute = postExecute;

		icon = new ImageIcon(MainWindow.class.getResource("/de/nasskappe/lc3/sim/gui/icons/stepover.gif"));

		putValue(NAME, "step over");
		putValue(SHORT_DESCRIPTION, "step over");
		putValue(SMALL_ICON, icon);
		putValue(ACTION_COMMAND_KEY, "debugger_step_over");
		putValue(MNEMONIC_KEY, (int)'V');
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		utils.stepOver(postExecute);
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
				setEnabled(newState == State.STOPPED);
			}
		});
	}

	@Override
	public void memoryRead(CPU cpu, int addr, short value) {
	}

}
