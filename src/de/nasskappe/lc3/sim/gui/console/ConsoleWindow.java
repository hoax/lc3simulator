package de.nasskappe.lc3.sim.gui.console;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Window;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JDialog;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.text.BadLocationException;

import de.nasskappe.lc3.sim.maschine.LC3;
import de.nasskappe.lc3.sim.maschine.mem.IMemoryListener;
import de.nasskappe.lc3.sim.maschine.mem.Memory;

public class ConsoleWindow extends JDialog implements IMemoryListener {

	public final static int BITMASK_DISPLAY_READY = (1 << 15);
	public final static int BITMASK_KEYBOARD_READY = (1 << 15);
	public final static int BITMASK_KEYBOARD_INTERRUPT_ENABLED = (1 << 14);
	
	private JTextArea textArea;
	private volatile boolean displayBusy;
	private Memory mem;
	private LC3 lc3;

	public ConsoleWindow(Window parent, LC3 lc3) {
		super(parent);
		
		this.lc3 = lc3;
		this.mem = lc3.getMemory();
		
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
		
		mem.addListener(this);
		setDisplayReady();
		unsetCharacterIsWaiting();
	}

	protected void handleKeypress(KeyEvent e) {
		if (mem != null && !lc3.isStopped()) {
			if (!isCharacterWaiting()) { // previous character was read
				// put value into memory
				mem.setValue(Memory.ADDR_KBDR, (short) e.getKeyChar());
				setCharacterIsWaiting();
			}
			if (isKeyboardInterruptEnabled()) {
				lc3.getInterruptController().interruptKeyboard();
			}
		}
	}
	
	private boolean isKeyboardInterruptEnabled() {
		short value = mem.getValue(Memory.ADDR_KBSR);
		boolean isEnabled = (value & BITMASK_KEYBOARD_INTERRUPT_ENABLED) != 0;
		return isEnabled;
	}

	private boolean isCharacterWaiting() {
		short value = mem.getValue(Memory.ADDR_KBSR);
		return (value & BITMASK_KEYBOARD_READY) != 0;
	}
	
	private void unsetCharacterIsWaiting() {
		short value = mem.getValue(Memory.ADDR_KBSR);
		value = (short) (value & ~BITMASK_KEYBOARD_READY);
		mem.setValue(Memory.ADDR_KBSR, value);
	}
	
	private void setCharacterIsWaiting() {
		short value = mem.getValue(Memory.ADDR_KBSR);
		value = (short) (value | BITMASK_KEYBOARD_READY);
		mem.setValue(Memory.ADDR_KBSR, value);
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
	public void memoryChanged(final Memory mem, int addr, short oldValue, final short newValue) {
		// output character to display
		if (addr == Memory.ADDR_DDR) {
			if (isDisplayReady() && newValue != 0) {
				setDisplayBusy();
				EventQueue.invokeLater(new Runnable() {
					@Override
					public void run() {
						// output character
						outputCharacter((char) (newValue & 0xFF));

						// next character please...
						setDisplayReady();
					}
				});
			}
		} else if (addr == Memory.ADDR_DSR) {
			boolean mBusy = (newValue & BITMASK_DISPLAY_READY) == 0;
			if (mBusy != displayBusy) {
				if (displayBusy) {
					setDisplayBusy();
				} else {
					setDisplayReady();
				}
			}
		}
	}

	private boolean isDisplayReady() {
		return !displayBusy;
	}
	
	private void setDisplayReady() {
		// display ready
		displayBusy = false;
		short value = mem.getValue(Memory.ADDR_DSR);
		value = (short) (value | BITMASK_DISPLAY_READY);
		mem.setValue(Memory.ADDR_DSR, value);
	}
	
	private void setDisplayBusy() {
		// display busy
		displayBusy = true;
		short value = mem.getValue(Memory.ADDR_DSR);
		value = (short) (value & ~BITMASK_DISPLAY_READY);
		mem.setValue(Memory.ADDR_DSR, value);
	}

	@Override
	public void memoryRead(Memory memory, int addr, short value) {
		if (addr == Memory.ADDR_KBDR) {
			unsetCharacterIsWaiting();
		}
	}

	
}
