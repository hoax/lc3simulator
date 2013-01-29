package de.nasskappe.lc3.sim.gui.console;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
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

import de.nasskappe.lc3.sim.maschine.ILC3Listener;
import de.nasskappe.lc3.sim.maschine.LC3;
import de.nasskappe.lc3.sim.maschine.LC3.State;
import de.nasskappe.lc3.sim.maschine.Memory;
import de.nasskappe.lc3.sim.maschine.Register;
import de.nasskappe.lc3.sim.maschine.cmds.ICommand;

public class ConsoleWindow extends JDialog implements ILC3Listener {

	private JTextArea textArea;
	private LC3 lc3;
	private volatile boolean displayBusy;

	public ConsoleWindow(LC3 lc3, Window parent) {
		super(parent);
		this.lc3 = lc3;
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
		
		lc3.addListener(this);
		setDisplayReady(lc3);
	}

	protected void handleKeypress(KeyEvent e) {
		if (lc3 != null) {
			int kbsr = lc3.readMemory(Memory.ADDR_KBSR);
			if ((kbsr & (1 << 15)) == 0) {
				lc3.writeMemory(Memory.ADDR_KBDR, (short) (e.getKeyChar() & 0xFF));
				setKeyboardReady();
			}
		}
	}
	
	public static void main(String[] args) {
		ConsoleWindow d = new ConsoleWindow(null, null);
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

	public void outputCharacter(char c) {
		try {
			textArea.getDocument().insertString(textArea.getDocument().getLength(),  ""+c, null);
			textArea.setCaretPosition(textArea.getDocument().getLength());
			if (!isVisible()) {
				setVisible(true);
			}
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void registerChanged(LC3 lc3, Register r, short oldValue, short value) {
	}

	@Override
	public void instructionExecuted(LC3 lc3, ICommand cmd) {
	}
	
	@Override
	public void memoryChanged(final LC3 lc3, int addr, final short value) {
		// output character to display
		if (addr == Memory.ADDR_DDR) {
			if (isDisplayReady() && value != 0) {
				setDisplayBusy(lc3);
				EventQueue.invokeLater(new Runnable() {
					@Override
					public void run() {
						// output character
						outputCharacter((char) (value & 0xFF));

						setDisplayReady(lc3);
					}
				});
			}
		} else if (addr == Memory.ADDR_DSR) {
			boolean mBusy = (value & 0x8000) == 0;
			if (mBusy != displayBusy) {
				if (displayBusy) {
					setDisplayBusy(lc3);
				} else {
					setDisplayReady(lc3);
				}
			}
		}
	}

	@Override
	public void stateChanged(LC3 lc3, State oldState, State newState) {
	}

	private boolean isDisplayReady() {
		return !displayBusy;
	}
	
	private void setDisplayReady(LC3 lc3) {
		// display ready
		displayBusy = false;
		lc3.writeMemory(Memory.ADDR_DSR, (short) 0x8000);
	}
	
	private void setDisplayBusy(LC3 lc3) {
		// display busy
		displayBusy = true;
		lc3.writeMemory(Memory.ADDR_DSR, (short) 0);
	}

	@Override
	public void memoryRead(LC3 lc3, int addr, short value) {
		if (addr == Memory.ADDR_KBDR) {
			setKeyboardBusy();
		}
	}

	private void setKeyboardBusy() {
		lc3.writeMemory(Memory.ADDR_KBSR, (short) 0x0000);
	}

	private void setKeyboardReady() {
		lc3.writeMemory(Memory.ADDR_KBSR, (short) 0x8000);
	}

	
}
