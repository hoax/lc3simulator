package de.nasskappe.lc3.sim.maschine.cmds;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import de.nasskappe.lc3.sim.maschine.Register;

public class STITest {

	private static short b(String s) {
		return (short) Integer.parseInt(s.replaceAll(" ", ""), 2);
	}

	@Test
	public void test_positive_offset() throws IllegalOpcodeException {
		STI sti = new STI();
		sti.init(b("1011 111 0 1111 1111"));
		
		assertEquals(Register.R7, sti.getSr());
		assertEquals(0xFF, sti.getPCOffset());
	}

	@Test
	public void test_negative_offset() throws IllegalOpcodeException {
		STI sti = new STI();
		sti.init(b("1011 000 1 1111 1101"));
		
		assertEquals(Register.R0, sti.getSr());
		assertEquals(-3, sti.getPCOffset());
	}

}
