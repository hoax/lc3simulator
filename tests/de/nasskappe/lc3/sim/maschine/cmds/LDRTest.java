package de.nasskappe.lc3.sim.maschine.cmds;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import de.nasskappe.lc3.sim.maschine.Register;

public class LDRTest {

	private static short b(String s) {
		return (short) Integer.parseInt(s.replaceAll(" ", ""), 2);
	}

	@Test
	public void test_positive_offset() {
		LDR ldr = new LDR();
		ldr.init(b("0110 111 101 011111"));
		
		assertEquals(Register.R7, ldr.getDr());
		assertEquals(Register.R5, ldr.getBaseR());
		assertEquals(31, ldr.getOffset());
	}

	@Test
	public void test_negative_offset() {
		LDR ldr = new LDR();
		ldr.init(b("0110 101 111 111111"));
		
		assertEquals(Register.R5, ldr.getDr());
		assertEquals(Register.R7, ldr.getBaseR());
		assertEquals(-1, ldr.getOffset());
	}

}
