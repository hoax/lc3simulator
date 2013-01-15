package de.nasskappe.lc3.sim.maschine.cmds;

import de.nasskappe.lc3.sim.maschine.Register;


public class JSR implements ICommand {
	
	private short offset;
	private Register baseR;

	@Override
	public void init(short code) throws IllegalOpcodeException {
		int bit11 = (code & (1<<11));
		if (bit11 == 0) {
			if ((code & (3 << 9)) != 0)
				throw new IllegalOpcodeException(code);
			
			if ((code & 0x3F) != 0)
				throw new IllegalOpcodeException(code);
			
			int baseRByte = (code & (7 << 6)) >> 6;
			baseR = Register.values()[baseRByte];
			offset = -1;
		} else {
			baseR = null;
			offset = (short) (code & 0x07FF);
			if ((offset & 0x0400) != 0) {
				offset |= 0xF800;
			}
		}
	}

	@Override
	public void execute() {
		// TODO Auto-generated method stub

	}

	public short getOffset() {
		return offset;
	}
	
	public Register getBaseR() {
		return baseR;
	}
}
