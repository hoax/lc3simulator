package de.nasskappe.lc3.sim.maschine.cmds;

import de.nasskappe.lc3.sim.maschine.CPU;

public class RTI implements ICommand {

	@Override
	public void init(short code) throws IllegalOpcodeException {
		if ((code & 0x0FFF) != 0)
			throw new IllegalOpcodeException(code);
	}
	
	@Override
	public void execute(CPU cpu) {
		// TODO
	}

	@Override
	public String getASM() {
		return "RTI";
	}

}
