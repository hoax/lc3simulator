package de.nasskappe.lc3.sim.gui;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import de.nasskappe.lc3.sim.maschine.CPU;

public class CpuUtils {

	private abstract class AbstractCpuAction implements Runnable {
		
		private Runnable postExecute;

		public AbstractCpuAction(Runnable postExecute) {
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
	
	private class RunAction extends AbstractCpuAction {
		public RunAction(Runnable postExecute) {
			super(postExecute);
		}

		@Override
		public void execute() {
			cpu.run();
		}
	}
	
	private class StepIntoAction extends AbstractCpuAction {
		public StepIntoAction(Runnable postExecute) {
			super(postExecute);
		}

		@Override
		public void execute() {
			cpu.step();
		}
	}
	
	private class StepOverAction extends AbstractCpuAction {
		public StepOverAction(Runnable postExecute) {
			super(postExecute);
		}

		@Override
		public void execute() {
			cpu.stepOver();
		}
	}
	
	private class StepReturnAction extends AbstractCpuAction {
		public StepReturnAction(Runnable postExecute) {
			super(postExecute);
		}

		@Override
		public void execute() {
			cpu.stepReturn();
		}
	}
	
	private ExecutorService executor = new ThreadPoolExecutor(1, 1, 0, TimeUnit.MINUTES, new LinkedBlockingQueue<Runnable>());
	private CPU cpu;
	
	public CpuUtils(CPU cpu) {
		this.cpu = cpu;
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
		cpu.setState(CPU.State.STOPPED);
	}
}
