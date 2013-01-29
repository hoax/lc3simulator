package de.nasskappe.lc3.sim.maschine;

public class Memory {

	private short[] mem;
	
	public Memory() {
		mem = new short[0x10000];
	}
	
	short getValue(int addr) {
		return mem[addr];
	}
	
	void setValue(int addr, short value) {
		mem[addr] = value;
	}
}
