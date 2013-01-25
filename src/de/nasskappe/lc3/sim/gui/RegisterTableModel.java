package de.nasskappe.lc3.sim.gui;

import javax.swing.table.AbstractTableModel;

import de.nasskappe.lc3.sim.maschine.CPU;
import de.nasskappe.lc3.sim.maschine.ICPUListener;
import de.nasskappe.lc3.sim.maschine.Register;
import de.nasskappe.lc3.sim.maschine.cmds.ICommand;

public class RegisterTableModel extends AbstractTableModel implements ICPUListener {

	private static String[][] LABELS = {
		{ "R0", "R1", "R2", "R3" },
		{ "R4", "R5", "R6", "R7" },
		{ "PSR", "PC", "IR", "CC" }
	};
	
	private CPU cpu;

	@Override
	public int getRowCount() {
		return 4;
	}

	@Override
	public int getColumnCount() {
		return 6;
	}
	
	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		if (columnIndex == 0 || columnIndex == 2 || columnIndex == 4) {
			return LABELS[columnIndex/2][rowIndex];
		} else {
			if (cpu == null)
				return null;
		
			if (columnIndex == 1) {
				switch(rowIndex) {
				case 0 : return cpu.getRegister(Register.R0);
				case 1 : return cpu.getRegister(Register.R1);
				case 2 : return cpu.getRegister(Register.R2);
				case 3 : return cpu.getRegister(Register.R3);
				}
			} else if (columnIndex == 3) {
				switch(rowIndex) {
				case 0 : return cpu.getRegister(Register.R4);
				case 1 : return cpu.getRegister(Register.R5);
				case 2 : return cpu.getRegister(Register.R6);
				case 3 : return cpu.getRegister(Register.R7);
				}
			} else if (columnIndex == 5) {
				switch(rowIndex) {
				case 0 : return cpu.getRegister(Register.PSR);
				case 1 : return cpu.getPC();
				case 2 : return cpu.getRegister(Register.IR);
				case 3 : return cpu.getCC();
				}
			}
		}
		return null;
	}

	@Override
	public void registerChanged(CPU cpu, Register r, short oldValue, short value) {
		this.cpu = cpu;
		fireTableDataChanged();
	}

	@Override
	public void instructionExecuted(CPU cpu, ICommand cmd) {
	}

	@Override
	public void memoryChanged(CPU cpu, int addr, short value) {
	}
	
	@Override
	public Class<?> getColumnClass(int columnIndex) {
		if (columnIndex % 2 == 1)
			return Integer.class;
		
		return super.getColumnClass(columnIndex);
	}

	
}
