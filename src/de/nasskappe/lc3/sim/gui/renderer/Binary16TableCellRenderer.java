package de.nasskappe.lc3.sim.gui.renderer;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTable;

import de.nasskappe.lc3.sim.maschine.CPU;

public class Binary16TableCellRenderer extends DefaultCodeTableCellRenderer {

	private static final long serialVersionUID = -4664007369311751307L;

	private StringBuilder sb = new StringBuilder();
	
	public Binary16TableCellRenderer(CPU cpu) {
		super(cpu);
	}
	
	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {
		Component x = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

		String binaryString = "0000000000000000" + Integer.toBinaryString(((Number)value).intValue());
		int len = binaryString.length();
		binaryString = binaryString.substring(len-16, len);
		sb.setLength(0);
		sb.append(binaryString);
		sb.insert(12, " ");
		sb.insert(8, " ");
		sb.insert(4, " ");
		((JLabel)x).setText(sb.toString());
		
		return x;
	}

}
