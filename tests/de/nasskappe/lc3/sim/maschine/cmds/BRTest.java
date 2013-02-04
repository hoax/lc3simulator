package de.nasskappe.lc3.sim.maschine.cmds;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class BRTest {
	
	private static short b(String s) {
		return (short) Integer.parseInt(s.replaceAll(" ", ""), 2);
	}
	
	@Test
	public void testPNZ() {
		BR br = new BR();
		br.init(b("0000 111 101010101"));
		
		assertTrue(br.isP());
		assertTrue(br.isN());
		assertTrue(br.isZ());
		assertEquals(-171, br.getPCOffset());
	}

	@Test
	public void testP() {
		BR br = new BR();
		br.init(b("0000 001 000000101"));
		
		assertTrue(br.isP());
		assertFalse(br.isN());
		assertFalse(br.isZ());
		assertEquals(5, br.getPCOffset());
	}
	
	@Test
	public void testN() {
		BR br = new BR();
		br.init(b("0000 100 011111111"));
		
		assertFalse(br.isP());
		assertTrue(br.isN());
		assertFalse(br.isZ());
		assertEquals(255, br.getPCOffset());
	}
	
	@Test
	public void testZ() {
		BR br = new BR();
		br.init(b("0000 010 100000000"));
		
		assertFalse(br.isP());
		assertFalse(br.isN());
		assertTrue(br.isZ());
		assertEquals(-256, br.getPCOffset());
	}



}
