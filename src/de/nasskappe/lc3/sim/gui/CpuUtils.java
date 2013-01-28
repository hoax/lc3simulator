package de.nasskappe.lc3.sim.gui;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import de.nasskappe.lc3.sim.maschine.CPU;
import de.nasskappe.lc3.sim.maschine.cmds.ICommand;
import de.nasskappe.lc3.sim.maschine.cmds.JSR;
import de.nasskappe.lc3.sim.maschine.cmds.RET;
import de.nasskappe.lc3.sim.maschine.cmds.RTI;
import de.nasskappe.lc3.sim.maschine.cmds.TRAP;

public class CpuUtils {

	private abstract class AbstractCpuAction implements Runnable {
	}
	
	private class RunAction extends AbstractCpuAction {
		@Override
		public void run() {
			ICommand lastCmd = null;
			while(!pause && (!cpu.isBreakpointSetFor(cpu.getPC()) || lastCmd == null)) {
				lastCmd = cpu.step();
			}
			pause = false;
		}
	}
	
	private class StepIntoAction extends AbstractCpuAction {
		@Override
		public void run() {
			cpu.step();
		}
	}
	
	private class StepOverAction extends AbstractCpuAction {
		@Override
		public void run() {
			int oldPC = cpu.getPC();
			
			ICommand lastCmd = cpu.step();
			if (lastCmd.getClass() == JSR.class
					|| lastCmd.getClass() == TRAP.class) {
				while(!pause && (oldPC + 1) != cpu.getPC() && !cpu.isBreakpointSetFor(cpu.getPC())) {
					lastCmd = cpu.step();
				}
				pause = false;
			}
		}
	}
	
	private class StepReturnAction extends AbstractCpuAction {
		@Override
		public void run() {
			ICommand lastCmd = null;
			while(!pause && (!cpu.isBreakpointSetFor(cpu.getPC()) || lastCmd == null) 
					&& (lastCmd == null || !(lastCmd.getClass() == RET.class || lastCmd.getClass() == RTI.class))) {
				lastCmd = cpu.step();
			}
			pause = false;
		}
	}
	
	private ExecutorService executor = new ThreadPoolExecutor(1, 1, 0, TimeUnit.MINUTES, new LinkedBlockingQueue<Runnable>());
	private volatile boolean pause = false;
	private CPU cpu;
	
	public CpuUtils(CPU cpu) {
		this.cpu = cpu;
	}

	public void run() {
		executor.execute(new RunAction());
	}
	
	public void step() {
		executor.execute(new StepIntoAction());
	}
	
	public void stepOver() {
		executor.execute(new StepOverAction());
	}
	
	public void stepReturn() {
		executor.execute(new StepReturnAction());
	}

	public void pause() {
		pause = true;
	}
}
