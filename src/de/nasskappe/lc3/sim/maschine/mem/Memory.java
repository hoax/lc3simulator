package de.nasskappe.lc3.sim.maschine.mem;

import java.util.ArrayList;
import java.util.List;

public class Memory {
	
	public final static int ADDR_DDR = 0xFE06;
	public final static int ADDR_DSR = 0xFE04;
	public final static int ADDR_KBDR = 0xFE02;
	public final static int ADDR_KBSR = 0xFE00;
	public final static int ADDR_MCR = 0xFFFE;
	
	public final static int PSR_ADDR = 0xFEFD;
	public final static int USP_ADDR = 0xFEFE;
	public final static int SSP_ADDR = 0xFEFF;

	private List<IMemoryListener> listeners;
	
	private short[] mem;
	
	public Memory() {
		listeners = new ArrayList<IMemoryListener>();
		mem = new short[0x10000];
	}
	
	public short getValue(int addr) {
		short value = mem[addr];
		fireMemoryRead(addr, value);
		return value;
	}
	
	public void setValue(int addr, short value) {
		addr = addr & 0xFFFF;
		short oldValue = mem[addr];
		mem[addr] = value;
		fireMemoryChanged(addr, oldValue, value);
	}
	
	public void addListener(IMemoryListener l) {
		listeners.add(l);
	}
	
	public boolean removeListener(IMemoryListener l) {
		return listeners.remove(l);
	}

	private void fireMemoryChanged(int addr, short oldValue, short newValue) {
		for (IMemoryListener l : listeners) {
			l.memoryChanged(this, addr, oldValue, newValue);
		}
	}
	
	private void fireMemoryRead(int addr, short value) {
		for (IMemoryListener l : listeners) {
			l.memoryRead(this, addr, value);
		}
	}

}
