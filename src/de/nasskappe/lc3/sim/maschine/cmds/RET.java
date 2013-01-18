package de.nasskappe.lc3.sim.maschine.cmds;

import de.nasskappe.lc3.sim.maschine.CPU;
import de.nasskappe.lc3.sim.maschine.Register;

public class RET implements ICommand {

	@Override
	public void init(short code) throws IllegalOpcodeException {
		if ((code & 0x0FFF) != 0x01C0)
			throw new IllegalOpcodeException(code);
	}
	
	@Override
	public void execute(CPU cpu) {
		short addr = cpu.getRegister(Register.R7);
		cpu.setPC(addr);
	}

	@Override
	public String getASM() {
		return "RET";
	}

}
