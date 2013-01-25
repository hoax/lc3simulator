package de.nasskappe.lc3.sim.gui;

import java.util.HashMap;
import java.util.Map;

import javax.swing.table.AbstractTableModel;

import de.nasskappe.lc3.sim.maschine.CPU;
import de.nasskappe.lc3.sim.maschine.ICPUListener;
import de.nasskappe.lc3.sim.maschine.Register;
import de.nasskappe.lc3.sim.maschine.cmds.BR;
import de.nasskappe.lc3.sim.maschine.cmds.CommandFactory;
import de.nasskappe.lc3.sim.maschine.cmds.ICommand;

public class CodeTableModel extends AbstractTableModel implements ICPUListener {
	
	private Map<Integer, ICommand> row2cmd = new HashMap<Integer, ICommand>(128);
	private CommandFactory factory = new CommandFactory();
	private CPU cpu;
	
	public CodeTableModel(CPU cpu) {
		this.cpu = cpu;
	}
	
	@Override
	public int getRowCount() {
		return 0xFFFF;
	}

	@Override
	public int getColumnCount() {
		return 4;
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
			case 4: return "NOP";
			}
		} else {
			switch(columnIndex) {
			case 0: return cpu.isBreakpointSetFor(rowIndex);
			case 1: return rowIndex;
			case 2: return cmd.getCode();
			case 3: return cmd.getCode();
			case 4: return cmd.getASM(); 
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
	public void registerChanged(CPU cpu, Register r, short value) {
	}

	@Override
	public void instructionExecuted(CPU cpu, ICommand cmd) {
	}

	@Override
	public void memoryChanged(CPU cpu, int addr, short value) {
		ICommand cmd = factory.createCommand(value);
		row2cmd.put(addr, cmd);
		fireTableRowsUpdated(addr, addr);
	}
	
}