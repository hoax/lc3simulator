package de.nasskappe.lc3.sim.maschine;

import java.io.FileInputStream;
import java.io.IOException;

public class TestMain {

	public static void main(String ... args) throws IOException, Lc3Exception {
		CPU cpu = new CPU();
		
		FileInputStream input = new FileInputStream("3b.obj");
		int addr = input.read() << 8 | input.read();
		
		cpu.loadData(addr, input);
		
		cpu.writeMemory(0x3100, (short) 0x5555);
		cpu.setPC(0x3000);
		while (cpu.getPC() < 0x300B) {
			cpu.step();
		}
		int value = cpu.readMemory(0x3101) & 0xffff;
		System.out.printf("0x%04X - %d\n", value, value);
		System.out.println(Integer.toBinaryString(cpu.readMemory(0x3100)));
		System.out.println(Integer.toBinaryString(value));
	}
	
}
