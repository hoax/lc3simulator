package de.nasskappe.lc3.sim.maschine.cmds;

import de.nasskappe.lc3.sim.maschine.CPU;

public class Reserved extends AbstractCommand {

	@Override
	public void execute(CPU cpu) {
	}

	@Override
	public String getASM() {
		return "---";
	}

	@Override
	public Object accept(ICommandVisitor visitor) {
		return visitor.visit(this);
	}

}
