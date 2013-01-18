package de.nasskappe.lc3.sim.maschine.cmds;

import de.nasskappe.lc3.sim.maschine.CPU;
import de.nasskappe.lc3.sim.maschine.Register;


public class JSR implements ICommand {
	
	private short pcOffset;
	private Register baseR;

	@Override
	public void init(short code) throws IllegalOpcodeException {
		int bit11 = (code & (1<<11));
		if (bit11 == 0) {
			if ((code & (3 << 9)) != 0)
				throw new IllegalOpcodeException(code);
			
			if ((code & 0x3F) != 0)
				throw new IllegalOpcodeException(code);
			
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
	public void execute(CPU cpu) {
		int oldPC = cpu.getPC();
		cpu.setRegister(Register.R7, (short) oldPC);
		
		int pc;
		if (baseR != null) {
			pc = cpu.getRegister(baseR); 
		} else {
			pc = oldPC + pcOffset;
		}

		cpu.setPC(pc);
	}

	public short getPCOffset() {
		return pcOffset;
	}
	
	public Register getBaseR() {
		return baseR;
	}

	@Override
	public String getASM() {
		return "JSR";
	}
}
