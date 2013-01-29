package de.nasskappe.lc3.sim.maschine.cmds;

import de.nasskappe.lc3.sim.maschine.LC3;

public class RTI extends AbstractCommand {

	@Override
	public void init(short code) {
		super.init(code);
		
		if ((code & 0x0FFF) != 0) {
			setIllegal(true);
		}
	}
	
	@Override
	public void execute(LC3 lc3) {
		// TODO
	}

	@Override
	public String getASM() {
		return "RTI";
	}

	@Override
	public Object accept(ICommandVisitor visitor) {
		return visitor.visit(this);
	}

}
