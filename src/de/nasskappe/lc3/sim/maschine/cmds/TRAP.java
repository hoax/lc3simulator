package de.nasskappe.lc3.sim.maschine.cmds;

import de.nasskappe.lc3.sim.maschine.CPU;

public class TRAP implements ICommand {

	private int trap;

	@Override
	public void init(short code) throws IllegalOpcodeException {
		if ((code & 0x0F00) != 0)
			throw new IllegalOpcodeException(code);
		
		trap = code & 0xFF;
	}
	
	@Override
	public void execute(CPU cpu) {
		// TODO
	}

	public int getTrap() {
		return trap;
	}
}
