package de.nasskappe.lc3.sim.maschine.cmds;

import de.nasskappe.lc3.sim.maschine.CPU;

public interface ICommand {

	void execute(CPU cpu);

	void init(short code);

	String getASM();
	
	short getCode();
	
	Object accept(ICommandVisitor visitor);

}
