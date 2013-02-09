package de.nasskappe.lc3.sim.maschine.cmds;

import de.nasskappe.lc3.sim.maschine.LC3;
import de.nasskappe.lc3.sim.maschine.Register;

public class JMP extends AbstractCommand {

	Register baseR = null;
	
	@Override
	public void init(short code) {
		super.init(code);
		
		int r = (code >> 6) & 0x7;
		baseR = Register.values()[r];
		
		if ((code & 0x0E3F) != 0) {
			setIllegal(true);
		}
	}
	
	@Override
	public void execute(LC3 lc3) {
		short addr = lc3.getRegister(baseR);
		lc3.setPC(addr);
	}

	@Override
	public String getASM() {
		return "JMP";
	}
	
	public Register getBaseR() {
		return baseR;
	}

	@Override
	public Object accept(ICommandVisitor visitor) {
		return visitor.visit(this);
	}

}
