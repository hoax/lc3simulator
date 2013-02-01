package de.nasskappe.lc3.sim.gui;

import javax.swing.SwingUtilities;
import javax.swing.table.AbstractTableModel;

import de.nasskappe.lc3.sim.maschine.ILC3Listener;
import de.nasskappe.lc3.sim.maschine.LC3;
import de.nasskappe.lc3.sim.maschine.Lc3State;
import de.nasskappe.lc3.sim.maschine.Register;
import de.nasskappe.lc3.sim.maschine.cmds.ICommand;

public class RegisterTableModel extends AbstractTableModel implements ILC3Listener {

	private static String[][] LABELS = {
		{ "R0", "R1", "R2", "R3" },
		{ "R4", "R5", "R6", "R7" },
		{ "PSR", "PC", "IR", "CC" }
	};
	
	private LC3 lc3;
	
	public RegisterTableModel(LC3 lc3) {
		this.lc3 = lc3;
	}

	@Override
	public int getRowCount() {
		return 4;
	}

	@Override
	public int getColumnCount() {
		return 6;
	}
	
	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return (columnIndex % 2 == 1) 
				&& !(rowIndex >= 2 && columnIndex == 5);
	}
	
	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		if (columnIndex == 0 || columnIndex == 2 || columnIndex == 4) {
			return LABELS[columnIndex/2][rowIndex];
		} else {
			if (lc3 == null)
				return null;
		
			if (columnIndex == 1) {
				switch(rowIndex) {
				case 0 : return lc3.getRegister(Register.R0);
				case 1 : return lc3.getRegister(Register.R1);
				case 2 : return lc3.getRegister(Register.R2);
				case 3 : return lc3.getRegister(Register.R3);
				}
			} else if (columnIndex == 3) {
				switch(rowIndex) {
				case 0 : return lc3.getRegister(Register.R4);
				case 1 : return lc3.getRegister(Register.R5);
				case 2 : return lc3.getRegister(Register.R6);
				case 3 : return lc3.getRegister(Register.R7);
				}
			} else if (columnIndex == 5) {
				switch(rowIndex) {
				case 0 : return lc3.getRegister(Register.PSR);
				case 1 : return lc3.getPC();
				case 2 : return lc3.getRegister(Register.IR);
				case 3 : return lc3.getCC();
				}
			}
		}
		return null;
	}
	
	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		Number value = (Number) aValue;
		if (columnIndex == 1) {
			switch(rowIndex) {
			case 0 : 
				lc3.setRegister(Register.R0, value.shortValue());
				break;
				
			case 1 : 
				lc3.setRegister(Register.R1, value.shortValue());
				break;
				
			case 2 : 
				lc3.setRegister(Register.R2, value.shortValue());
				break;
				
			case 3 : 
				lc3.setRegister(Register.R3, value.shortValue());
				break;
			}
		} else if (columnIndex == 3) {
			switch(rowIndex) {
			case 0 : 
				lc3.setRegister(Register.R4, value.shortValue());
				break;
				
			case 1 : 
				lc3.setRegister(Register.R5, value.shortValue());
				break;
				
			case 2 : 
				lc3.setRegister(Register.R6, value.shortValue());
				break;
				
			case 3 : 
				lc3.setRegister(Register.R7, value.shortValue());
				break;
			}
		} else if (columnIndex == 5) {
			switch(rowIndex) {
			case 0 : 
				lc3.setRegister(Register.PSR, value.shortValue());
				break;
				
			case 1 : 
				lc3.setPC(value.intValue() & 0xffff);
				break;
				
			case 2 : 
				lc3.setRegister(Register.IR, value.shortValue());
				break;
				
			case 3 : 
				lc3.getCC();
				break;
			}
		}
	}

	@Override
	public void registerChanged(LC3 lc3, Register r, short oldValue, short value) {
	}

	@Override
	public void instructionExecuted(LC3 lc3, ICommand cmd) {
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		if (columnIndex % 2 == 1)
			return Integer.class;
		
		return super.getColumnClass(columnIndex);
	}

	@Override
	public void stateChanged(LC3 lc3, Lc3State oldState, Lc3State newState) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				fireTableDataChanged();
			}
		});
	}

	@Override
	public void breakpointChanged(LC3 lc3, int address, boolean set) {
	}
	
}
