package de.nasskappe.lc3.sim.gui.editor;

import java.awt.Component;
import java.awt.Font;

import javax.swing.DefaultCellEditor;
import javax.swing.JTable;

import de.nasskappe.lc3.sim.gui.JNumberField;

public class NumberCellEditor extends DefaultCellEditor {

	private JNumberField numberField;

	public NumberCellEditor() {
		super(new JNumberField());

		numberField = (JNumberField) editorComponent;
		numberField.setFont(new Font("Courier New", Font.PLAIN, numberField.getFont().getSize()));
	}

	public int getValue() {
		return numberField.getNumber();
	}

	@Override
	public Object getCellEditorValue() {
		return getValue();
	}

	@Override
	public Component getTableCellEditorComponent(JTable table, Object value,
			boolean isSelected, int row, int column) {
		super.getTableCellEditorComponent(table, value, isSelected, row, column);
		
		numberField.setNumber(((Number) value).intValue());
		
		return numberField;
	}
}