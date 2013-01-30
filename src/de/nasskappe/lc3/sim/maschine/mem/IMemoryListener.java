package de.nasskappe.lc3.sim.maschine.mem;

public interface IMemoryListener {

	void memoryChanged(Memory memory, int addr, short oldValue, short newValue);

	void memoryRead(Memory memory, int addr, short value);

}
