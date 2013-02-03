package de.nasskappe.lc3.sim.maschine.cmds;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class RTITest {

	private static short b(String s) {
		return (short) Integer.parseInt(s.replaceAll(" ", ""), 2);
	}

	@Test
	public void testValid() throws IllegalOpcodeException {
		RTI ret = new RTI();
		ret.init(b("1000 000 000 000000"));
	}

	@Test
	public void testInvalid() throws Exception {
		RTI ret = new RTI();
		ret.init(b("1000 000 100 000000"));
		assertTrue(ret.isIllegal());
	}
	
}
