package de.nasskappe.lc3.sim.maschine.cmds;

import de.nasskappe.lc3.sim.maschine.CPU;
import de.nasskappe.lc3.sim.maschine.Register;

public class RET extends AbstractCommand {

	@Override
	public void init(short code) {
		super.init(code);
		
		if ((code & 0x0FFF) != 0x01C0) {
			setIllegal(true);
		}
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
