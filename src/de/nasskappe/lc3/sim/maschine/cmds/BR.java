package de.nasskappe.lc3.sim.maschine.cmds;

public class BR implements ICommand {

	private boolean n;
	private boolean z;
	private boolean p;
	private short offset;

	@Override
	public void init(short code) throws IllegalOpcodeException {
		n = (code & (1<<11)) != 0;
		z = (code & (1<<10)) != 0;
		p = (code & (1<<9)) != 0;
		offset = (short) (code & 0x01FF);
		if ((offset & 0x100) != 0) {
			offset |= 0xFF00;
		}
	}

	@Override
	public void execute() {
		// TODO Auto-generated method stub

	}

}
