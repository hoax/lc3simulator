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

/**
 * The LC3 simulates the Litte Computer 3 as described in
 * "Introduction to Computing Systems: From bits & gates to C & beyond"
 * [ISBN-13: 978-0072467505].
 *
 * @author Tobias Mayer
 *
 */
public class LC3 {

	/**
	 * the interrupt controller
	 */
	private InterruptController ic;
	
	/**
	 * the memory
	 */
	private Memory mem;
	
	/**
	 * stores the register's values
	 */
	private Map<Register, Short> register;
	
	/**
	 * current state of the LC3
	 */
	private volatile Lc3State state;
	
	/**
	 * factory to decode the bytes to get the correct command implementation
	 */
	private CommandFactory cmdFactory;
	
	/**
	 * listeners that we should notify
	 */
	private Set<ILC3Listener> listeners;
	
	/**
	 * addresses at which the simulator should stop execution
	 */
	private Set<Integer> addressBreakpoints;
	
	/**
	 * maps symbols to memory addresses
	 */
	private SymbolTable symbolTable = new SymbolTable();
	
	/**
	 * utils to support the simulator
	 */
	private Lc3Utils utils = new Lc3Utils(this, symbolTable);

	/**
	 * listens for changes to the MCR and stops execution if
	 * clock-bit is cleared
	 */
	private IMemoryListener memoryListener = new IMemoryListener() {
		@Override
		public void memoryRead(Memory memory, int addr, short value) {
		}
		
		@Override
		public void memoryChanged(Memory memory, int addr, short oldValue,
				short newValue) {
			// check clock bit (unset by TRAP 0x25 [HALT])
			if (addr == Memory.ADDR_MCR && !utils.isClockEnabled()) {
				setState(Lc3State.STOPPED);
			}
		}
	};
	
	/**
	 * creates a new LC3 simulator
	 */
	public LC3() {
		state = Lc3State.STOPPED;
		listeners = new HashSet<ILC3Listener>();
		cmdFactory = new CommandFactory();
		
		ic = new InterruptController();
		mem = new Memory();
		mem.addListener(memoryListener);
		
		register = new HashMap<Register, Short>();
		addressBreakpoints = new HashSet<Integer>();
			
		reset();
	}
	
	/**
	 * executes the next instruction and 
	 * @return
	 */
	public ICommand step() {
		// get address of instruction to execute
		Integer addr = getPC();
		
		// get instruction
		short code = mem.getValue(addr);
		setRegister(Register.IR, code);
		
		// decode instruction
		ICommand cmd = cmdFactory.createCommand((short)code, addr);
		
		// increment PC
		setRegister(Register.PC, (short) (addr + 1));

		// execute
		cmd.execute(this);
		
		// notify listeners
		fireInstructionExecuted(this, cmd);
		
		// handle pending interrupts
		handleInterrupt();
		
		return cmd;
	}

	/**
	 * handle the next pending interrupt if there is one
	 */
	private void handleInterrupt() {
		if (ic.isNextInterruptHigherThan(utils.getPriority())) {
			// save PC and PSR
			short oldPSR = getRegister(Register.PSR);
			short oldPC = getRegister(Register.PC);
			short ssp = getRegister(Register.SSP);
			
			getMemory().setValue(--ssp, oldPC);
			getMemory().setValue(--ssp, oldPSR);
			
			// switch to supervisor mode
			utils.setSupervisor(true);
			
			// set register r6 to supervisor stack
			setRegister(Register.R6, ssp);
			
			// set PC to address of interrupt handler
			InterruptRequest ir = ic.getNextInterrupt();
			short newPC = getMemory().getValue(0x100 + ir.getInterrupt().getVector());
			utils.setPriority(ir.getPriority());
			setRegister(Register.PC, newPC);
		}
	}

