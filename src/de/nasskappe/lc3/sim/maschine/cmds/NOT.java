package de.nasskappe.lc3.sim.maschine.cmds;

import de.nasskappe.lc3.sim.maschine.LC3;
import de.nasskappe.lc3.sim.maschine.Register;

public class NOT extends AbstractCommand {

	private Register dr;
	private Register sr;

	@Override
	public void init(short code) {
		super.init(code);
		
		int drByte = (code & (7 << 9)) >> 9;
		dr = Register.values()[drByte];
		
		int srByte = (code & (7 << 6)) >> 6;
		sr = Register.values()[srByte];
		
		if ((code & 0x3F) != 0x3F) {
			setIllegal(true);
		}
	}
	
	@Override
	public void execute(LC3 lc3) {
		int value = lc3.getRegister(sr);
		value = ~value;
		lc3.setRegister(dr, (short) value);
		lc3.updateCC((short)value);
	}

	public Register getSr() {
		return sr;
	}
	
	public Register getDr() {
		return dr;
	}

	@Override
	public String getASM() {
		return "NOT";
	}	@Override
	
	public Object accept(ICommandVisitor visitor) {
		return visitor.visit(this);
	}

}
