package de.nasskappe.lc3.sim.maschine.cmds;

import de.nasskappe.lc3.sim.maschine.LC3;
import de.nasskappe.lc3.sim.maschine.Register;

public class LDR extends AbstractCommand {

	private Register dr;
	private Register baseR;
	private short offset;

	@Override
	public void init(short code) {
		super.init(code);
		
		int drByte = (code & (7 << 9)) >> 9;
		dr = Register.values()[drByte];
		
		int baseRbyte = (code & (7 << 6)) >> 6;
		baseR = Register.values()[baseRbyte];
		
		offset = (short) (code & 0x003F);
		if ((offset & 0x20) != 0) {
			offset |= 0xFFC0;
		}
	}

	@Override
	public void execute(LC3 lc3) {
		int addr = lc3.getRegister(baseR) + offset;
		short value = lc3.getMemory().getValue(addr);
		lc3.setRegister(dr, value);
		lc3.updateCC(value);
	}

	public Register getDr() {
		return dr;
	}
	
	public Register getBaseR() {
		return baseR;
	}
	
	public short getOffset() {
		return offset;
	}

	@Override
	public String getASM() {
		return "LDR";
	}
	
	@Override
	public Object accept(ICommandVisitor visitor) {
		return visitor.visit(this);
	}

}
