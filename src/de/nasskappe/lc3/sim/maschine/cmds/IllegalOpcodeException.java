package de.nasskappe.lc3.sim.maschine.cmds;

import de.nasskappe.lc3.sim.maschine.Lc3Exception;

public class IllegalOpcodeException extends Lc3Exception {

	private static final long serialVersionUID = -7252642844668527597L;

	public IllegalOpcodeException(int code) {
		super("Illegal Opcode: " + code);
	}
}
