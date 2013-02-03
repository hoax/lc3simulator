package de.nasskappe.lc3.sim.maschine;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SymbolTable {

	Map<Integer, String> address2symbol;
	Map<String, Integer> symbol2address;
	
	public SymbolTable() {
		address2symbol = new HashMap<Integer, String>();
		symbol2address = new HashMap<String, Integer>();
	}
	
	public void add(String symbol, Integer address) {
		symbol = symbol.toUpperCase();
		address2symbol.put(address, symbol);
		symbol2address.put(symbol, address);
	}
	
	public String findSymbolByAddress(Integer address) {
		return address2symbol.get(address);
	}
	
	public Integer findAddressBySymbol(String symbol) {
		return symbol2address.get(symbol.toUpperCase());
	}
	
	public void clear() {
		address2symbol.clear();
		symbol2address.clear();
	}
	
	private File getSymFile(File file) {
		if (!file.getName().toLowerCase().endsWith(".sym")) {
			String name = file.getName();
			int idx = name.lastIndexOf(".");
			if (idx > 0) {
				name = name.substring(0, idx);
			}
			name = name + ".sym";
			return new File(file.getParentFile(), name);
		}
		return file;
	}
	
	public void addSymbolsFromFile(File file) {
		file = getSymFile(file);

		Reader reader;
		try {
			reader = new FileReader(file);
			addSymbolsFrom(reader);
			try {
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public void addSymbolsFrom(Reader input) {
		Pattern p = Pattern.compile("//\\s+(\\S+)\\s+(\\d+).*");
		BufferedReader reader = new BufferedReader(input);
		try {
			String line;
			while ((line = reader.readLine()) != null) {
				Matcher m = p.matcher(line);
				if (m.matches()) {
					String symbol = m.group(1);
					String addressString = m.group(2);
					Integer address = Integer.parseInt(addressString, 16);
					
					add(symbol, address);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void removeSymbolForAddress(int startAddress) {
		address2symbol.remove(startAddress);
		
		Iterator<Entry<String, Integer>> it = symbol2address.entrySet().iterator();
		while (it.hasNext()) {
			Entry<String, Integer> entry = it.next();
			if (entry.getValue() == startAddress) {
				it.remove();
			}
		}
	}
	
}
