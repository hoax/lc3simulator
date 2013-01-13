package de.nasskappe.lc3.sim.maschine.cmds;

import de.nasskappe.lc3.sim.maschine.Register;

public class LD implements ICommand {

	private Register dr;
	private short offset;
	
	@Override
	public void init(short code) throws IllegalOpcodeException {
		int drByte = (code & (7<<9)) >> 9;
		dr = Register.values()[drByte];
		
		offset = (short) (code & 0x1FF);
		if ((offset & 0x100) != 0) {
			offset |= 0xFF00;
		}
	}
	
	@Override
	public void execute() {
		// TODO Auto-generated method stub
		
	}

}
