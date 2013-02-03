package de.nasskappe.lc3.sim.maschine;

import java.io.FileInputStream;
import java.io.IOException;

public class TestMain {

	public static void main(String ... args) throws IOException, Lc3Exception {
		LC3 lc3 = new LC3();
		
		FileInputStream input = new FileInputStream("3b.obj");
		int addr = input.read() << 8 | input.read();
		
		lc3.getUtils().loadData(addr, input);
		
		lc3.getMemory().setValue(0x3100, (short) 0x5555);
		lc3.setPC(0x3000);
		while (lc3.getPC() < 0x300B) {
			lc3.step();
		}
		int value = lc3.getMemory().getValue(0x3101) & 0xffff;
		System.out.printf("0x%04X - %d\n", value, value);
		System.out.println(Integer.toBinaryString(lc3.getMemory().getValue(0x3100)));
		System.out.println(Integer.toBinaryString(value));
	}
	
}
