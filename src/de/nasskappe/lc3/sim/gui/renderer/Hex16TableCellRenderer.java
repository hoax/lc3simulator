package de.nasskappe.lc3.sim.gui.renderer;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTable;

import de.nasskappe.lc3.sim.maschine.LC3;

public class Hex16TableCellRenderer extends DefaultCodeTableCellRenderer {

	private static final long serialVersionUID = -4664007369311751307L;

	public Hex16TableCellRenderer(LC3 lc3) {
		super(lc3);
	}
	
	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {

		Component x = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

		if (value instanceof Number) {
			String hexString = String.format("0x%04X", ((Number)value).intValue() & 0xFFFF);
			((JLabel)x).setText(hexString);
		}
		
		return x;
	}

}
