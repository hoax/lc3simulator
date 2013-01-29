package de.nasskappe.lc3.sim.maschine.cmds;

import de.nasskappe.lc3.sim.maschine.LC3;
import de.nasskappe.lc3.sim.maschine.Register;

public class ST extends AbstractCommand {

	private Register sr;
	private short pcOffset;

	@Override
	public void init(short code) {
		super.init(code);
		
		int srByte = (code & (7<<9)) >> 9;
		sr = Register.values()[srByte];
		
		pcOffset = (short) (code & 0x1FF);
		if ((pcOffset & 0x100) != 0) {
			pcOffset |= 0xFE00;
		}
	}

	@Override
	public void execute(LC3 lc3) {
		int addr = lc3.getPC() + pcOffset;
		short value = lc3.getRegister(sr);
		lc3.writeMemory(addr, value);
	}

	public Register getSr() {
		return sr;
	}
	
	public short getPcOffset() {
		return pcOffset;
	}

	@Override
	public String getASM() {
		return "ST";
	}
	
	@Override
	public Object accept(ICommandVisitor visitor) {
		return visitor.visit(this);
	}

}
