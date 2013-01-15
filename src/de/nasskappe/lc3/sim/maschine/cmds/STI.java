package de.nasskappe.lc3.sim.maschine.cmds;

import de.nasskappe.lc3.sim.maschine.Register;

public class STI implements ICommand {

	private Register sr;
	private short pcOffset;

	@Override
	public void init(short code) throws IllegalOpcodeException {
		int drByte = (code & (7 << 9)) >> 9;
		sr = Register.values()[drByte];
		
		pcOffset = (short) (code & 0x1FF);
		if ((pcOffset & 0x100) != 0)
			pcOffset |= 0xFF00;
	}
	
	@Override
	public void execute() {
		// TODO Auto-generated method stub
		
	}

	public Register getSr() {
		return sr;
	}
	
	public short getPcOffset() {
		return pcOffset;
	}
}
