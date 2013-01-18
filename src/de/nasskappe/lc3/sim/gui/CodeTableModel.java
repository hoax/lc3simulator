package de.nasskappe.lc3.sim.gui;

import java.util.HashMap;
import java.util.Map;

import javax.swing.table.AbstractTableModel;

import de.nasskappe.lc3.sim.maschine.CPU;
import de.nasskappe.lc3.sim.maschine.ICPUListener;
import de.nasskappe.lc3.sim.maschine.Lc3Exception;
import de.nasskappe.lc3.sim.maschine.Register;
import de.nasskappe.lc3.sim.maschine.cmds.CommandFactory;
import de.nasskappe.lc3.sim.maschine.cmds.ICommand;

public class CodeTableModel extends AbstractTableModel implements ICPUListener {
	
	private Map<Integer, ICommand> row2cmd = new HashMap<Integer, ICommand>(128);
	private CommandFactory factory = new CommandFactory();
	
	@Override
	public int getRowCount() {
		return 0xFFFF;
	}

	@Override
	public int getColumnCount() {
		return 2;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		ICommand cmd = row2cmd.get(rowIndex);
		if (cmd == null) {
			switch(columnIndex) {
			case 0: return rowIndex;
			case 1: return "NOP";
			}
		} else {
			switch(columnIndex) {
			case 0: return rowIndex;
			case 1: return cmd.getASM(); 
			}
		}
		return null;
	}

	@Override
	public void registerChanged(CPU cpu, Register r, short value) {
	}

	@Override
	public void instructionExecuted(CPU cpu, ICommand cmd) {
	}

	@Override
	public void memoryChanged(CPU cpu, int addr, short value) {
		try {
			ICommand cmd = factory.createCommand(value);
			row2cmd.put(addr, cmd);
			fireTableRowsUpdated(addr, addr);
		} catch (Lc3Exception e) {
			e.printStackTrace();
		}
	}

	
	
}
