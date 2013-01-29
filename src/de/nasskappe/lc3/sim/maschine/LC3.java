package de.nasskappe.lc3.sim.maschine;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import de.nasskappe.lc3.sim.maschine.Register.CC_Value;
import de.nasskappe.lc3.sim.maschine.cmds.CommandFactory;
import de.nasskappe.lc3.sim.maschine.cmds.ICommand;
import de.nasskappe.lc3.sim.maschine.cmds.JSR;
import de.nasskappe.lc3.sim.maschine.cmds.RET;
import de.nasskappe.lc3.sim.maschine.cmds.RTI;
import de.nasskappe.lc3.sim.maschine.cmds.TRAP;

public class LC3 {
	public enum State {
		RUNNING, STOPPED
	}
	
	private final static int BIT_P = 0;
	private final static int BIT_Z = 1;
	private final static int BIT_N = 2;
	private final static int BITS_PRIORITY = 8;
	private final static int BIT_PRIVILEGE = 15;
	
	private Memory mem;
	private Map<Register, Short> register;
	private int currentPriority;
	private volatile State state;
	
	private CommandFactory cmdFactory;
	private Set<ILC3Listener> listeners;
	private Set<Integer> addressBreakpoints;
	
	
	public LC3() {
		state = State.STOPPED;
		currentPriority = 0;
		listeners = new HashSet<ILC3Listener>();
		cmdFactory = new CommandFactory();
		mem = new Memory();
		register = new HashMap<Register, Short>();
		addressBreakpoints = new HashSet<>();
	
		reset();
	}
	
	public void loadData(int startAddress, InputStream input) throws IOException {
		for(int b = input.read(); b != -1; b = input.read()) {
			b <<= 8;
			b |= input.read();
			writeMemory(startAddress++, (short) b);
		}
	}
	
	public ICommand step() {
		Integer addr = getPC();
		short code = readMemory(addr);
		setRegister(Register.IR, code);
		ICommand cmd = cmdFactory.createCommand((short)code, addr);
		setRegister(Register.PC, (short) (addr + 1));

		cmd.execute(this);
		
		fireInstructionExecuted(this, cmd);
		
		return cmd;
	}
	
	public ICommand stepOver() {
		setState(State.RUNNING);
		
		int oldPC = getPC();
		
		ICommand lastCmd = step();
		if (lastCmd.getClass() == JSR.class
				|| lastCmd.getClass() == TRAP.class) {
			while(!isStopped() && (oldPC + 1) != getPC() && !isBreakpointSetFor(getPC())) {
				lastCmd = step();
			}
		}

		setState(State.STOPPED);
		return lastCmd;
	}

	public ICommand stepReturn() {
		setState(State.RUNNING);
		
		ICommand lastCmd = null;
		while(!isStopped() && (!isBreakpointSetFor(getPC()) || lastCmd == null) 
				&& (lastCmd == null || !(lastCmd.getClass() == RET.class || lastCmd.getClass() == RTI.class))) {
			lastCmd = step();
		}
		
		setState(State.STOPPED);
		return lastCmd;
	}
	
	public ICommand run() {
		setState(State.RUNNING);
		
		ICommand lastCmd = null;
		while(!isStopped() && (!isBreakpointSetFor(getPC()) || lastCmd == null)) {
			lastCmd = step();
		}
		
		setState(State.STOPPED);
		return lastCmd;
	}

	public void setState(State newState) {
		if (newState != state) {
			State oldState = state;
			state = newState;
			
			if (state == State.RUNNING) {
				writeMemory(Memory.ADDR_MCR, (short) 0x8000);
			} else {
				writeMemory(Memory.ADDR_MCR, (short) 0x0);
			}
			
			fireStateChanged(oldState, newState);
		}
	}
	
	public boolean isStopped() {
		return state == State.STOPPED;
	}
	
	
	public boolean isBreakpointSetFor(int pc) {
		return addressBreakpoints.contains(pc);
	}
	
	public Set<Integer> getAddressBreakpoints() {
		return Collections.unmodifiableSet(addressBreakpoints);
	}
	
	public boolean toggleAddressBreakpoint(Integer address) {
		boolean isBreakpointSet = isBreakpointSetFor(address);

		setAddressBreakpoint(address, !isBreakpointSet);
		
		return !isBreakpointSet;
	}

	private void fireInstructionExecuted(LC3 lc3, ICommand cmd) {
		for(ILC3Listener l : listeners) {
			l.instructionExecuted(lc3, cmd);
		}
	}

