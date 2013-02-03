package de.nasskappe.lc3.sim.gui;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import de.nasskappe.lc3.sim.maschine.LC3;
import de.nasskappe.lc3.sim.maschine.Register;
import de.nasskappe.lc3.sim.maschine.SymbolTable;
import de.nasskappe.lc3.sim.maschine.mem.Memory;

/**
 * Helper for the LC3
 * 
 * @author Tobias Mayer
 *
 */
public class Lc3Utils {
	private final static int BITMASK_PRIVILEGE = (1 << 15);
	private final static int BIT_PRIORITY = 8;
	private final static int BITMASK_PRIORITY = 0x700; // 0000 0111 0000 0000
	private final static int BITMASK_CLOCK_ENABLED = (1 << 15);
	private final static int BITMASK_CC = 0x7;

	/**
	 * Base class that calls the callback runnable after execution
	 * 
	 * @author Tobias Mayer
	 *
	 */
	private abstract class AbstractLc3Action implements Runnable {
		
		private Runnable postExecute;

		public AbstractLc3Action(Runnable postExecute) {
			this.postExecute = postExecute;
		}

		@Override
		public void run() {
			execute();
			if (postExecute != null)
				postExecute.run();
		}
		
		abstract void execute();
	}
	
	/**
	 * Calls LC3#run() and calls the callback afterwards
	 * 
	 * @author Tobias Mayer
	 *
	 */
	private class RunAction extends AbstractLc3Action {
		public RunAction(Runnable postExecute) {
			super(postExecute);
		}

		@Override
		public void execute() {
			lc3.run();
		}
	}
	
	/**
	 * Calls LC3#step() and calls the callback afterwards
	 * 
	 * @author Tobias Mayer
	 *
	 */
	private class StepIntoAction extends AbstractLc3Action {
		public StepIntoAction(Runnable postExecute) {
			super(postExecute);
		}

		@Override
		public void execute() {
			lc3.step();
		}
	}
	
	/**
	 * Calls LC3#stepOver() and calls the callback afterwards
	 * 
	 * @author Tobias Mayer
	 *
	 */
	private class StepOverAction extends AbstractLc3Action {
		public StepOverAction(Runnable postExecute) {
			super(postExecute);
		}

		@Override
		public void execute() {
			lc3.stepOver();
		}
	}
	
	/**
	 * Calls LC3#stepReturn() and calls the callback afterwards
	 * 
	 * @author Tobias Mayer
	 *
	 */
	private class StepReturnAction extends AbstractLc3Action {
		public StepReturnAction(Runnable postExecute) {
			super(postExecute);
		}

		@Override
		public void execute() {
			lc3.stepReturn();
		}
	}
	
	/**
	 * Executor to run the actions in another Thread (to not block the Swing EDT)
	 */
	private ExecutorService executor = new ThreadPoolExecutor(1, 1, 0, TimeUnit.MINUTES, new LinkedBlockingQueue<Runnable>());
	
	/**
	 * the lc3 which is modified/used
	 */
	private LC3 lc3;
	private SymbolTable symbolTable;
	
	public Lc3Utils(LC3 lc3, SymbolTable symbolTable) {
		this.lc3 = lc3;
		this.symbolTable = symbolTable;
	}

	public void run(Runnable postExecute) {
		executor.execute(new RunAction(postExecute));
	}
	
	public void step(Runnable postExecute) {
		executor.execute(new StepIntoAction(postExecute));
	}
	
	public void stepOver(Runnable postExecute) {
		executor.execute(new StepOverAction(postExecute));
	}
	
	public void stepReturn(Runnable postExecute) {
		executor.execute(new StepReturnAction(postExecute));
	}

	public void pause() {
		lc3.stop();
	}
	
	/**
	 * checks PSR if LC3 is in supervisor mode
	 * @return
	 */
	public boolean isSupervisor() {
		short psr = lc3.getRegister(Register.PSR); 
		return (psr & BITMASK_PRIVILEGE) == 0;
	}
	
	/**
	 * extracts the priority from the PSR
	 * @return
	 */
	public int getPriority() {
		short psr = lc3.getRegister(Register.PSR);
		int prio = (psr & BITMASK_PRIORITY) >> BIT_PRIORITY;
		return prio;
	}

	/**
	 * checks if the clock bit is set inside the MCR
	 * @return
	 */
	public boolean isClockEnabled() {
		short mcr = lc3.getMemory().getValue(Memory.ADDR_MCR);
		return (mcr & BITMASK_CLOCK_ENABLED) != 0;
	}

	/**
	 * turns supervisor mode on/off (modifies PSR)
	 * @param b
	 */
	public void setSupervisor(boolean b) {
		short psr = lc3.getRegister(Register.PSR);
		psr = (short) (psr & ~BITMASK_PRIVILEGE);
		lc3.setRegister(Register.PSR, psr);
	}

	/**
	 * sets the CC bits inside the PSR
	 * @param bits
	 */
	public void setCC(short bits) {
		short psr = lc3.getRegister(Register.PSR);
		psr = (short) (psr & ~BITMASK_CC);
		psr = (short) (psr | bits);
		lc3.setRegister(Register.PSR, psr);
	}

	/**
	 * sets the priority inside the PSR
	 * @param priority value from 0 to 7
	 */
	public void setPriority(int priority) {
		short psr = lc3.getRegister(Register.PSR);
		psr = (short) (psr & ~BITMASK_PRIORITY); // set priority bits to 0
		priority = priority << BIT_PRIORITY; // shift bits of new value to correct position
		priority = priority & BITMASK_PRIORITY; // to be sure set other bits to 0
		psr = (short) (psr | priority);
	}
	
	/**
	 * loads the programm into the memory at the specified address
	 * @param startAddress
	 * @param input
	 * @throws IOException
	 */
	public void loadData(int startAddress, InputStream input) throws IOException {
		for(int b = input.read(); b != -1; b = input.read()) {
			if (symbolTable != null) {
				symbolTable.removeSymbolForAddress(startAddress);
			}

			b <<= 8;
			b |= input.read();
			lc3.getMemory().setValue(startAddress++, (short) b);
		}
	}


}
