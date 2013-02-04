package de.nasskappe.lc3.sim.maschine.cmds;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import de.nasskappe.lc3.sim.maschine.Register;

public class STRTest {

	private static short b(String s) {
		return (short) Integer.parseInt(s.replaceAll(" ", ""), 2);
	}

	@Test
	public void test_positive_offset() {
		STR str = new STR();
		str.init(b("0111 111 101 011111"));
		
		assertEquals(Register.R7, str.getSr());
		assertEquals(Register.R5, str.getBaseR());
		assertEquals(31, str.getOffset());
	}

	@Test
	public void test_negative_offset() {
		STR str = new STR();
		str.init(b("0111 101 111 111111"));
		
		assertEquals(Register.R5, str.getSr());
		assertEquals(Register.R7, str.getBaseR());
		assertEquals(-1, str.getOffset());
	}

}
