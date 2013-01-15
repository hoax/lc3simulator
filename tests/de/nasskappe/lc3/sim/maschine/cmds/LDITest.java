package de.nasskappe.lc3.sim.maschine.cmds;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import de.nasskappe.lc3.sim.maschine.Register;

public class LDITest {

	private static short b(String s) {
		return (short) Integer.parseInt(s.replaceAll(" ", ""), 2);
	}

	@Test
	public void test_positive_offset() throws IllegalOpcodeException {
		LDI ld = new LDI();
		ld.init(b("1010 111 0 1111 1111"));
		
		assertEquals(Register.R7, ld.getDr());
		assertEquals(0xFF, ld.getPcOffset());
	}

	@Test
	public void test_negative_offset() throws IllegalOpcodeException {
		LDI ld = new LDI();
		ld.init(b("1010 000 1 1111 1111"));
		
		assertEquals(Register.R0, ld.getDr());
		assertEquals(-1, ld.getPcOffset());
	}

}
