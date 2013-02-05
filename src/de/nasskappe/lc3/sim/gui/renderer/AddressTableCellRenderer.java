package de.nasskappe.lc3.sim.gui.renderer;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTable;

import de.nasskappe.lc3.sim.maschine.LC3;

public class AddressTableCellRenderer extends DefaultCodeTableCellRenderer {

	private static final long serialVersionUID = -4664007369311751307L;

	public AddressTableCellRenderer(LC3 lc3) {
		super(lc3);
	}
	
	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {

		Component x = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

		if (value instanceof Number) {
			int address = ((Number) value).intValue() & 0xFFFF;
			String label = lc3.getSymbolTable().findSymbolByAddress(address);
			String text = String.format("0x%04X", address);
			if (label != null) {
				text = text + " " + label;
			}
			((JLabel)x).setText(text);
			setToolTipText(text);
		}
		
		return x;
	}

}
