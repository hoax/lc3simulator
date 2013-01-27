package de.nasskappe.lc3.sim.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.ButtonGroup;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import de.nasskappe.lc3.sim.gui.formatter.BinaryFormatter;
import de.nasskappe.lc3.sim.gui.formatter.DecimalFormatter;
import de.nasskappe.lc3.sim.gui.formatter.HexFormatter;
import de.nasskappe.lc3.sim.gui.formatter.IValueFormatter;

public class JNumberField extends JTextField {

	private JPopupMenu popupMenu;
	protected IValueFormatter formatter;
	private int value;

	public JNumberField() {
		super();
		init();
	}

	private void init() {
		createPopup();
	}
	
	private void createPopup() {
		final DecimalFormatter decimalFormatter = new DecimalFormatter();
		final HexFormatter hexFormatter = new HexFormatter();
		final BinaryFormatter binaryFormatter = new BinaryFormatter();
				
		JRadioButtonMenuItem decItem = new JRadioButtonMenuItem("decimal");
		decItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				formatter = decimalFormatter;
				updateText();
			}
		});
		
		JRadioButtonMenuItem hexItem = new JRadioButtonMenuItem("hexadecimal");
		hexItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				formatter = hexFormatter;
				updateText();
			}
		});
		
		JRadioButtonMenuItem binaryItem = new JRadioButtonMenuItem("binary");
		binaryItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				formatter = binaryFormatter;
				updateText();
			}
		});
		
		ButtonGroup bg = new ButtonGroup();
		bg.add(decItem);
		bg.add(hexItem);
		bg.add(binaryItem);

		popupMenu = new JPopupMenu();
		popupMenu.add(decItem);
		popupMenu.add(hexItem);
		popupMenu.add(binaryItem);
		
		this.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (SwingUtilities.isRightMouseButton(e)) {
					popupMenu.show(JNumberField.this, e.getX(), e.getY());
				}
			}
		});
		
		formatter = hexFormatter;
		hexItem.setSelected(true);
	}

	public void setNumber(int value) {
		this.value = value;
		updateText();
	}

	private void updateText() {
		String s = formatter.format(value);
		setText(s);
	}
	
	public int getNumber() {
		return NumberUtils.stringToInt(getText());
	}
	
}
