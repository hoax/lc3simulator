package de.nasskappe.lc3.sim.maschine.cmds;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import de.nasskappe.lc3.sim.maschine.Register;

public class NOTTest {

	private static short b(String s) {
		return (short) Integer.parseInt(s.replaceAll(" ", ""), 2);
	}

	@Test
	public void testValid() throws IllegalOpcodeException {
		NOT not = new NOT();
		not.init(b("1001 111 000 111111"));
		
		assertEquals(Register.R7, not.getDr());
		assertEquals(Register.R0, not.getSr());
	}
	
	@Test
	public void testInvalid() throws Exception {
		NOT not = new NOT();
		not.init(b("1001 111 000 111110"));
		assertTrue(not.isIllegal());
	}

}
