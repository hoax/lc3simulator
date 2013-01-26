package de.nasskappe.lc3.sim.maschine.cmds;

import de.nasskappe.lc3.sim.maschine.CPU;

public class RTI extends AbstractCommand {

	@Override
	public void init(short code) {
		super.init(code);
		
		if ((code & 0x0FFF) != 0) {
			setIllegal(true);
		}
	}
	
	@Override
	public void execute(CPU cpu) {
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
