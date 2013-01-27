package de.nasskappe.lc3.sim.gui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.AbstractListModel;
import javax.swing.ComboBoxModel;

import de.nasskappe.lc3.sim.gui.formatter.HexFormatter;
import de.nasskappe.lc3.sim.gui.formatter.IValueFormatter;

public class HexNumberComboBoxModel extends AbstractListModel<String> implements ComboBoxModel<String> {

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

			int number = NumberUtils.stringToInt(anObject.toString());
			
			selectedObject = anObject;
			
			addAddress(number);
		}
	}

	public void addAddress(Integer anObject) {
		String s = formatter.format(anObject);
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
