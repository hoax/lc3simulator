package de.nasskappe.lc3.sim.maschine.cmds;

import de.nasskappe.lc3.sim.maschine.Register;

public class LDI implements ICommand {

	private Register dr;
	private short pcOffset;

	@Override
	public void init(short code) throws IllegalOpcodeException {
		int drByte = (code & (7 << 9)) >> 9;
		dr = Register.values()[drByte];
		
		pcOffset = (short) (code & 0x1FF);
		if ((pcOffset & 0x100) != 0)
			pcOffset |= 0xFE00;
	}
	
	@Override
	public void execute() {
		// TODO Auto-generated method stub
		
	}

	public Register getDr() {
		return dr;
	}
	
	public short getPcOffset() {
		return pcOffset;
	}
}
