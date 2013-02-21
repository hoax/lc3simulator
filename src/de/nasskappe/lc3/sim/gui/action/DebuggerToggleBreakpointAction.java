package de.nasskappe.lc3.sim.gui.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import de.nasskappe.lc3.sim.gui.MainWindow;
import de.nasskappe.lc3.sim.maschine.LC3;

public class DebuggerToggleBreakpointAction extends AbstractAction {

	private ImageIcon icon;
	private LC3 lc3;
	private JTable table;

	public DebuggerToggleBreakpointAction(LC3 lc3, JTable table) {
		this.lc3 = lc3;
		this.table = table;

		icon = new ImageIcon(MainWindow.class.getResource("/de/nasskappe/lc3/sim/gui/icons/breakpoint.png"));
		icon = new ImageIcon(icon.getImage().getScaledInstance(16, 16, 0));

		putValue(NAME, "toggle breakpoint");
		putValue(SHORT_DESCRIPTION, "toggles breakpoint at current selection");
		putValue(SMALL_ICON, icon);
		putValue(ACTION_COMMAND_KEY, "debugger_toogle_breakpoint");
		putValue(MNEMONIC_KEY, (int)'t');
		
		table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				setEnabled(e.getFirstIndex() != -1);
			}
		});
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		int row = table.getSelectedRow();
		lc3.toggleAddressBreakpoint(row);
	}
	
}
