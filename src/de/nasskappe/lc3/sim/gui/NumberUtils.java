package de.nasskappe.lc3.sim.gui;

public class NumberUtils {

	public static Integer stringToInt(String addressString) {
		addressString = addressString.replaceAll("\\s", "").toLowerCase();
		if (addressString.startsWith("x"))
			addressString = "0" + addressString;
		
		Integer address;
		if (addressString.startsWith("b")) {
			address = Integer.parseInt(addressString.substring(1), 2);
		} else {
			address = Integer.decode(addressString);
		}
		
		return address;
	}
}
