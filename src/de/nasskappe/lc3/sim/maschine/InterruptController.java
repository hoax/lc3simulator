package de.nasskappe.lc3.sim.maschine;

import java.util.PriorityQueue;
import java.util.Queue;

public class InterruptController {

	public final static int INTERRUPT_PRIORITY_KEYBOARD = 4;
	
	public enum Interrupt {
		KEYBOARD(0x80);
		
		private int vector;
	
		Interrupt(int vector) {
			this.vector = vector;
		}
		
		public int getVector() {
			return vector;
		}
	}
	
	public class InterruptRequest implements Comparable<InterruptRequest> {
		private Interrupt interrupt;
		private int priority;
		
		public InterruptRequest(Interrupt interrupt, int priority) {
			this.interrupt = interrupt;
			this.priority = priority;
		}
		
		public Interrupt getInterrupt() {
			return interrupt;
		}
		
		public int getPriority() {
			return priority;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			result = prime * result
					+ ((interrupt == null) ? 0 : interrupt.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			InterruptRequest other = (InterruptRequest) obj;
			if (!getOuterType().equals(other.getOuterType()))
				return false;
			if (interrupt != other.interrupt)
				return false;
			return true;
		}

		private InterruptController getOuterType() {
			return InterruptController.this;
		}

		@Override
		public int compareTo(InterruptRequest o) {
			if (this.priority > o.priority)
				return -1;
			if (this.priority < o.priority)
				return 1;
			
			return 0;
		}
	}

	private Queue<InterruptRequest> pendingInterrupts;

	public InterruptController() {
		pendingInterrupts = new PriorityQueue<InterruptRequest>();
	}
	
	public void interruptKeyboard() {
		InterruptRequest ir = new InterruptRequest(Interrupt.KEYBOARD, INTERRUPT_PRIORITY_KEYBOARD);
		newInterrupt(ir);
	}
	
	private void newInterrupt(InterruptRequest ir) {
		if (!pendingInterrupts.contains(ir)) {
			pendingInterrupts.add(ir);
		}
	}

	public boolean isNextInterruptHigherThan(int currentPrio) {
		InterruptRequest ir = pendingInterrupts.peek();
		if (ir == null)
			return false;
		
		return ir.getPriority() > currentPrio;
	}
	
	public InterruptRequest getNextInterrupt() {
		return pendingInterrupts.poll();
	}
}
