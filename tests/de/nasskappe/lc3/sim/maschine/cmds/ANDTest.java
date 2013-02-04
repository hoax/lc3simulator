package de.nasskappe.lc3.sim.maschine.cmds;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import de.nasskappe.lc3.sim.maschine.Register;

public class ANDTest {

	private static short b(String s) {
		return (short) Integer.parseInt(s.replaceAll(" ", ""), 2);
	}

	@Test
	public void test_two_registers()  {
		AND and = new AND();
		and.init(b("0101 010 001 0 00 000"));
		
		assertEquals(Register.R2, and.getDr());
		assertEquals(Register.R1, and.getSr1());
		assertEquals(Register.R0, and.getSr2());
		assertEquals(-1, and.getImm());

	}

	@Test
	public void test_register_and_immediate() throws Exception {
		AND and = new AND();
		and.init(b("0101 111 101 1 10000"));
		assertEquals(Register.R7, and.getDr());
		assertEquals(Register.R5, and.getSr1());
		assertEquals(null, and.getSr2());
		assertEquals(-16, and.getImm());
	}

	@Test
	public void test_invalid_opcode() {
		AND and = new AND();
		and.init(b("0001 111 101 0 11 000"));
		assertTrue(and.isIllegal());
	}
	
}
