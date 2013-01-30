package de.nasskappe.lc3.sim.maschine.cmds;

import de.nasskappe.lc3.sim.maschine.LC3;
import de.nasskappe.lc3.sim.maschine.Register;

public class RTI extends AbstractCommand {

	@Override
	public void init(short code) {
		super.init(code);
		
		if ((code & 0x0FFF) != 0) {
			setIllegal(true);
		}
	}
	
	@Override
	public void execute(LC3 lc3) {
		if (!lc3.getUtils().isSupervisor()) {
			// TODO exception!
		} else {
			short ssp = lc3.getRegister(Register.SSP);
			short oldPSR = lc3.getMemory().getValue(ssp++);
			short oldPC = lc3.getMemory().getValue(ssp++);
			lc3.setRegister(Register.R6, ssp);
			
			lc3.setRegister(Register.PC, oldPC);
			lc3.setRegister(Register.PSR, oldPSR);
			
			if (!lc3.getUtils().isSupervisor()) {
				short usp = lc3.getRegister(Register.USP);
				lc3.setRegister(Register.R6, usp);
			}
		}
	}

	@Override
	public String getASM() {
		return "RTI";
	}

	@Override
	public Object accept(ICommandVisitor visitor) {
		return visitor.visit(this);
	}

}
