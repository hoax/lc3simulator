package de.nasskappe.lc3.sim.maschine.cmds;

import de.nasskappe.lc3.sim.maschine.CPU;
import de.nasskappe.lc3.sim.maschine.Register;

public class LD extends AbstractCommand {

	private Register dr;
	private short pcOffset;
	
	@Override
	public void init(short code) {
		super.init(code);
		
		int drByte = (code & (7<<9)) >> 9;
		dr = Register.values()[drByte];
		
		pcOffset = (short) (code & 0x1FF);
		if ((pcOffset & 0x100) != 0) {
			pcOffset |= 0xFF00;
		}
	}
	
	@Override
	public void execute(CPU cpu) {
		int addr = cpu.getPC() + pcOffset;
		short value = cpu.readMemory(addr);
		cpu.setRegister(dr, value);
		cpu.updateCC(value);
	}

	public Register getDr() {
		return dr;
	}
	
	public short getPCOffset() {
		return pcOffset;
	}

	@Override
	public String getASM() {
		return "LD";
	}

	@Override
	public Object accept(ICommandVisitor visitor) {
		return visitor.visit(this);
	}

}
