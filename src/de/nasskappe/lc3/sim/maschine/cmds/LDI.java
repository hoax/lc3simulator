package de.nasskappe.lc3.sim.maschine.cmds;

import de.nasskappe.lc3.sim.maschine.LC3;
import de.nasskappe.lc3.sim.maschine.Register;

public class LDI extends AbstractCommand {

	private Register dr;
	private short pcOffset;

	@Override
	public void init(short code)  {
		super.init(code);
		
		int drByte = (code & (7 << 9)) >> 9;
		dr = Register.values()[drByte];
		
		pcOffset = (short) (code & 0x1FF);
		if ((pcOffset & 0x100) != 0)
			pcOffset |= 0xFE00;
	}
	
	@Override
	public void execute(LC3 lc3) {
		int addr = lc3.getPC() + pcOffset;
		int addr2 = ((int)lc3.getMemory().getValue(addr)) & 0xFFFF;
		short value = lc3.getMemory().getValue(addr2);
		lc3.setRegister(dr, value);
		lc3.updateCC(value);
	}

	public Register getDr() {
		return dr;
	}
	
	public short getPcOffset() {
		return pcOffset;
	}

	@Override
	public String getASM() {
		return "LDI";
	}

	@Override
	public Object accept(ICommandVisitor visitor) {
		return visitor.visit(this);
	}

}
