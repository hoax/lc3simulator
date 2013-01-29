package de.nasskappe.lc3.sim.gui.console;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Window;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JDialog;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.text.BadLocationException;

import de.nasskappe.lc3.sim.maschine.IDisplay;
import de.nasskappe.lc3.sim.maschine.IKeyboardListener;

public class ConsoleWindow extends JDialog implements IDisplay {

	private JTextArea textArea;
	private IKeyboardListener keyboardListener;

	public ConsoleWindow(Window parent) {
		super(parent);
		setModal(false);
		
		setTitle("Console");
		setLayout(new BorderLayout());

		textArea = new JTextArea();
		textArea.setFont(new Font("Courier New", 
				textArea.getFont().getStyle(), 
				textArea.getFont().getSize()));
		
		textArea.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {
				handleKeypress(e);
				e.consume();
			}
		});
		
		add(new JScrollPane(textArea), BorderLayout.CENTER);
		
		setPreferredSize(new Dimension(400, 400));
		pack();
	}

	protected void handleKeypress(KeyEvent e) {
		if (keyboardListener != null) {
			keyboardListener.keyPressed(e.getKeyChar());
		}
	}
	
	public static void main(String[] args) {
		ConsoleWindow d = new ConsoleWindow(null);
		d.setModal(false);
		d.pack();
		d.setLocationRelativeTo(null);
		d.setVisible(true);
		
		d.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosed(WindowEvent e) {
				System.exit(0);
			}
		});
		d.outputCharacter('h');
	}

	@Override
	public void outputCharacter(char c) {
		try {
			textArea.getDocument().insertString(textArea.getDocument().getLength(),  ""+c, null);
			textArea.setCaretPosition(textArea.getDocument().getLength());
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
	}
	
}
