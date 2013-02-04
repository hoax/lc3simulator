package de.nasskappe.lc3.sim.maschine.cmds;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import de.nasskappe.lc3.sim.maschine.Register;

public class STTest {

	private static short b(String s) {
		return (short) Integer.parseInt(s.replaceAll(" ", ""), 2);
	}

	@Test
	public void test_positive_offset() {
		ST st = new ST();
		st.init(b("0011 111 0 1111 1111"));
		
		assertEquals(Register.R7, st.getSr());
		assertEquals(0xFF, st.getPCOffset());
	}

	@Test
	public void test_negative_offset() {
		ST st = new ST();
		st.init(b("0011 000 1 1111 1101"));
		
		assertEquals(Register.R0, st.getSr());
		assertEquals(-3, st.getPCOffset());
	}

}