	/**
	 * execute next instruction. 
	 * if it is a method call or trap proceed execution until it returns or a
	 * breakpoint is hit
	 * @return
	 */
	public ICommand stepOver() {
		setState(Lc3State.RUNNING);
		
		int oldPC = getPC();
		
		ICommand lastCmd = step();
		if (lastCmd.getClass() == JSR.class
				|| lastCmd.getClass() == TRAP.class) {
			while(!isStopped() && (oldPC + 1) != getPC() && !isBreakpointSetFor(getPC())) {
				lastCmd = step();
			}
		}

		setState(Lc3State.STOPPED);
		return lastCmd;
	}

	/**
	 * calls step() repeatedly until the current subroutine returns or a breakpoint is hit
	 * @return
	 */
	public ICommand stepReturn() {
		setState(Lc3State.RUNNING);
		
		ICommand lastCmd = null;
		int retsUntilReturn = 1;
		Class<?> returnClass = RET.class;
		if (utils.isSupervisor())
			returnClass = RTI.class;
		
		while (!(
				isStopped() 
				|| (isBreakpointSetFor(getPC()) && lastCmd != null) 
				|| (retsUntilReturn == 0 && lastCmd != null && lastCmd.getClass() == returnClass)
				)) {
			lastCmd = step();
			if (lastCmd.getClass() == TRAP.class 
					|| lastCmd.getClass() == JSR.class) {
				retsUntilReturn++;
			}
			else if (lastCmd.getClass() == RET.class) {
				retsUntilReturn--;
			}
		}
				
		setState(Lc3State.STOPPED);
		return lastCmd;
	}
	
	/**
	 * call step() repeatedly until the next breakpoint is hit
	 * @return
	 */
	public ICommand run() {
		setState(Lc3State.RUNNING);
		
		ICommand lastCmd = null;
		while(!isStopped() && (!isBreakpointSetFor(getPC()) || lastCmd == null)) {
			lastCmd = step();
		}
		
		setState(Lc3State.STOPPED);
		return lastCmd;
	}
	
	/**
	 * Signals the machine to stop execution
	 */
	public void stop() {
		setState(Lc3State.STOPPED);
	}
	
	/**
	 * updates the internal status variable and the MCR to the new state 
	 * @param newState
	 */
	private void setState(Lc3State newState) {
		if (newState != state) {
			Lc3State oldState = state;
			state = newState;
			
			// update MCR
			if (state == Lc3State.RUNNING) {
				mem.setValue(Memory.ADDR_MCR, (short) 0x8000);
			} else {
				mem.setValue(Memory.ADDR_MCR, (short) 0x0);
			}
			
			fireStateChanged(oldState, newState);
		}
	}
	
	/**
	 * Determines if the LC3 should be stopped
	 * @return <code>true</code> if it should be stopped, otherwise <code>false</code>
	 */
	public boolean isStopped() {
		return state == Lc3State.STOPPED;
	}
	
	
	/**
	 * Determines if a breakpoint is set for the given address
	 * @param pc
	 * @return <code>true</code> if a breakpoint is set, otherwise <code>false</code>
	 */
	public boolean isBreakpointSetFor(int pc) {
		return addressBreakpoints.contains(pc);
	}
	
	/**
	 * Returns a collection with all currently set breakpoints
	 * @return
	 */
	public Set<Integer> getAddressBreakpoints() {
		return Collections.unmodifiableSet(new HashSet<Integer>(addressBreakpoints));
	}
	
	/**
	 * Toggles a breakpoint on the given address.
	 * @param address
	 * @return <code>true</code> a breakpoint has been set, otherwise <code>false</code>
	 */
	public boolean toggleAddressBreakpoint(Integer address) {
		boolean isBreakpointSet = isBreakpointSetFor(address);

		setAddressBreakpoint(address, !isBreakpointSet);
		
		return !isBreakpointSet;
	}

	/**
	 * notifies the listeners that an instruction was executed
	 * @param lc3 the lc3 that executed that instruction
	 * @param cmd the instruction
	 */
	private void fireInstructionExecuted(LC3 lc3, ICommand cmd) {
		for(ILC3Listener l : listeners) {
			l.instructionExecuted(lc3, cmd);
		}
	}

