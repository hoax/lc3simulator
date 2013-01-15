package de.nasskappe.lc3.sim.maschine.cmds;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import de.nasskappe.lc3.sim.maschine.Register;

public class JSRTest {

	private static short b(String s) {
		return (short) Integer.parseInt(s.replaceAll(" ", ""), 2);
	}

	@Test
	public void test_positive() throws IllegalOpcodeException {
		JSR jsr = new JSR();
		jsr.init(b("0100  1 010 1010 1010"));
		
		assertEquals(682, jsr.getOffset());
		assertNull(jsr.getBaseR());
	}

	@Test
	public void test_negative() throws IllegalOpcodeException {
		JSR jsr = new JSR();
		jsr.init(b("0100 1 101 0101 0110"));
		
		assertEquals(-682, jsr.getOffset());
		assertNull(jsr.getBaseR());
	}
	
	@Test(expected=IllegalOpcodeException.class)
	public void test_invalid() throws IllegalOpcodeException {
		JSR jsr = new JSR();
		jsr.init(b("0100 0 101 0101 0110"));
	}		

	@Test
	public void test_register() throws IllegalOpcodeException {
		JSR jsr = new JSR();
		jsr.init(b("0100 000 001 000000"));
		
		assertEquals(Register.R1, jsr.getBaseR());
		assertEquals(-1, jsr.getOffset());
	}

}