package de.nasskappe.lc3.sim.gui.renderer;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import de.nasskappe.lc3.sim.maschine.CPU;

public class DefaultCodeTableCellRenderer extends DefaultTableCellRenderer {

	public static Color PC_COLOR = Color.CYAN;
	
	private CPU cpu;
	
	public DefaultCodeTableCellRenderer(CPU cpu) {
		this.cpu = cpu;
	}
	
	public CPU getCpu() {
		return cpu;
	}
	
	@Override
	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {
		
		setBackground(null);
		
		JLabel comp = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus,
				row, column);
		
		if (cpu != null && row == cpu.getPC()) {
			comp.setBackground(PC_COLOR);
		}
		
		setFont(new Font("Courier New", Font.PLAIN, getFont().getSize()));
		return comp;
	}
	
}