	/**
	 * returns the unsigned value of register PC
	 * @return
	 */
	public int getPC() {
		return ((int)register.get(Register.PC)) & 0xFFFF;
	}
	
	/**
	 * sets register PC to the given value
	 * @param pc
	 */
	public void setPC(int pc) {
		setRegister(Register.PC, (short) pc);
	}
	
	/**
	 * sets a register to the given value
	 * @param register
	 * @param value
	 */
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
	
	/**
	 * gets the registers current value
	 * @param register
	 * @return
	 */
	public Short getRegister(Register register) {
		Short value = this.register.get(register);
		if (value == null) {
			value = 0;
		}
		return value;
	}

	/**
	 * updates the CC depending on value being zero/negative/positive
	 * @param value
	 */
	public void updateCC(short value) {
		Register.CC_Value ccValue = Register.CC_Value.Z;
		if (value > 0)
			ccValue = Register.CC_Value.P;
		else if (value < 0)
			ccValue = Register.CC_Value.N;
		
		setRegister(Register.CC, (short) ccValue.ordinal());
		utils.setCC(ccValue.getBits());
	}
	
	/**
	 * returns the current CC value
	 * @return
	 */
	public CC_Value getCC() {
		short val = register.get(Register.CC);
		return CC_Value.values()[val];
	}
	
	/**
	 * notifies listeners that the lc3's state has changed form oldState to newState
	 * @param oldState
	 * @param newState
	 */
	private void fireStateChanged(Lc3State oldState, Lc3State newState) {
		for (ILC3Listener l : listeners) {
			l.stateChanged(this, oldState, newState);
		}
	}

	/**
	 * adds a listener to be notified
	 * @param listener
	 */
	public void addListener(ILC3Listener listener) {
		listeners.add(listener);
		listener.stateChanged(this, state, state);
	}
	
	/**
	 * removes a listener
	 * @param listener
	 * @return
	 */
	public boolean removeListener(ILC3Listener listener) {
		return listeners.remove(listener);
	}
	
	/**
	 * notifies listeners that a register's value changed
	 * @param register
	 * @param oldValue
	 * @param value
	 */
	private void fireRegisterChanged(Register register, short oldValue, short value) {
		for(ILC3Listener l : listeners) {
			l.registerChanged(this, register, oldValue, value);
		}
	}

	/**
	 * sets/removes a breakpoint at address depend
	 * @param address address to add/remove breakpoint
	 * @param aValue <code>true</code> if the breakpoint should be set, <code>false</code> if it should be deleted
	 */
	public void setAddressBreakpoint(Integer address, Boolean aValue) {
		if (aValue) {
			addressBreakpoints.add(address);
		} else {
			addressBreakpoints.remove(address);
		}
		
		fireBreakpointChanged(address, aValue);
	}

	/**
	 * notifies listeners that a breakpoint was added/removed
	 * @param address
	 * @param set
	 */
	private void fireBreakpointChanged(int address, boolean set) {
		for(ILC3Listener l : listeners) {
			l.breakpointChanged(this, address, set);
		}
	}
	
	/**
	 * reset the machine, clear memory, load lc3os again
	 */
	public void reset() {
		setState(Lc3State.STOPPED);
		getSymbolTable().clear();
		
		for(int addr = 0; addr < 0x10000; addr++) {
			mem.setValue(addr, (short) 0);
		}
		
		InputStream osStream = getClass().getResourceAsStream("lc3os.obj");
		assert(osStream != null);
		
		try {
			int addr = osStream.read() << 8;
			addr |= osStream.read();
			
			utils.loadData(addr, osStream);
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

	/**
	 * returns the memory
	 * @return
	 */
	public Memory getMemory() {
		return mem;
	}

	/**
	 * returns the interrupt controller
	 * @return
	 */
	public InterruptController getInterruptController() {
		return ic;
	}

	/**
	 * returns the utils
	 * @return
	 */
	public Lc3Utils getUtils() {
		return utils;
	}

	/**
	 * returns the symboltable
	 * @return
	 */
	public SymbolTable getSymbolTable() {
		return symbolTable;
	}
	
}
