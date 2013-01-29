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
import de.nasskappe.lc3.sim.maschine.Register;
import de.nasskappe.lc3.sim.maschine.cmds.ICommand;

public class ConsoleWindow extends JDialog implements IDisplay, ICPUListener {

	public static int ADDR_KBSR = 0xFE00;
	public static int ADDR_KBDR = 0xFE02;
	public static int ADDR_DSR = 0xFE04;
	public static int ADDR_DDR = 0xFE06;
	
	
	private JTextArea textArea;
	private CPU cpu;

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
			int kbsr = cpu.readMemory(ADDR_KBSR);
			if ((kbsr & (1 << 15)) == 0) {
				cpu.writeMemory(ADDR_KBDR, (short) (e.getKeyChar() & 0xFF));
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
		if (addr == ADDR_DDR) {
			if (isDisplayReady(cpu)) {
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
		}
	}

	@Override
	public void stateChanged(CPU cpu, State oldState, State newState) {
	}

	private boolean isDisplayReady(CPU cpu) {
		int dsr = cpu.readMemory(ADDR_DSR);
		return ((dsr & (1 << 15)) != 0);
	}
	
	private void setDisplayReady(CPU cpu) {
		// display ready
		cpu.writeMemory(ADDR_DSR, (short) 0x8000);
	}
	
	private void setDisplayBusy(CPU cpu) {
		// display busy
		cpu.writeMemory(ADDR_DSR, (short) 0); 
	}

	@Override
	public void memoryRead(CPU cpu, int addr, short value) {
		if (addr == ADDR_KBDR) {
			setKeyboardBusy();
		}
	}

	private void setKeyboardBusy() {
		cpu.writeMemory(ADDR_KBSR, (short) 0x0000);
	}

	private void setKeyboardReady() {
		cpu.writeMemory(ADDR_KBSR, (short) 0x8000);
	}

	
}
