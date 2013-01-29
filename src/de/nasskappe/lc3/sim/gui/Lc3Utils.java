package de.nasskappe.lc3.sim.gui;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import de.nasskappe.lc3.sim.maschine.LC3;

public class Lc3Utils {

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
}