	public int getPC() {
		return ((int)register.get(Register.PC)) & 0xFFFF;
	}
	
	public void setPC(int pc) {
		setRegister(Register.PC, (short) pc);
	}
	
	public void setRegister(Register register, Short value) {
		Short oldValue = this.register.get(register);
		this.register.put(register, value);
		if (oldValue == null)
			oldValue = 0;
		fireRegisterChanged(register, oldValue, value);
	}
	
	public Short getRegister(Register register) {
		return this.register.get(register);
	}

	public void updateCC(short value) {
		Register.CC_Value ccValue = Register.CC_Value.Z;
		if (value > 0)
			ccValue = Register.CC_Value.P;
		else if (value < 0)
			ccValue = Register.CC_Value.N;
		
		setRegister(Register.CC, (short) ccValue.ordinal());
		updatePSR();
	}
	
	public CC_Value getCC() {
		short val = register.get(Register.CC);
		return CC_Value.values()[val];
	}

	public short readMemory(int addr) {
		short value = mem.getValue(addr);
		fireMemoryRead(addr, value);
		return value;
	}
	
	public void writeMemory(int addr, short value) {
		mem.setValue(addr, value);
		
		// check clock bit (disabled by TRAP 0x25 [HALT])
		if (addr == Memory.ADDR_MCR && (value & 0x8000) == 0) {
			setState(State.STOPPED);
		}
		
		fireMemoryChanged(addr, value);
	}

	private void fireMemoryChanged(int addr, short value) {
		for (ILC3Listener l : listeners) {
			l.memoryChanged(this, addr, value);
		}
	}
	
	private void fireMemoryRead(int addr, short value) {
		for (ILC3Listener l : listeners) {
			l.memoryRead(this, addr, value);
		}
	}
	
	private void fireStateChanged(State oldState, State newState) {
		for (ILC3Listener l : listeners) {
			l.stateChanged(this, oldState, newState);
		}
	}

	public void addListener(ILC3Listener listener) {
		listeners.add(listener);
		listener.stateChanged(this, state, state);
	}
	
	public boolean removeListener(ILC3Listener listener) {
		return listeners.remove(listener);
	}
	
	private void fireRegisterChanged(Register register, short oldValue, short value) {
		for(ILC3Listener l : listeners) {
			l.registerChanged(this, register, oldValue, value);
		}
	}

	public void setAddressBreakpoint(Integer address, Boolean aValue) {
		if (aValue) {
			addressBreakpoints.add(address);
		} else {
			addressBreakpoints.remove(address);
		}
		
		fireMemoryChanged(address, readMemory(address));
	}
	
	public void updatePSR() {
		short psr = 0 << BIT_PRIVILEGE; // usermode
		psr |= currentPriority << BITS_PRIORITY;
		psr |= (bool2bit(getCC() == CC_Value.N)) << BIT_N;
		psr |= (bool2bit(getCC() == CC_Value.P)) << BIT_P;
		psr |= (bool2bit(getCC() == CC_Value.Z)) << BIT_Z;
		
		setRegister(Register.PSR, psr);
	}
	
	private int bool2bit(boolean v) {
		return (v) ? 1 : 0;
	}

	public void reset() {
		setState(State.STOPPED);
		
		for(int i = 0; i< 0x10000; i++) {
			writeMemory(i, (short) 0);
		}
		
		InputStream osStream = getClass().getResourceAsStream("lc3os.obj");
		assert(osStream != null);
		
		try {
			int addr = osStream.read() << 8;
			addr |= osStream.read();
			
			loadData(addr, osStream);
		} catch (IOException e) {
			throw new RuntimeException(e.getMessage(), e);
		} finally {
			try {
				osStream.close();
			} catch (IOException e) {
				throw new RuntimeException(e.getMessage(), e);
			}
		}
		
		register.put(Register.R0, (short) 0);
		register.put(Register.R1, (short) 0);
		register.put(Register.R2, (short) 0);
		register.put(Register.R3, (short) 0);
		register.put(Register.R4, (short) 0);
		register.put(Register.R5, (short) 0);
		register.put(Register.R6, (short) 0);
		register.put(Register.R7, (short) 0);
		register.put(Register.PSR, (short) 0);
		register.put(Register.IR, (short) 0);
		register.put(Register.PC, (short) 0x3000);
		updateCC((short) 0);
	}

}
