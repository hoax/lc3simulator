package de.nasskappe.lc3.sim.maschine.cmds;

import de.nasskappe.lc3.sim.maschine.LC3;
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
	public void execute(LC3 lc3) {
		int currentPC = lc3.getPC();
		lc3.setRegister(Register.R7, (short) currentPC);
		
		int addr = lc3.getMemory().getValue(trap) & 0xFFFF;
		lc3.setPC(addr);
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
