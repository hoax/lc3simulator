package de.nasskappe.lc3.sim.maschine.cmds;

public class RTI implements ICommand {

	@Override
	public void init(short code) throws IllegalOpcodeException {
		if ((code & 0x0FFF) != 0)
			throw new IllegalOpcodeException(code);
	}
	
	@Override
	public void execute() {
		// TODO Auto-generated method stub
		
	}

}
