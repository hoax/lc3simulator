package de.nasskappe.lc3.sim.gui.renderer;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

public class Binary16TableCellRenderer extends DefaultTableCellRenderer {

	private static final long serialVersionUID = -4664007369311751307L;

	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {
		Component x = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

		String binaryString = Integer.toBinaryString(((Number)value).intValue());
		int len = binaryString.length();
		binaryString = "0000000000000000".substring(0, 16-len) + binaryString;
		
		((JLabel)x).setText(binaryString);
		
		return x;
	}

}