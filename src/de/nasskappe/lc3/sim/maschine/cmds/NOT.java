package de.nasskappe.lc3.sim.maschine.cmds;

import de.nasskappe.lc3.sim.maschine.CPU;
import de.nasskappe.lc3.sim.maschine.Register;

public class NOT extends AbstractCommand {

	private Register dr;
	private Register sr;

	@Override
	public void init(short code) {
		super.init(code);
		
		int drByte = (code & (7 << 9)) >> 9;
		dr = Register.values()[drByte];
		
		int srByte = (code & (7 << 6)) >> 6;
		sr = Register.values()[srByte];
		
		if ((code & 0x3F) != 0x3F) {
			setIllegal(true);
		}
	}
	
	@Override
	public void execute(CPU cpu) {
		int value = cpu.getRegister(sr);
		value = ~value;
		cpu.setRegister(dr, (short) value);
		cpu.updateCC((short)value);
	}

	public Register getSr() {
		return sr;
	}
	
	public Register getDr() {
		return dr;
	}

	@Override
	public String getASM() {
		return "NOT";
	}	@Override
	
	public Object accept(ICommandVisitor visitor) {
		return visitor.visit(this);
	}

}
