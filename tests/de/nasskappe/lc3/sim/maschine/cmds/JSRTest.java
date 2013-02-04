package de.nasskappe.lc3.sim.maschine.cmds;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import de.nasskappe.lc3.sim.maschine.Register;

public class JSRTest {

	private static short b(String s) {
		return (short) Integer.parseInt(s.replaceAll(" ", ""), 2);
	}

	@Test
	public void test_positive() {
		JSR jsr = new JSR();
		jsr.init(b("0100  1 010 1010 1010"));
		
		assertEquals(682, jsr.getPCOffset());
		assertNull(jsr.getBaseR());
	}

	@Test
	public void test_negative() {
		JSR jsr = new JSR();
		jsr.init(b("0100 1 101 0101 0110"));
		
		assertEquals(-682, jsr.getPCOffset());
		assertNull(jsr.getBaseR());
	}
	
	@Test
	public void test_invalid() {
		JSR jsr = new JSR();
		jsr.init(b("0100 0 101 0101 0110"));
		assertTrue(jsr.isIllegal());
	}		

	@Test
	public void test_register() {
		JSR jsr = new JSR();
		jsr.init(b("0100 000 001 000000"));
		
		assertEquals(Register.R1, jsr.getBaseR());
		assertEquals(-1, jsr.getPCOffset());
	}

}
