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

public class CPU {

	private Memory mem;
	private Map<Register, Short> register;
	private CommandFactory cmdFactory;
	private Set<ICPUListener> listeners;
	
	private Set<Integer> addressBreakpoints;
	
	public CPU() {
		listeners = new HashSet<ICPUListener>();
		cmdFactory = new CommandFactory();
		mem = new Memory();
		register = new HashMap<Register, Short>();
		addressBreakpoints = new HashSet<>();

		register.put(Register.R0, (short) 0);
		register.put(Register.R1, (short) 0);
		register.put(Register.R2, (short) 0);
		register.put(Register.R3, (short) 0);
		register.put(Register.R4, (short) 0);
		register.put(Register.R5, (short) 0);
		register.put(Register.R6, (short) 0);
		register.put(Register.R7, (short) 0);
		register.put(Register.PC, (short) 0x3000);
		register.put(Register.PSR, (short) 0);
		register.put(Register.IR, (short) 0);
		updateCC((short) 0);
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
		ICommand cmd = cmdFactory.createCommand((short)code);
		setRegister(Register.PC, (short) (addr + 1));

		cmd.execute(this);
		fireInstructionExecuted(this, cmd);
		
		return cmd;
	}
	
	public ICommand run() {
		ICommand lastCmd = null;
		while(!isBreakpointSetFor(getPC()) || lastCmd == null) {
			lastCmd = step();
		}
		return lastCmd;
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

	private void fireInstructionExecuted(CPU cpu, ICommand cmd) {
		for(ICPUListener l : listeners) {
			l.instructionExecuted(cpu, cmd);
		}
	}

	public int getPC() {
		return ((int)register.get(Register.PC)) & 0xFFFF;
	}
	
	public void setPC(int pc) {
		setRegister(Register.PC, (short) pc);
	}
	
	public void setRegister(Register register, Short value) {
		this.register.put(register, value);
		fireRegisterChanged(register, value);
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
	}
	
	public CC_Value getCC() {
		short val = register.get(Register.CC);
		return CC_Value.values()[val];
	}

	public short readMemory(int addr) {
		return mem.getValue(addr);
	}
	
	public void writeMemory(int addr, short value) {
		mem.setValue(addr, value);
		fireMemoryChanged(this, addr, value);
	}

	private void fireMemoryChanged(CPU cpu, int addr, short value) {
		for (ICPUListener l : listeners) {
			l.memoryChanged(this, addr, value);
		}
	}

	public void addCpuListener(ICPUListener listener) {
		listeners.add(listener);
	}
	
	public boolean removeCpuListener(ICPUListener listener) {
		return listeners.remove(listener);
	}
	
	private void fireRegisterChanged(Register register, short value) {
		for(ICPUListener l : listeners) {
			l.registerChanged(this, register, value);
		}
	}

	public void setAddressBreakpoint(Integer address, Boolean aValue) {
		if (aValue) {
			addressBreakpoints.add(address);
		} else {
			addressBreakpoints.remove(address);
		}
		
		fireMemoryChanged(this, address, readMemory(address));
	}
	
}
