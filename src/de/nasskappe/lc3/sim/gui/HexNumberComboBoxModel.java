package de.nasskappe.lc3.sim.gui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.AbstractListModel;
import javax.swing.ComboBoxModel;

import de.nasskappe.lc3.sim.gui.formatter.HexFormatter;
import de.nasskappe.lc3.sim.gui.formatter.IValueFormatter;

public class HexNumberComboBoxModel extends AbstractListModel implements ComboBoxModel {

	List<String> addresses = new ArrayList<String>();
	Object selectedObject;
	IValueFormatter formatter = new HexFormatter();
	
	@Override
	public int getSize() {
		return addresses.size();
	}

	@Override
	public String getElementAt(int index) {
		return addresses.get(index);
	}

	@Override
	public void setSelectedItem(Object anObject) {
		if ((selectedObject != null && !selectedObject.equals( anObject )) ||
				selectedObject == null && anObject != null) {

			try {
				int number;
				number = NumberUtils.stringToInt(anObject.toString());
				selectedObject = formatter.format(number);
			} catch (Exception e) {
				selectedObject = anObject;
			}
			
			
			fireContentsChanged(this, 0, getSize() - 1);

		}
	}

	public void addAddress(Object anObject) {
		String s = String.valueOf(anObject);
		int number;
		try {
			number = NumberUtils.stringToInt(anObject.toString());
			s = formatter.format(number);
		} catch (Exception e) {
		}
		if (!addresses.contains(s)) {
			addresses.add(s);
			Collections.sort(addresses);
			fireContentsChanged(this, 0, getSize() - 1);
		}
	}

	@Override
	public Object getSelectedItem() {
		return selectedObject;
	}

}
