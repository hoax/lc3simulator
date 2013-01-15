package de.nasskappe.lc3.sim.maschine;

import java.util.HashMap;
import java.util.Map;

public class CPU {

	private Memory mem;
	private Map<Register, Short> register;
	
	public CPU() {
		mem = new Memory();
		register = new HashMap<Register, Short>();
		
	}
	
	public void loadData(byte[] data) {
		
	}
}
