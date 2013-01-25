package de.nasskappe.lc3.sim.gui.renderer;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

public class LabelTableCellRenderer extends DefaultTableCellRenderer {

	private static final long serialVersionUID = -4664007369311751307L;

	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {
		Component x = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		x.setFont(x.getFont().deriveFont(Font.BOLD));
		x.setBackground(Color.LIGHT_GRAY);
		return x;
	}

}
