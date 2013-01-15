package de.nasskappe.lc3.sim.maschine.cmds;

import org.junit.Test;

public class RETTest {

	private static short b(String s) {
		return (short) Integer.parseInt(s.replaceAll(" ", ""), 2);
	}

	@Test
	public void testValid() throws IllegalOpcodeException {
		RET ret = new RET();
		ret.init(b("1100 000 111 000000"));
	}

	@Test(expected=IllegalOpcodeException.class)
	public void testInvalid() throws Exception {
		RET ret = new RET();
		ret.init(b("1100 010 111 000000"));
	}
	
}
