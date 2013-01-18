package de.nasskappe.lc3.sim.gui;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class Main {
	
	
	public static void main(String[] args) throws ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException {
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		
		MainWindow window = new MainWindow();
		
		window.pack();
		window.setLocationRelativeTo(null);

		window.setDefaultCloseOperation(MainWindow.EXIT_ON_CLOSE);
		window.setVisible(true);
	}
	
}
