package de.nasskappe.lc3.sim.maschine.cmds;

import de.nasskappe.lc3.sim.maschine.CPU;
import de.nasskappe.lc3.sim.maschine.Register;

public class LEA implements ICommand {

	private Register dr;
	private short pcOffset;

	@Override
	public void init(short code) throws IllegalOpcodeException {
		int drByte = (code & (7 << 9)) >> 9;
		dr = Register.values()[drByte];
		
		pcOffset = (short) (code & 0x1FF);
		if ((pcOffset & 0x100) != 0)
			pcOffset |= 0xFF00;
	}
	
	@Override
	public void execute(CPU cpu) {
		int addr = cpu.getPC() + pcOffset;
		cpu.setRegister(dr, (short) addr);
		cpu.updateCC((short)addr);
	}
	
	public Register getDr() {
		return dr;
	}
	
	public short getPcOffset() {
		return pcOffset;
	}

}
