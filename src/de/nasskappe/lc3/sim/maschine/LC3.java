package de.nasskappe.lc3.sim.maschine;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import de.nasskappe.lc3.sim.gui.Lc3Utils;
import de.nasskappe.lc3.sim.maschine.InterruptController.InterruptRequest;
import de.nasskappe.lc3.sim.maschine.Register.CC_Value;
import de.nasskappe.lc3.sim.maschine.cmds.CommandFactory;
import de.nasskappe.lc3.sim.maschine.cmds.ICommand;
import de.nasskappe.lc3.sim.maschine.cmds.JSR;
import de.nasskappe.lc3.sim.maschine.cmds.RET;
import de.nasskappe.lc3.sim.maschine.cmds.RTI;
import de.nasskappe.lc3.sim.maschine.cmds.TRAP;
import de.nasskappe.lc3.sim.maschine.mem.IMemoryListener;
import de.nasskappe.lc3.sim.maschine.mem.Memory;

public class LC3 {
	public enum State {
		RUNNING, STOPPED
	}

	private InterruptController ic;
	private Memory mem;
	private Map<Register, Short> register;
	private volatile State state;
	
	private CommandFactory cmdFactory;
	private Set<ILC3Listener> listeners;
	private Set<Integer> addressBreakpoints;
	private Lc3Utils utils = new Lc3Utils(this);

	private IMemoryListener memoryListener = new IMemoryListener() {
		@Override
		public void memoryRead(Memory memory, int addr, short value) {
		}
		
		@Override
		public void memoryChanged(Memory memory, int addr, short oldValue,
				short newValue) {
			// check clock bit (unset by TRAP 0x25 [HALT])
			if (addr == Memory.ADDR_MCR && !utils.isClockEnabled()) {
				setState(State.STOPPED);
			}
		}
	};
	
	public LC3() {
		state = State.STOPPED;
		listeners = new HashSet<ILC3Listener>();
		cmdFactory = new CommandFactory();
		
		ic = new InterruptController();
		mem = new Memory();
		mem.addListener(memoryListener);
		
		register = new HashMap<Register, Short>();
		addressBreakpoints = new HashSet<Integer>();
			
		reset();
	}
	
	public void loadData(int startAddress, InputStream input) throws IOException {
		for(int b = input.read(); b != -1; b = input.read()) {
			b <<= 8;
			b |= input.read();
			mem.setValue(startAddress++, (short) b);
		}
	}
	
	public ICommand step() {
		Integer addr = getPC();
		short code = mem.getValue(addr);
		setRegister(Register.IR, code);
		ICommand cmd = cmdFactory.createCommand((short)code, addr);
		setRegister(Register.PC, (short) (addr + 1));

		cmd.execute(this);
		
		fireInstructionExecuted(this, cmd);
		
		handleInterrupt();
		
		return cmd;
	}
	
	private void handleInterrupt() {
		if (ic.isNextInterruptHigherThan(utils.getPriority())) {
			// save PC and PSR
			short oldPSR = getRegister(Register.PSR);
			short oldPC = getRegister(Register.PC);
			short ssp = getRegister(Register.SSP);
			
			getMemory().setValue(--ssp, oldPC);
			getMemory().setValue(--ssp, oldPSR);
			
			utils.setSupervisor(true);
			
			setRegister(Register.R6, ssp);
			
			// switch to interrupt handling
			InterruptRequest ir = ic.getNextInterrupt();
			short newPC = getMemory().getValue(0x100 + ir.getInterrupt().getVector());
			utils.setPriority(ir.getPriority());
			setRegister(Register.PC, newPC);
		}
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
			
			// update MCR
			if (state == State.RUNNING) {
				mem.setValue(Memory.ADDR_MCR, (short) 0x8000);
			} else {
				mem.setValue(Memory.ADDR_MCR, (short) 0x0);
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
		if (register == Register.R6) {
			if (utils.isSupervisor()) {
				this.register.put(Register.SSP, value);
			} else {
				this.register.put(Register.USP, value);
			}
		}
		if (oldValue == null)
			oldValue = 0;
		fireRegisterChanged(register, oldValue, value);
	}
	
	public Short getRegister(Register register) {
		Short value = this.register.get(register);
		if (value == null) {
			value = 0;
		}
		return value;
	}

	public void updateCC(short value) {
		Register.CC_Value ccValue = Register.CC_Value.Z;
		if (value > 0)
			ccValue = Register.CC_Value.P;
		else if (value < 0)
			ccValue = Register.CC_Value.N;
		
		setRegister(Register.CC, (short) ccValue.ordinal());
		utils.setCC(ccValue.getBits());
	}
	
	public CC_Value getCC() {
		short val = register.get(Register.CC);
		return CC_Value.values()[val];
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
		
		fireBreakpointChanged(address, aValue);
	}
	
	private void fireBreakpointChanged(int address, boolean set) {
		for(ILC3Listener l : listeners) {
			l.breakpointChanged(this, address, set);
		}
	}
	
	public void reset() {
		setState(State.STOPPED);
		
		for(int addr = 0; addr < 0x10000; addr++) {
			mem.setValue(addr, (short) 0);
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
		
		setRegister(Register.USP, (short) 0);
		setRegister(Register.SSP, (short) 0x3000);
		setRegister(Register.R0, (short) 0);
		setRegister(Register.R1, (short) 0);
		setRegister(Register.R2, (short) 0);
		setRegister(Register.R3, (short) 0);
		setRegister(Register.R4, (short) 0);
		setRegister(Register.R5, (short) 0);
		setRegister(Register.R6, (short) 0);
		setRegister(Register.R7, (short) 0);
		setRegister(Register.PSR, (short) 0x8000);
		setRegister(Register.IR, (short) 0);
		setRegister(Register.PC, (short) 0x3000);
		updateCC((short) 0);
	}

	public Memory getMemory() {
		return mem;
	}

	public InterruptController getInterruptController() {
		return ic;
	}

	public Lc3Utils getUtils() {
		return utils;
	}
}
