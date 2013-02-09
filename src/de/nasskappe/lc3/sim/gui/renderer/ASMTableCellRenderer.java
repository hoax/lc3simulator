package de.nasskappe.lc3.sim.gui.renderer;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTable;

import de.nasskappe.lc3.sim.maschine.LC3;
import de.nasskappe.lc3.sim.maschine.Register;
import de.nasskappe.lc3.sim.maschine.cmds.ADD;
import de.nasskappe.lc3.sim.maschine.cmds.AND;
import de.nasskappe.lc3.sim.maschine.cmds.BR;
import de.nasskappe.lc3.sim.maschine.cmds.ICommand;
import de.nasskappe.lc3.sim.maschine.cmds.ICommandVisitor;
import de.nasskappe.lc3.sim.maschine.cmds.JSR;
import de.nasskappe.lc3.sim.maschine.cmds.LD;
import de.nasskappe.lc3.sim.maschine.cmds.LDI;
import de.nasskappe.lc3.sim.maschine.cmds.LDR;
import de.nasskappe.lc3.sim.maschine.cmds.LEA;
import de.nasskappe.lc3.sim.maschine.cmds.NOT;
import de.nasskappe.lc3.sim.maschine.cmds.JMP;
import de.nasskappe.lc3.sim.maschine.cmds.RTI;
import de.nasskappe.lc3.sim.maschine.cmds.Reserved;
import de.nasskappe.lc3.sim.maschine.cmds.ST;
import de.nasskappe.lc3.sim.maschine.cmds.STI;
import de.nasskappe.lc3.sim.maschine.cmds.STR;
import de.nasskappe.lc3.sim.maschine.cmds.TRAP;

public class ASMTableCellRenderer extends DefaultCodeTableCellRenderer {

	class ToStringVisitor implements ICommandVisitor {
		StringBuilder sb = new StringBuilder();
		
		private String toString(int v) {
			return Integer.toString(v);
		}
		
		private String toHexString(int v) {
			return String.format("0x%x", v & 0xFFFF);
		}
		
		@Override
		public Object visit(ADD cmd) {
			sb.setLength(0);
			sb.append(cmd.getASM());
			sb.append(" ");
			sb.append(cmd.getDr());
			sb.append(", ");
			sb.append(cmd.getSr1());
			sb.append(", ");
			if (cmd.getSr2() == null) {
				sb.append(toString(cmd.getImm()));
			} else {
				sb.append(cmd.getSr2());
			}
			
			return sb.toString();
		}

		@Override
		public Object visit(AND cmd) {
			sb.setLength(0);
			sb.append(cmd.getASM());
			sb.append(" ");
			sb.append(cmd.getDr());
			sb.append(", ");
			sb.append(cmd.getSr1());
			sb.append(", ");
			if (cmd.getSr2() == null) {
				sb.append(toString(cmd.getImm()));
			} else {
				sb.append(cmd.getSr2());
			}
			
			return sb.toString();
		}

		@Override
		public Object visit(BR cmd) {
			sb.setLength(0);
			if (cmd.getCode() == 0) {
				sb.append("NOP");
			} else {
				sb.append(cmd.getASM());
				if (cmd.isZ())
					sb.append("z");
				if (cmd.isN())
					sb.append("n");
				if (cmd.isP())
					sb.append("p");
				
				sb.append(" ");
				int address = cmd.getCodePosition() + 1 + cmd.getPCOffset();
				String label = lc3.getSymbolTable().findSymbolByAddress(address);
				if (label == null) {
					label = toHexString(address);
				}
				
				if (cmd.getCode() == (cmd.getCode() & 0xFF)) {
					label = label + "  '" + (char) cmd.getCode() + "'";
				}
				
				sb.append(label);
			}
			
			return sb.toString();
		}

		@Override
		public Object visit(JSR cmd) {
			sb.setLength(0);
			sb.append(cmd.getASM());
			sb.append(" ");
			if (cmd.getBaseR() == null) {
				int address = cmd.getCodePosition() + 1 + cmd.getPCOffset();
				String label = lc3.getSymbolTable().findSymbolByAddress(address);
				if (label == null) {
					label = toHexString(address);
				}
				
				sb.append(label);
			} else {
				sb.append(cmd.getBaseR());
			}
			
			return sb.toString();
		}

		@Override
		public Object visit(LD cmd) {
			sb.setLength(0);
			sb.append(cmd.getASM());
			sb.append(" ");
			sb.append(cmd.getDr());
			sb.append(", ");

			int address = cmd.getCodePosition() + 1 + cmd.getPCOffset();
			String label = lc3.getSymbolTable().findSymbolByAddress(address);
			if (label == null) {
				label = toHexString(address);
			}

			sb.append(label);
			
			return sb.toString();
		}

