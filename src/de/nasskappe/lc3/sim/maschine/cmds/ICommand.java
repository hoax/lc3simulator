package de.nasskappe.lc3.sim.maschine.cmds;

import de.nasskappe.lc3.sim.maschine.LC3;

public interface ICommand {

	void execute(LC3 lc3);

	void init(short code);

	String getASM();
	
	short getCode();
	
	Object accept(ICommandVisitor visitor);
	
	int getCodePosition();
	void setCodePosition(int codePosition);
	
	boolean isIllegal();
}
