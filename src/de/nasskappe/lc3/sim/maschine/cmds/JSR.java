package de.nasskappe.lc3.sim.maschine.cmds;

public class JSR implements ICommand {
	
	private short offset;

	@Override
	public void init(short code) throws IllegalOpcodeException {
		int bit11 = (code & (1<<11));
		if (bit11 == 0)
			throw new IllegalOpcodeException(code);
		
		offset = (short) (code & 0x07FF);
		if ((offset & 0x0400) != 0) {
			offset |= 0xF800;
		}
	}

	@Override
	public void execute() {
		// TODO Auto-generated method stub

	}

}
