package de.nasskappe.lc3.sim.maschine.cmds;

import de.nasskappe.lc3.sim.maschine.LC3;

public class Reserved extends AbstractCommand {

	@Override
	public void execute(LC3 lc3) {
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
