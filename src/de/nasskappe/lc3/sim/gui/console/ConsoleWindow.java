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

import de.nasskappe.lc3.sim.maschine.CPU;
import de.nasskappe.lc3.sim.maschine.CPU.State;
import de.nasskappe.lc3.sim.maschine.ICPUListener;
import de.nasskappe.lc3.sim.maschine.IDisplay;
import de.nasskappe.lc3.sim.maschine.Memory;
import de.nasskappe.lc3.sim.maschine.Register;
import de.nasskappe.lc3.sim.maschine.cmds.ICommand;

public class ConsoleWindow extends JDialog implements IDisplay, ICPUListener {

	private JTextArea textArea;
	private CPU cpu;
	private volatile boolean displayBusy;

	public ConsoleWindow(CPU cpu, Window parent) {
		super(parent);
		this.cpu = cpu;
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
		
		cpu.addCpuListener(this);
		setDisplayReady(cpu);
	}

	protected void handleKeypress(KeyEvent e) {
		if (cpu != null) {
			int kbsr = cpu.readMemory(Memory.ADDR_KBSR);
			if ((kbsr & (1 << 15)) == 0) {
				cpu.writeMemory(Memory.ADDR_KBDR, (short) (e.getKeyChar() & 0xFF));
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

	@Override
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
	public void registerChanged(CPU cpu, Register r, short oldValue, short value) {
	}

	@Override
	public void instructionExecuted(CPU cpu, ICommand cmd) {
	}
	
	@Override
	public void memoryChanged(final CPU cpu, int addr, final short value) {
		// output character to display
		if (addr == Memory.ADDR_DDR) {
			if (isDisplayReady() && value != 0) {
				setDisplayBusy(cpu);
				EventQueue.invokeLater(new Runnable() {
					@Override
					public void run() {
						// output character
						outputCharacter((char) (value & 0xFF));

						setDisplayReady(cpu);
					}
				});
			}
		} else if (addr == Memory.ADDR_DSR) {
			boolean mBusy = (value & 0x8000) == 0;
			if (mBusy != displayBusy) {
				if (displayBusy) {
					setDisplayBusy(cpu);
				} else {
					setDisplayReady(cpu);
				}
			}
		}
	}

	@Override
	public void stateChanged(CPU cpu, State oldState, State newState) {
	}

	private boolean isDisplayReady() {
		return !displayBusy;
	}
	
	private void setDisplayReady(CPU cpu) {
		// display ready
		displayBusy = false;
		cpu.writeMemory(Memory.ADDR_DSR, (short) 0x8000);
	}
	
	private void setDisplayBusy(CPU cpu) {
		// display busy
		displayBusy = true;
		cpu.writeMemory(Memory.ADDR_DSR, (short) 0);
	}

	@Override
	public void memoryRead(CPU cpu, int addr, short value) {
		if (addr == Memory.ADDR_KBDR) {
			setKeyboardBusy();
		}
	}

	private void setKeyboardBusy() {
		cpu.writeMemory(Memory.ADDR_KBSR, (short) 0x0000);
	}

	private void setKeyboardReady() {
		cpu.writeMemory(Memory.ADDR_KBSR, (short) 0x8000);
	}

	
}
