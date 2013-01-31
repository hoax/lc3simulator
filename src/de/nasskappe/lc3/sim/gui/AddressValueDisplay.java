package de.nasskappe.lc3.sim.gui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.JPanel;

import de.nasskappe.lc3.sim.gui.formatter.BinaryFormatter;
import de.nasskappe.lc3.sim.gui.formatter.DecimalFormatter;
import de.nasskappe.lc3.sim.gui.formatter.HexFormatter;

public class AddressValueDisplay extends JPanel {
	private JLabel lblDecValue;
	private JLabel lblHexvalue;
	private JLabel lblBinvalue;

	private DecimalFormatter decFormatter;
	private HexFormatter hexFormatter;
	private BinaryFormatter binFormatter;
	private JLabel lblAddressValue;
	
	public AddressValueDisplay() {
		decFormatter = new DecimalFormatter();
		hexFormatter = new HexFormatter();
		binFormatter = new BinaryFormatter();
		
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{0, 70, 10, 0, 0, 0};
		gridBagLayout.rowHeights = new int[]{0, 0, 0, 0};
		gridBagLayout.columnWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{0.0, 0.0, 0.0, Double.MIN_VALUE};
		setLayout(gridBagLayout);
		
		JLabel lblAddress = new JLabel("address:");
		GridBagConstraints gbc_lblAddress = new GridBagConstraints();
		gbc_lblAddress.insets = new Insets(0, 0, 5, 5);
		gbc_lblAddress.gridx = 0;
		gbc_lblAddress.gridy = 0;
		add(lblAddress, gbc_lblAddress);
		
		lblAddressValue = new JLabel("address");
		GridBagConstraints gbc_lblAddressValue = new GridBagConstraints();
		gbc_lblAddressValue.anchor = GridBagConstraints.WEST;
		gbc_lblAddressValue.insets = new Insets(0, 0, 5, 5);
		gbc_lblAddressValue.gridx = 1;
		gbc_lblAddressValue.gridy = 0;
		add(lblAddressValue, gbc_lblAddressValue);
		
		JLabel lblDecimal = new JLabel("dec:");
		GridBagConstraints gbc_lblDecimal = new GridBagConstraints();
		gbc_lblDecimal.insets = new Insets(0, 0, 5, 5);
		gbc_lblDecimal.gridx = 3;
		gbc_lblDecimal.gridy = 0;
		add(lblDecimal, gbc_lblDecimal);
		
		lblDecValue = new JLabel("0");
		GridBagConstraints gbc_lblDecValue = new GridBagConstraints();
		gbc_lblDecValue.anchor = GridBagConstraints.WEST;
		gbc_lblDecValue.insets = new Insets(0, 0, 5, 0);
		gbc_lblDecValue.gridx = 4;
		gbc_lblDecValue.gridy = 0;
		add(lblDecValue, gbc_lblDecValue);
		
		JLabel lblHex = new JLabel("hex:");
		GridBagConstraints gbc_lblHex = new GridBagConstraints();
		gbc_lblHex.insets = new Insets(0, 0, 5, 5);
		gbc_lblHex.gridx = 3;
		gbc_lblHex.gridy = 1;
		add(lblHex, gbc_lblHex);
		
		lblHexvalue = new JLabel("0x0000");
		GridBagConstraints gbc_lblHexvalue = new GridBagConstraints();
		gbc_lblHexvalue.anchor = GridBagConstraints.WEST;
		gbc_lblHexvalue.insets = new Insets(0, 0, 5, 0);
		gbc_lblHexvalue.gridx = 4;
		gbc_lblHexvalue.gridy = 1;
		add(lblHexvalue, gbc_lblHexvalue);
		
		JLabel lblBinary = new JLabel("bin:");
		GridBagConstraints gbc_lblBinary = new GridBagConstraints();
		gbc_lblBinary.insets = new Insets(0, 0, 0, 5);
		gbc_lblBinary.gridx = 3;
		gbc_lblBinary.gridy = 2;
		add(lblBinary, gbc_lblBinary);
		
		lblBinvalue = new JLabel("0000 0000 0000 0000");
		GridBagConstraints gbc_lblBinvalue = new GridBagConstraints();
		gbc_lblBinvalue.anchor = GridBagConstraints.WEST;
		gbc_lblBinvalue.gridx = 4;
		gbc_lblBinvalue.gridy = 2;
		add(lblBinvalue, gbc_lblBinvalue);
	}

	public void setNumber(int address, short number) {
		int n = number & 0xFFFF;
		
		String addressString = hexFormatter.format(address);
		lblAddressValue.setText(addressString);
	
		String decString = decFormatter.format(n);
		if (number < 0) {
			decString = decString + " (" +decFormatter.format(number) + ")";
		}
		lblDecValue.setText(decString);
		
		String hexString = hexFormatter.format(n);
		lblHexvalue.setText(hexString);
		
		String binString = binFormatter.format(n);
		lblBinvalue.setText(binString);
	}
}
