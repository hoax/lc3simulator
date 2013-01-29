package de.nasskappe.lc3.sim.maschine;

public class Memory {

	private short[] mem;
	public static int ADDR_DDR = 0xFE06;
	public static int ADDR_DSR = 0xFE04;
	public static int ADDR_KBDR = 0xFE02;
	public static int ADDR_KBSR = 0xFE00;
	
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
