package de.nasskappe.lc3.sim.maschine.cmds;

import de.nasskappe.lc3.sim.maschine.CPU;

public class Reserved implements ICommand {

	@Override
	public void execute(CPU cpu) {
	}

	@Override
	public void init(short code) throws IllegalOpcodeException {
	}

	@Override
	public String getASM() {
		return "---";
	}

}
