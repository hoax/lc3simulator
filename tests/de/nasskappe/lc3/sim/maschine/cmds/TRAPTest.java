package de.nasskappe.lc3.sim.maschine.cmds;

import static org.junit.Assert.*;

import org.junit.Test;

public class TRAPTest {

	@Test
	public void testTRAP()
		throws Exception {
		TRAP trap = new TRAP();
		trap.init((short)0xF012);
		
		assertEquals(0x12, trap.getTrap());
	}

}
