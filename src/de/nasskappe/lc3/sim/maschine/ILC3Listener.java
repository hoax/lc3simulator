package de.nasskappe.lc3.sim.maschine;

import de.nasskappe.lc3.sim.maschine.cmds.ICommand;

public interface ILC3Listener {

	void registerChanged(LC3 lc3, Register r, short oldValue, short value);
	
	void instructionExecuted(LC3 lc3, ICommand cmd);

	void memoryChanged(LC3 lc3, int addr, short value);
	
	void memoryRead(LC3 lc3, int addr, short value);
	
	void stateChanged(LC3 lc3, LC3.State oldState, LC3.State newState);
}
