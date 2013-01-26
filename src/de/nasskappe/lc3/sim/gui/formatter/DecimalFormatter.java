package de.nasskappe.lc3.sim.gui.formatter;

public class DecimalFormatter implements IValueFormatter {

	@Override
	public String format(int value) {
		return Integer.toString(value);
	}

}
