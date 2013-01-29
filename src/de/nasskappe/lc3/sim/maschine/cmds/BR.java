package de.nasskappe.lc3.sim.maschine.cmds;

import de.nasskappe.lc3.sim.maschine.LC3;
import de.nasskappe.lc3.sim.maschine.Register;
import de.nasskappe.lc3.sim.maschine.Register.CC_Value;

public class BR extends AbstractCommand {

	private boolean n;
	private boolean z;
	private boolean p;
	private short pcOffset;

	@Override
	public void init(short code) {
		super.init(code);
		
		n = (code & (1<<11)) != 0;
		z = (code & (1<<10)) != 0;
		p = (code & (1<<9)) != 0;
		pcOffset = (short) (code & 0x01FF);
		if ((pcOffset & 0x100) != 0) {
			pcOffset |= 0xFF00;
		}
	}

	@Override
	public void execute(LC3 lc3) {
		CC_Value cc = lc3.getCC();
		if ((p && (cc == Register.CC_Value.P))
				|| (n && (cc == Register.CC_Value.N))
				|| (z && (cc == Register.CC_Value.Z))) {
			int pc = lc3.getPC();
			pc += pcOffset;
			lc3.setPC(pc);
		}
	}

	public boolean isN() {
		return n;
	}

	public boolean isZ() {
		return z;
	}

	public boolean isP() {
		return p;
	}

	public short getPCOffset() {
		return pcOffset;
	}

	@Override
	public String getASM() {
		return "BR";
	}
	
	@Override
	public Object accept(ICommandVisitor visitor) {
		return visitor.visit(this);
	}

}
