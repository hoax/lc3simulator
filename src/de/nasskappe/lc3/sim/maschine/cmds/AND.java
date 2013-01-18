package de.nasskappe.lc3.sim.maschine.cmds;

import de.nasskappe.lc3.sim.maschine.CPU;
import de.nasskappe.lc3.sim.maschine.Register;

public class AND implements ICommand {

	private Register dr;
	private Register sr1;
	private Register sr2; // if sr2 is null, imm has to be used.
	private short imm; // only used if sr2 is not null

	@Override
	public void init(short code) throws IllegalOpcodeException {
		int drByte = (code & 0x0E00) >> 9;
		dr = Register.values()[drByte];
		
		int sr1Byte = (code & 0x01C0) >> 6;
		sr1 = Register.values()[sr1Byte];
		
		int registerOrImm = (code & 0x0020) >> 5;
		if (registerOrImm == 0) {
			if ((code & 0x0018) != 0)
				throw new IllegalOpcodeException(code);
			
			imm = -1;
			int sr2Byte = (code & 0x0007);
			sr2 = Register.values()[sr2Byte];
		} else {
			imm = (short) (code & 0x001F);
			if ((code & 0x0010) != 0) {
				imm |= 0xFFF0;
			}
			sr2 = null;
		}
	}

	@Override
	public void execute(CPU cpu) {
		int val1 = cpu.getRegister(sr1);
		int val2 = imm;
		if (sr2 != null) {
			val2 = cpu.getRegister(sr2);
		}
		short result = (short) (val1 & val2);
		cpu.setRegister(dr, result);
		cpu.updateCC(result);
	}

	public Register getDr() {
		return dr;
	}
	
	public Register getSr1() {
		return sr1;
	}
	
	public Register getSr2() {
		return sr2;
	}
	
	public short getImm() {
		return imm;
	}

	@Override
	public String getASM() {
		return "AND";
	}
	
}
