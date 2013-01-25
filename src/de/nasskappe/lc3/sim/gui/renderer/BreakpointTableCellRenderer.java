package de.nasskappe.lc3.sim.gui.renderer;

import java.awt.Component;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTable;

import de.nasskappe.lc3.sim.maschine.CPU;

public class BreakpointTableCellRenderer extends DefaultCodeTableCellRenderer {

	private ImageIcon breakpointImage;

	public BreakpointTableCellRenderer(CPU cpu, JTable table) {
		super(cpu);
		breakpointImage = new ImageIcon(getClass().getResource("breakpoint.png"));
		int height = table.getRowHeight() - 4;
		breakpointImage =  new ImageIcon(breakpointImage.getImage().getScaledInstance(height, height, 0));
	}
	
	@Override
	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {
		JLabel comp = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus,
				row, column);
		
		if (value instanceof Boolean) {
			Boolean v = (Boolean) value;
			Icon icon = null;
			if (v) {
				icon = breakpointImage;
			}
			comp.setIcon(icon);
			comp.setText(null);
		}
		
		return comp;
	}
}
