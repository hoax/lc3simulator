package de.nasskappe.lc3.sim.gui.formatter;

public class BinaryFormatter implements IValueFormatter {

	StringBuffer sb = new StringBuffer();
	
	private String padLeft(String s, int length , char padding) {
		while(s.length() < length) {
			s = padding + s;
		}
		return s;
	}
	
	@Override
	public String format(int value) {
		sb.setLength(0);
		sb.append("b ");
		sb.append(padLeft(Integer.toString(value, 2), 16, '0'));
		sb.insert(14, " ");
		sb.insert(10, " ");
		sb.insert(6, " ");

		return sb.toString();
	}

}
