package de.nasskappe.lc3.sim.maschine.cmds;

import de.nasskappe.lc3.sim.maschine.LC3;

public abstract class AbstractCommand implements ICommand {

	private short code;
	private boolean isIllegal = false;
	private int codePosition;

	@Override
	abstract public void execute(LC3 lc3);

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

	@Override
	public int getCodePosition() {
		return codePosition;
	}
	
	public void setCodePosition(int codePosition) {
		this.codePosition = codePosition;
	}

}
