package de.nasskappe.lc3.sim.maschine.cmds;

import java.util.ArrayList;
import java.util.List;

public class CommandFactory {

	private List<Class<? extends ICommand>> mapping = new ArrayList<>(16);
	{
		mapping.add(BR.class); // 0000
		mapping.add(ADD.class); // 0001
		mapping.add(LD.class); // 0010
		mapping.add(ST.class); // 0011
		mapping.add(JSR.class); // 0100
		mapping.add(AND.class); // 0101
		mapping.add(LDR.class); // 0110
		mapping.add(STR.class); // 0111
		mapping.add(RTI.class); // 1000
		mapping.add(NOT.class); // 1001
		mapping.add(LDI.class); // 1010
		mapping.add(STI.class); // 1011
		mapping.add(RET.class); // 1100
		mapping.add(Reserved.class); // 1101
		mapping.add(LEA.class); // 1110
		mapping.add(TRAP.class); // 1111
	};
	
	public ICommand createCommand(short code, int codePosition) {
		int cmdNo = (code & 0xF000) >> 12;
		@SuppressWarnings("unchecked")
		Class<ICommand> clazz = (Class<ICommand>) mapping.get(cmdNo);
		try {
			ICommand cmd = clazz.newInstance();
			cmd.init(code);
			cmd.setCodePosition(codePosition);
			
			return cmd;
		} catch (Exception e) {
			//TODO handle exception
		}
		return null;
	}
}
