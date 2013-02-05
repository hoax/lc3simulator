package de.nasskappe.lc3.sim.gui.renderer;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import de.nasskappe.lc3.sim.maschine.LC3;

public class DefaultCodeTableCellRenderer extends DefaultTableCellRenderer {

	public static Color PC_COLOR = new Color(0xa6f271);
	
	LC3 lc3;
	
	public DefaultCodeTableCellRenderer(LC3 lc3) {
		this.lc3 = lc3;
	}
	
	@Override
	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {
		
		setBackground(null);
		
		JLabel comp = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus,
				row, column);
		
		if (lc3 != null && row == lc3.getPC() && lc3.isStopped()) {
			comp.setBackground(PC_COLOR);
		}
		
		setFont(new Font("Courier New", Font.PLAIN, getFont().getSize()));
		return comp;
	}
	
}
