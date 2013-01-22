package de.nasskappe.lc3.sim.maschine.cmds;

import de.nasskappe.lc3.sim.maschine.CPU;
import de.nasskappe.lc3.sim.maschine.Register;

public class STI extends AbstractCommand {

	private Register sr;
	private short pcOffset;

	@Override
	public void init(short code) {
		super.init(code);
		
		int drByte = (code & (7 << 9)) >> 9;
		sr = Register.values()[drByte];
		
		pcOffset = (short) (code & 0x1FF);
		if ((pcOffset & 0x100) != 0)
			pcOffset |= 0xFF00;
	}
	
	@Override
	public void execute(CPU cpu) {
		int addr = cpu.getPC() + pcOffset;
		int addr2 = cpu.readMemory(addr) & 0xFFFF;
		short value = cpu.getRegister(sr);
		cpu.writeMemory(addr2, value);
	}

	public Register getSr() {
		return sr;
	}
	
	public short getPcOffset() {
		return pcOffset;
	}

	@Override
	public String getASM() {
		return "STI";
	}
}
