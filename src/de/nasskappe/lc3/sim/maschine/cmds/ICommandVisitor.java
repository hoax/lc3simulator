package de.nasskappe.lc3.sim.maschine.cmds;

public interface ICommandVisitor {

	Object visit(ADD cmd);
	Object visit(AND cmd);
	Object visit(BR cmd);
	Object visit(JSR cmd);
	Object visit(LD cmd);
	Object visit(LDI cmd);
	Object visit(LDR cmd);
	Object visit(LEA cmd);
	Object visit(NOT cmd);
	Object visit(RET cmd);
	Object visit(RTI cmd);
	Object visit(ST cmd);
	Object visit(STI cmd);
	Object visit(STR cmd);
	Object visit(TRAP cmd);
	Object visit(Reserved cmd);

}
