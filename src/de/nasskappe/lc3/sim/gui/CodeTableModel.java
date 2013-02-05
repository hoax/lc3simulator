package de.nasskappe.lc3.sim.gui;

import java.util.HashMap;
import java.util.Map;

import javax.swing.SwingUtilities;
import javax.swing.table.AbstractTableModel;

import de.nasskappe.lc3.sim.maschine.ILC3Listener;
import de.nasskappe.lc3.sim.maschine.LC3;
import de.nasskappe.lc3.sim.maschine.Lc3State;
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

	private int oldPC;
	
	public CodeTableModel(LC3 lc3) {
		this.lc3 = lc3;
	}
	
	@Override
	public String getColumnName(int column) {
		return COLUMNS[column];
	}
	
	@Override
	public int getRowCount() {
		return 0x10000;
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
			case 0: return lc3.isBreakpointSetFor(rowIndex);
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
		
		switch(columnIndex) {
		case 0:
			lc3.setAddressBreakpoint(rowIndex, (Boolean) aValue);
			fireTableRowsUpdated(rowIndex, rowIndex);
			break;
			
		case 2:
		case 3:
			lc3.getMemory().setValue(rowIndex, ((Number) aValue).shortValue());
			break;
		}
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return (columnIndex == 2 || columnIndex == 3);
	}
	
	@Override
	public void registerChanged(LC3 lc3, Register r, short oldValue, short value) {
		if (lc3.isStopped() && r == Register.PC) {
			updatePC();
		}
	}

	@Override
	public void instructionExecuted(LC3 lc3, ICommand cmd) {
	}

	@Override
	public void stateChanged(LC3 lc3, Lc3State oldState, Lc3State newState) {
		updatePC();
	}
	
	private void updatePC() {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				int pc = lc3.getPC();
				
				fireTableRowsUpdated(oldPC, oldPC);
				fireTableRowsUpdated(pc, pc);
				
				oldPC = pc;
			}
		});
	}

	@Override
	public void memoryChanged(Memory memory, final int addr, short oldValue,
			final short newValue) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				if (newValue == 0) {
					row2cmd.remove((Integer) addr);
				} else {
					ICommand cmd = factory.createCommand(newValue, addr);
					row2cmd.put(addr, cmd);
				}
				fireTableRowsUpdated(addr, addr);
			}
		});
	}

	@Override
	public void memoryRead(Memory memory, int addr, short value) {
	}

	@Override
	public void breakpointChanged(LC3 lc3, final int address, boolean set) {
		// triggers redraw -> show/hide breakpoint icon
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				fireTableRowsUpdated(address, address);
			}
		});
	}
	
}
