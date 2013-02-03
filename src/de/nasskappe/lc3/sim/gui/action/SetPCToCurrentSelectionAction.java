package de.nasskappe.lc3.sim.gui.action;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JTable;

import de.nasskappe.lc3.sim.maschine.ILC3Listener;
import de.nasskappe.lc3.sim.maschine.LC3;
import de.nasskappe.lc3.sim.maschine.Lc3State;
import de.nasskappe.lc3.sim.maschine.Register;
import de.nasskappe.lc3.sim.maschine.cmds.ICommand;

public class SetPCToCurrentSelectionAction extends AbstractAction implements ILC3Listener {

//	private Icon icon;
	private LC3 lc3;
	private JTable table;

	public SetPCToCurrentSelectionAction(LC3 lc3, JTable table) {
		this.lc3 = lc3;
		this.table = table;

//		icon = new ImageIcon(MainWindow.class.getResource("/de/nasskappe/lc3/sim/gui/icons/pause.gif"));

		putValue(NAME, "set PC to current selection");
		putValue(SHORT_DESCRIPTION, "set PC to current selection");
//		putValue(SMALL_ICON, icon);
		putValue(ACTION_COMMAND_KEY, "debugger_set_pc");
		putValue(MNEMONIC_KEY, (int)'C');
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		int row = table.getSelectedRow();
		if (row != -1) {
			lc3.setPC(row);
		}
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
