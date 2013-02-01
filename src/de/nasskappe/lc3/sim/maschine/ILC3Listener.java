package de.nasskappe.lc3.sim.maschine;

import de.nasskappe.lc3.sim.maschine.cmds.ICommand;

public interface ILC3Listener {

	void registerChanged(LC3 lc3, Register r, short oldValue, short value);
	
	void instructionExecuted(LC3 lc3, ICommand cmd);

	void stateChanged(LC3 lc3, Lc3State oldState, Lc3State newState);

	void breakpointChanged(LC3 lc3, int address, boolean set);
}
