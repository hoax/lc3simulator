package de.nasskappe.lc3.sim.gui.formatter;

public class HexFormatter implements IValueFormatter {

	@Override
	public String format(int value) {
		return String.format("0x%04X", value);
	}

}
