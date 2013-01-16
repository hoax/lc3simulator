package de.nasskappe.lc3.sim.maschine;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import de.nasskappe.lc3.sim.maschine.Register.CC_Value;
import de.nasskappe.lc3.sim.maschine.cmds.CommandFactory;
import de.nasskappe.lc3.sim.maschine.cmds.ICommand;

public class CPU {

	private Memory mem;
	private Map<Register, Short> register;
	private CommandFactory cmdFactory;
	
	public CPU() {
		cmdFactory = new CommandFactory();
		mem = new Memory();
		register = new HashMap<Register, Short>();

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
		updateCC((short) 0);
	}
	
	public void loadData(int startAddress, InputStream input) throws IOException {
		for(int b = input.read(); b != -1; b = input.read()) {
			b <<= 8;
			b |= input.read();
			mem.setValue(startAddress++, (short) b);
		}
	}
	
	public void step() throws Lc3Exception {
		Integer addr = getPC();
		short code = mem.getValue(addr);
		setRegister(Register.IR, code);
		ICommand cmd = cmdFactory.createCommand((short)code);
		register.put(Register.PC, (short) (addr + 1));

		cmd.execute(this);
	}

	public int getPC() {
		return ((int)register.get(Register.PC)) & 0xFFFF;
	}
	
	public void setPC(int pc) {
		register.put(Register.PC, (short) pc);
	}
	
	public void setRegister(Register register, Short value) {
		this.register.put(register, value);
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
		
		this.register.put(Register.CC, (short) ccValue.ordinal());
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
	}
}
