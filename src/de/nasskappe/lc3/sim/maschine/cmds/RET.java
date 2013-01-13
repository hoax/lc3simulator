package de.nasskappe.lc3.sim.maschine.cmds;

public class RET implements ICommand {

	@Override
	public void init(short code) throws IllegalOpcodeException {
		if ((code & 0x0FFF) != 0x01C0)
			throw new IllegalOpcodeException(code);
	}
	
	@Override
	public void execute() {
		// TODO Auto-generated method stub
		
	}

}
