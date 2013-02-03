package de.nasskappe.lc3.sim.maschine.cmds;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import de.nasskappe.lc3.sim.maschine.Register;

public class LEATest {

	private static short b(String s) {
		return (short) Integer.parseInt(s.replaceAll(" ", ""), 2);
	}

	@Test
	public void test_positive_offset() throws IllegalOpcodeException {
		LEA lea = new LEA();
		lea.init(b("1110 111 0 1111 1111"));
		
		assertEquals(Register.R7, lea.getDr());
		assertEquals(0xFF, lea.getPCOffset());
	}

	@Test
	public void test_negative_offset() throws IllegalOpcodeException {
		LEA lea = new LEA();
		lea.init(b("1110 000 1 1111 1101"));
		
		assertEquals(Register.R0, lea.getDr());
		assertEquals(-3, lea.getPCOffset());
	}

}
