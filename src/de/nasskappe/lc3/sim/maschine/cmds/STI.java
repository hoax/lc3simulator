package de.nasskappe.lc3.sim.maschine.cmds;

import de.nasskappe.lc3.sim.maschine.LC3;
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
	public void execute(LC3 lc3) {
		int addr = lc3.getPC() + pcOffset;
		int addr2 = lc3.getMemory().getValue(addr) & 0xFFFF;
		short value = lc3.getRegister(sr);
		lc3.getMemory().setValue(addr2, value);
	}

	public Register getSr() {
		return sr;
	}
	
	public short getPCOffset() {
		return pcOffset;
	}

	@Override
	public String getASM() {
		return "STI";
	}
	
	@Override
	public Object accept(ICommandVisitor visitor) {
		return visitor.visit(this);
	}

}
