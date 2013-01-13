package de.nasskappe.lc3.sim.maschine.cmds;

public interface ICommand {

	void execute();

	void init(short code) throws IllegalOpcodeException;
	
}
