package de.nasskappe.lc3.sim.maschine.cmds;

import de.nasskappe.lc3.sim.maschine.LC3;
import de.nasskappe.lc3.sim.maschine.Register;


public class JSR extends AbstractCommand {
	
	private short pcOffset;
	private Register baseR;

	@Override
	public void init(short code) {
		super.init(code);
		
		int bit11 = (code & (1<<11));
		if (bit11 == 0) {
			if ((code & (3 << 9)) != 0) {
				setIllegal(true);
			}
			if ((code & 0x3F) != 0) {
				setIllegal(true);
			}
			
			int baseRByte = (code & (7 << 6)) >> 6;
			baseR = Register.values()[baseRByte];
			pcOffset = -1;
		} else {
			baseR = null;
			pcOffset = (short) (code & 0x07FF);
			if ((pcOffset & 0x0400) != 0) {
				pcOffset |= 0xF800;
			}
		}
	}

	@Override
	public void execute(LC3 lc3) {
		int oldPC = lc3.getPC();
		lc3.setRegister(Register.R7, (short) oldPC);
		
		int pc;
		if (baseR != null) {
			pc = lc3.getRegister(baseR); 
		} else {
			pc = oldPC + pcOffset;
		}

		lc3.setPC(pc);
	}

	public short getPCOffset() {
		return pcOffset;
	}
	
	public Register getBaseR() {
		return baseR;
	}

	@Override
	public String getASM() {
		if (baseR == null) {
			return "JSR";
		} else {
			return "JSRR";
		}
	}

	@Override
	public Object accept(ICommandVisitor visitor) {
		return visitor.visit(this);
	}

}
