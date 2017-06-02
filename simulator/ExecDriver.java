package simulator;

import java.util.ArrayList;

public class ExecDriver {
	class Input{
		String unit, instr;
		long vj, vk;
		String qj, qk;
		String rob;
		
		Input(String unit, String instr, long vj, long vk, String qj, String qk, String rob){
			this.unit = unit;
			this.instr = instr;
			this.vj = vj;
			this.vk = vk;
			this.qj = qj;
			this.qk = qk;
			this.rob = rob;
		}
	}
	
	
	private ArrayList<Input> trace;
	public ExecDriver(){
		trace = new ArrayList<Input>();
		trace.add(new Input("lsu", "L.D", 200, 0, null, null, "ROB0")); // vk=R0, ROB0=F2
		trace.add(new Input("mul", "MUL.D", -1, 20, "ROB0", null, "ROB1")); // vj=F0, vk=F2
	}
	
	public static void main(){
		
	}
}
