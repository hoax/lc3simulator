package de.nasskappe.lc3.sim.maschine;

import de.nasskappe.lc3.sim.maschine.cmds.ICommand;

public interface ICPUListener {

	void registerChanged(CPU cpu, Register r, short value);
	
	void instructionExecuted(CPU cpu, ICommand cmd);

	void memoryChanged(CPU cpu, int addr, short value);
}
