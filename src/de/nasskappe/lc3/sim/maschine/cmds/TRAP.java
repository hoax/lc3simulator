package de.nasskappe.lc3.sim.maschine.cmds;

import de.nasskappe.lc3.sim.maschine.CPU;
import de.nasskappe.lc3.sim.maschine.Register;

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
		int currentPC = cpu.getPC();
		cpu.setRegister(Register.R7, (short) currentPC);
		
		int addr = cpu.readMemory(trap) & 0xFFFF;
		cpu.setPC(addr);
	}

	public int getTrap() {
		return trap;
	}

	@Override
	public String getASM() {
		return "TRAP";
	}

	@Override
	public Object accept(ICommandVisitor visitor) {
		return visitor.visit(this);
	}

}
