package de.nasskappe.lc3.sim.gui;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import de.nasskappe.lc3.sim.maschine.LC3;
import de.nasskappe.lc3.sim.maschine.Register;
import de.nasskappe.lc3.sim.maschine.mem.Memory;

public class Lc3Utils {
	private final static int BITMASK_PRIVILEGE = (1 << 15);
	private final static int BIT_PRIORITY = 8;
	private final static int BITMASK_PRIORITY = 0x700; // 0000 0111 0000 0000
	private final static int BITMASK_CLOCK_ENABLED = (1 << 15);
	private final static int BITMASK_CC = 0x7;

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
	
	private class RunAction extends AbstractLc3Action {
		public RunAction(Runnable postExecute) {
			super(postExecute);
		}

		@Override
		public void execute() {
			lc3.run();
		}
	}
	
	private class StepIntoAction extends AbstractLc3Action {
		public StepIntoAction(Runnable postExecute) {
			super(postExecute);
		}

		@Override
		public void execute() {
			lc3.step();
		}
	}
	
	private class StepOverAction extends AbstractLc3Action {
		public StepOverAction(Runnable postExecute) {
			super(postExecute);
		}

		@Override
		public void execute() {
			lc3.stepOver();
		}
	}
	
	private class StepReturnAction extends AbstractLc3Action {
		public StepReturnAction(Runnable postExecute) {
			super(postExecute);
		}

		@Override
		public void execute() {
			lc3.stepReturn();
		}
	}
	
	private ExecutorService executor = new ThreadPoolExecutor(1, 1, 0, TimeUnit.MINUTES, new LinkedBlockingQueue<Runnable>());
	private LC3 lc3;
	
	public Lc3Utils(LC3 lc3) {
		this.lc3 = lc3;
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
		lc3.setState(LC3.State.STOPPED);
	}
	
	public boolean isSupervisor() {
		short psr = lc3.getRegister(Register.PSR); 
		return (psr & BITMASK_PRIVILEGE) == 0;
	}
	
	public int getPriority() {
		short psr = lc3.getRegister(Register.PSR);
		int prio = (psr & BITMASK_PRIORITY) >> BIT_PRIORITY;
		return prio;
	}

	public boolean isClockEnabled() {
		short mcr = lc3.getMemory().getValue(Memory.ADDR_MCR);
		return (mcr & BITMASK_CLOCK_ENABLED) != 0;
	}

	public void setSupervisor(boolean b) {
		short psr = lc3.getRegister(Register.PSR);
		psr = (short) (psr & ~BITMASK_PRIVILEGE);
		lc3.setRegister(Register.PSR, psr);
	}

	public void setCC(short bits) {
		short psr = lc3.getRegister(Register.PSR);
		psr = (short) (psr & ~BITMASK_CC);
		psr = (short) (psr | bits);
		lc3.setRegister(Register.PSR, psr);
	}

	public void setPriority(int priority) {
		short psr = lc3.getRegister(Register.PSR);
		psr = (short) (psr & ~BITMASK_PRIORITY); // set priority bits to 0
		priority = priority << BIT_PRIORITY; // shift bits of new value to correct position
		priority = priority & BITMASK_PRIORITY; // to be sure set other bits to 0
		psr = (short) (psr | priority);
	}
}
