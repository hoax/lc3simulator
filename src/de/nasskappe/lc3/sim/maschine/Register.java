package de.nasskappe.lc3.sim.maschine;

public enum Register {
	R0, R1, R2, R3, R4, R5, R6, R7,
	PC, IR, PSR, CC;
	
	public enum CC_Value {
		P, N, Z
	}
}
