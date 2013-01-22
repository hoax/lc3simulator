package de.nasskappe.lc3.sim.maschine.cmds;

import de.nasskappe.lc3.sim.maschine.CPU;

public class TRAP extends AbstractCommand {

	private int trap;

	@Override
	public void init(short code) {
		super.init(code);
		
		if ((code & 0x0F00) != 0) {
			setIllegal(true);
		}
		
		trap = code & 0xFF;
	}
	
	@Override
	public void execute(CPU cpu) {
		// TODO
	}

	public int getTrap() {
		return trap;
	}

	@Override
	public String getASM() {
		return "TRAP";
	}
}
