package de.nasskappe.lc3.sim.maschine.cmds;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import de.nasskappe.lc3.sim.maschine.Lc3Exception;
import de.nasskappe.lc3.sim.maschine.Register;

public class ADDTest {
	
	private static short b(String s) {
		return (short) Integer.parseInt(s.replaceAll(" ", ""), 2);
	}

	@Test
	public void test_two_registers() throws Lc3Exception {
		ADD add = new ADD();
		add.init(b("0001 010 000 0 00 001"));
		assertEquals(Register.R2, add.getDr());
		assertEquals(Register.R0, add.getSr1());
		assertEquals(Register.R1, add.getSr2());
		assertEquals(-1, add.getImm());
	}
	
	@Test
	public void test_register_and_immediate() throws Exception {
		ADD add = new ADD();
		add.init(b("0001 111 101 1 10000"));
		assertEquals(Register.R7, add.getDr());
		assertEquals(Register.R5, add.getSr1());
		assertEquals(null, add.getSr2());
		assertEquals(-16, add.getImm());
	}

	@Test
	public void test_invalid_opcode() {
		ADD add = new ADD();
		add.init(b("0001 111 101 0 11 000"));
		assertTrue(add.isIllegal());
	}
}
