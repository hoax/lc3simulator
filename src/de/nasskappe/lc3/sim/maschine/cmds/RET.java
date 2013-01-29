package de.nasskappe.lc3.sim.maschine.cmds;

import de.nasskappe.lc3.sim.maschine.LC3;
import de.nasskappe.lc3.sim.maschine.Register;

public class RET extends AbstractCommand {

	@Override
	public void init(short code) {
		super.init(code);
		
		if ((code & 0x0FFF) != 0x01C0) {
			setIllegal(true);
		}
	}
	
	@Override
	public void execute(LC3 lc3) {
		short addr = lc3.getRegister(Register.R7);
		lc3.setPC(addr);
	}

	@Override
	public String getASM() {
		return "RET";
	}

	@Override
	public Object accept(ICommandVisitor visitor) {
		return visitor.visit(this);
	}

}