		@Override
		public Object visit(LDI cmd) {
			sb.setLength(0);
			sb.append(cmd.getASM());
			sb.append(" ");
			sb.append(cmd.getDr());
			sb.append(", ");
			
			int address = cmd.getCodePosition() + 1 + cmd.getPCOffset();
			String label = lc3.getSymbolTable().findSymbolByAddress(address);
			if (label == null) {
				label = toHexString(address);
			}

			sb.append(label);
			
			return sb.toString();
		}

		@Override
		public Object visit(LDR cmd) {
			sb.setLength(0);
			sb.append(cmd.getASM());
			sb.append(" ");
			sb.append(cmd.getDr());
			sb.append(", ");
			sb.append(cmd.getBaseR());
			sb.append(", ");
			sb.append(toString(cmd.getOffset()));
			
			return sb.toString();
		}

		@Override
		public Object visit(LEA cmd) {
			sb.setLength(0);
			sb.append(cmd.getASM());
			sb.append(" ");
			sb.append(cmd.getDr());
			sb.append(", ");

			
			int address = cmd.getCodePosition() + 1 + cmd.getPCOffset();
			String label = lc3.getSymbolTable().findSymbolByAddress(address);
			if (label == null) {
				label = toHexString(address);
			}
			
			sb.append(label);
			
			return sb.toString();
		}

		@Override
		public Object visit(NOT cmd) {
			sb.setLength(0);
			sb.append(cmd.getASM());
			sb.append(" ");
			sb.append(cmd.getDr());
			sb.append(", ");
			sb.append(cmd.getSr());
			
			return sb.toString();
		}

		@Override
		public Object visit(JMP cmd) {
			sb.setLength(0);
			
			if (cmd.getBaseR() == Register.R7) {
				sb.append("RET / JMP R7");
			} else {
				sb.append(cmd.getASM()).append(" ").append(cmd.getBaseR());
			}

			return sb.toString();
		}

		@Override
		public Object visit(RTI cmd) {
			sb.setLength(0);
			sb.append(cmd.getASM());

			return sb.toString();
		}

		@Override
		public Object visit(ST cmd) {
			sb.setLength(0);
			sb.append(cmd.getASM());
			sb.append(" ");
			sb.append(cmd.getSr());
			sb.append(", ");
			
			int address = cmd.getCodePosition() + 1 + cmd.getPCOffset();
			String label = lc3.getSymbolTable().findSymbolByAddress(address);
			if (label == null) {
				label = toHexString(address);
			}

			sb.append(label);
			
			return sb.toString();
		}

		@Override
		public Object visit(STI cmd) {
			sb.setLength(0);
			sb.append(cmd.getASM());
			sb.append(" ");
			sb.append(cmd.getSr());
			sb.append(", ");
			
			int address = cmd.getCodePosition() + 1 + cmd.getPCOffset();
			String label = lc3.getSymbolTable().findSymbolByAddress(address);
			if (label == null) {
				label = toHexString(address);
			}

			sb.append(label);
			
			return sb.toString();
		}

		@Override
		public Object visit(STR cmd) {
			sb.setLength(0);
			sb.append(cmd.getASM());
			sb.append(" ");
			sb.append(cmd.getSr());
			sb.append(", ");
			sb.append(cmd.getBaseR());
			sb.append(", ");
			sb.append(toString(cmd.getOffset()));
			
			return sb.toString();
		}

		@Override
		public Object visit(TRAP cmd) {
			sb.setLength(0);
			
			int address = lc3.getMemory().getValue(cmd.getTrap()) & 0xFFFF;
			String label = lc3.getSymbolTable().findSymbolByAddress(address);
			if (label == null) {
				label = toHexString(cmd.getTrap());
			}
			sb.append(cmd.getASM());
			sb.append(" ");
			sb.append(label);
			
			return sb.toString();
		}

		@Override
		public Object visit(Reserved cmd) {
			sb.setLength(0);
			sb.append(cmd.getASM());
			
			return sb.toString();
		}
		
	}
	
	private ToStringVisitor visitor = new ToStringVisitor();
	
	public ASMTableCellRenderer(LC3 lc3) {
		super(lc3);
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {
		Component comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus,
				row, column);
		ICommand cmd = (ICommand) value;
		String text = String.valueOf(cmd.accept(visitor));
		((JLabel) comp).setText(text);
		
		return comp;
	}
	
}
