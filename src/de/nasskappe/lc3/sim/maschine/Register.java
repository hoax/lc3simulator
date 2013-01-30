package de.nasskappe.lc3.sim.maschine;

public enum Register {
	R0, R1, R2, R3, R4, R5, R6, R7,
	PC, IR, PSR, CC, USP, SSP;
	
	public enum CC_Value {
		P(1), N(4), Z(2);
		
		private final short bits;

		private CC_Value(int bits) {
			this.bits = (short) bits;
		}
		
		public short getBits() {
			return bits;
		}
	}
}
