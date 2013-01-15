package de.nasskappe.lc3.sim.maschine.cmds;

import de.nasskappe.lc3.sim.maschine.Register;

public class LDR implements ICommand {

	private Register dr;
	private Register baseR;
	private short offset;

	@Override
	public void init(short code) throws IllegalOpcodeException {
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
	public void execute() {
		// TODO Auto-generated method stub
		
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
	
}
