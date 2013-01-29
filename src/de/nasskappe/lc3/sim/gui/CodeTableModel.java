package de.nasskappe.lc3.sim.gui;

import java.util.HashMap;
import java.util.Map;

import javax.swing.table.AbstractTableModel;

import de.nasskappe.lc3.sim.maschine.CPU;
import de.nasskappe.lc3.sim.maschine.CPU.State;
import de.nasskappe.lc3.sim.maschine.ICPUListener;
import de.nasskappe.lc3.sim.maschine.Register;
import de.nasskappe.lc3.sim.maschine.cmds.BR;
import de.nasskappe.lc3.sim.maschine.cmds.CommandFactory;
import de.nasskappe.lc3.sim.maschine.cmds.ICommand;

public class CodeTableModel extends AbstractTableModel implements ICPUListener {

	private final static String[] COLUMNS = {
		"", "address", "binary", "hex", "ASM"
	};
	
	private final static BR nop = new BR();
	
	private Map<Integer, ICommand> row2cmd = new HashMap<Integer, ICommand>(128);
	private CommandFactory factory = new CommandFactory();
	private CPU cpu;
	
	public CodeTableModel(CPU cpu) {
		this.cpu = cpu;
	}
	
	@Override
	public String getColumnName(int column) {
		return COLUMNS[column];
	}
	
	@Override
	public int getRowCount() {
		return 0xFFFF;
	}

	@Override
	public int getColumnCount() {
		return 5;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		ICommand cmd = row2cmd.get(rowIndex);
		if (cmd == null) {
			switch(columnIndex) {
			case 0: return false;
			case 1: return rowIndex;
			case 2: return 0;
			case 3: return 0;
			case 4: return nop;
			}
		} else {
			switch(columnIndex) {
			case 0: return cpu.isBreakpointSetFor(rowIndex);
			case 1: return rowIndex;
			case 2: return cmd.getCode();
			case 3: return cmd.getCode();
			case 4: return cmd; 
			}
		}
		return null;
	}
	
	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		super.setValueAt(aValue, rowIndex, columnIndex);
		
		ICommand cmd = row2cmd.get(rowIndex);
		if (cmd == null) {
			cmd = new BR();
			cmd.init((short) 0);
			row2cmd.put(rowIndex, cmd);
		}

		switch(columnIndex) {
		case 0:
			cpu.setAddressBreakpoint(rowIndex, (Boolean) aValue);
			fireTableRowsUpdated(rowIndex, rowIndex);
			break;
		}
	}

	@Override
	public void registerChanged(CPU cpu, Register r, short oldValue, short value) {
		if (r == Register.PC) {
			int pc = ((int)value) & 0xffff;
			int oldPC = ((int)oldValue) & 0xffff;
			
			fireTableRowsUpdated(oldPC, oldPC);
			fireTableRowsUpdated(pc, pc);
		}
	}

	@Override
	public void instructionExecuted(CPU cpu, ICommand cmd) {
	}

	@Override
	public void memoryChanged(CPU cpu, int addr, short value) {
		if (value == 0) {
			row2cmd.remove((Integer) addr);
		} else {
			ICommand cmd = factory.createCommand(value, addr);
			row2cmd.put(addr, cmd);
		}
		fireTableRowsUpdated(addr, addr);
	}

	@Override
	public void stateChanged(CPU cpu, State oldState, State newState) {
	}

	@Override
	public void memoryRead(CPU cpu, int addr, short value) {
	}
	
}
