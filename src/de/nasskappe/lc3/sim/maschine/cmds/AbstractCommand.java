package de.nasskappe.lc3.sim.maschine.cmds;

import de.nasskappe.lc3.sim.maschine.CPU;

public abstract class AbstractCommand implements ICommand {

	private short code;
	private boolean isIllegal;

	@Override
	abstract public void execute(CPU cpu);

	@Override
	public void init(short code) {
		this.code = code;
	}
	
	public short getCode() {
		return code;
	}

	@Override
	abstract public String getASM();

	public boolean isIllegal() {
		return isIllegal;
	}
	
	public void setIllegal(boolean isIllegal) {
		this.isIllegal = isIllegal;
	}
}
