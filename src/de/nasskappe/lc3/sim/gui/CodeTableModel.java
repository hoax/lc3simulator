package de.nasskappe.lc3.sim.gui;

import java.util.HashMap;
import java.util.Map;

import javax.swing.table.AbstractTableModel;

import de.nasskappe.lc3.sim.maschine.ILC3Listener;
import de.nasskappe.lc3.sim.maschine.LC3;
import de.nasskappe.lc3.sim.maschine.LC3.State;
import de.nasskappe.lc3.sim.maschine.Register;
import de.nasskappe.lc3.sim.maschine.cmds.BR;
import de.nasskappe.lc3.sim.maschine.cmds.CommandFactory;
import de.nasskappe.lc3.sim.maschine.cmds.ICommand;
import de.nasskappe.lc3.sim.maschine.mem.IMemoryListener;
import de.nasskappe.lc3.sim.maschine.mem.Memory;

public class CodeTableModel extends AbstractTableModel implements ILC3Listener, IMemoryListener {

	private final static String[] COLUMNS = {
		"", "address", "binary", "hex", "ASM"
	};
	
	private final static BR nop = new BR();
	
	private Map<Integer, ICommand> row2cmd = new HashMap<Integer, ICommand>(128);
	private CommandFactory factory = new CommandFactory();
	private LC3 lc3;
	
	public CodeTableModel(LC3 lc3) {
		this.lc3 = lc3;
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
			case 0: return lc3.isBreakpointSetFor(rowIndex);
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
			lc3.setAddressBreakpoint(rowIndex, (Boolean) aValue);
			fireTableRowsUpdated(rowIndex, rowIndex);
			break;
		}
	}

	@Override
	public void registerChanged(LC3 lc3, Register r, short oldValue, short value) {
		if (r == Register.PC) {
			int pc = ((int)value) & 0xffff;
			int oldPC = ((int)oldValue) & 0xffff;
			
			fireTableRowsUpdated(oldPC, oldPC);
			fireTableRowsUpdated(pc, pc);
		}
	}

	@Override
	public void instructionExecuted(LC3 lc3, ICommand cmd) {
	}

	@Override
	public void stateChanged(LC3 lc3, State oldState, State newState) {
	}

	@Override
	public void memoryChanged(Memory memory, int addr, short oldValue,
			short newValue) {
		if (newValue == 0) {
			row2cmd.remove((Integer) addr);
		} else {
			ICommand cmd = factory.createCommand(newValue, addr);
			row2cmd.put(addr, cmd);
		}
		fireTableRowsUpdated(addr, addr);
	}

	@Override
	public void memoryRead(Memory memory, int addr, short value) {
	}

	@Override
	public void breakpointChanged(LC3 lc3, int address, boolean set) {
		// triggers redraw -> show/hide breakpoint icon
		fireTableRowsUpdated(address, address); 
	}
	
}
