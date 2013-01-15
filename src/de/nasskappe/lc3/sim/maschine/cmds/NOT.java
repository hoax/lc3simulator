package de.nasskappe.lc3.sim.maschine.cmds;

import de.nasskappe.lc3.sim.maschine.Register;

public class NOT implements ICommand {

	private Register dr;
	private Register sr;

	@Override
	public void init(short code) throws IllegalOpcodeException {
		int drByte = (code & (7 << 9)) >> 9;
		dr = Register.values()[drByte];
		
		int srByte = (code & (7 << 6)) >> 6;
		sr = Register.values()[srByte];
		
		if ((code & 0x3F) != 0x3F)
			throw new IllegalOpcodeException(code);
	}
	
	@Override
	public void execute() {
		// TODO Auto-generated method stub
		
	}

	public Register getSr() {
		return sr;
	}
	
	public Register getDr() {
		return dr;
	}
	
}
