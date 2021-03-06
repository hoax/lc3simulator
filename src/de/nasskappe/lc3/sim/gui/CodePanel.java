package de.nasskappe.lc3.sim.gui;

import java.awt.BorderLayout;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.EventObject;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;

import de.nasskappe.lc3.sim.gui.editor.NumberCellEditor;
import de.nasskappe.lc3.sim.gui.formatter.BinaryFormatter;
import de.nasskappe.lc3.sim.gui.formatter.HexFormatter;
import de.nasskappe.lc3.sim.gui.renderer.ASMTableCellRenderer;
import de.nasskappe.lc3.sim.gui.renderer.AddressTableCellRenderer;
import de.nasskappe.lc3.sim.gui.renderer.Binary16TableCellRenderer;
import de.nasskappe.lc3.sim.gui.renderer.BreakpointTableCellRenderer;
import de.nasskappe.lc3.sim.gui.renderer.Hex16TableCellRenderer;
import de.nasskappe.lc3.sim.maschine.LC3;

public class CodePanel extends JPanel {

	private LC3 lc3;
	private JTable table;
	private CodeTableModel tableModel;

	public CodePanel(LC3 lc3) {
		
		this.lc3 = lc3;
		
		setLayout(new BorderLayout());
		
		table = new JTable() {
			@Override
			public boolean editCellAt(int row, int column, EventObject e) {
				if (e instanceof KeyEvent) {
					KeyEvent k = (KeyEvent) e;
					
					if (k.isControlDown() || k.isMetaDown() || k.isAltDown()) {
						return false;
					}
					
					if (k.getKeyCode() >= KeyEvent.VK_F4
							&& k.getKeyCode() < KeyEvent.VK_F10) {
						return false;
					}
				}
				return super.editCellAt(row, column, e);
			}
		};
		table.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.setRowSelectionAllowed(true);
		table.setFillsViewportHeight(true);
		
		tableModel = new CodeTableModel(lc3);
		
		lc3.addListener(tableModel);
		lc3.getMemory().addListener(tableModel);
		
		table.setModel(tableModel);
		
		table.getColumnModel().getColumn(0).setCellRenderer(new BreakpointTableCellRenderer(lc3, table));
		
		table.getColumnModel().getColumn(1).setCellRenderer(new AddressTableCellRenderer(lc3));
		
		table.getColumnModel().getColumn(2).setCellRenderer(new Binary16TableCellRenderer(lc3));
		table.getColumnModel().getColumn(2).setCellEditor(new NumberCellEditor(true, new BinaryFormatter()));
		
		table.getColumnModel().getColumn(3).setCellRenderer(new Hex16TableCellRenderer(lc3));
		table.getColumnModel().getColumn(3).setCellEditor(new NumberCellEditor(true, new HexFormatter()));

		table.getColumnModel().getColumn(4).setCellRenderer(new ASMTableCellRenderer(lc3));
		
		table.getColumnModel().getColumn(0).setResizable(false);
		table.getColumnModel().getColumn(0).setMaxWidth(22);
		table.getColumnModel().getColumn(0).setMinWidth(22);

		table.getColumnModel().getColumn(1).setResizable(true);
		table.getColumnModel().getColumn(1).setPreferredWidth(80);
		table.getColumnModel().getColumn(1).setMinWidth(80);

		table.getColumnModel().getColumn(2).setResizable(false);
		table.getColumnModel().getColumn(2).setMaxWidth(150);
		table.getColumnModel().getColumn(2).setMinWidth(150);

		table.getColumnModel().getColumn(3).setResizable(false);
		table.getColumnModel().getColumn(3).setMaxWidth(60);
		table.getColumnModel().getColumn(3).setMinWidth(60);

		table.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (SwingUtilities.isLeftMouseButton(e) && e.getClickCount() == 2) {
					if (table.columnAtPoint(e.getPoint()) == 0) {
						toggleBreakpointAtSelectedAddress();
					}
				}
				else if (SwingUtilities.isRightMouseButton(e) && e.getClickCount() == 2) {
					int row = table.rowAtPoint(e.getPoint());
					CodePanel.this.lc3.setPC(row);
				}
			}
		});	
				
		add(new JScrollPane(table), BorderLayout.CENTER);
	}

	public void setSelectedRow(int row) {
		table.getSelectionModel().setSelectionInterval(row, row);
	}

	private void toggleBreakpointAtSelectedAddress() {
		int selectedRow = table.getSelectedRow();
		lc3.toggleAddressBreakpoint(selectedRow);
	}

	public void scrollTo(int row) {
		Rectangle rect = table.getCellRect(row, 0, true);
		rect.y = rect.y - 2* rect.height;
		rect.height = 5 * rect.height;
		table.scrollRectToVisible(rect);
	}

	public JTable getTable() {
		return table;
	}

	public CodeTableModel getTableModel() {
		return tableModel;
	}

	@Override
	public void requestFocus() {
		table.requestFocus();
	}
}
