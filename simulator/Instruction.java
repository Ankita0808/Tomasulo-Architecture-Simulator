package simulator;

//Instruction
//Instruction is an object that contains the PC and the instruction
public class Instruction{
	public int address;
	public String instruction;
	public String opcode;
	public String rd;
	public String rs;
	public String rt;
	public int immediate;
	public String FU;
	public int offset;
	public boolean flag;
	public int predicted_target;
	public Instruction(int pc, String i){
		address = pc;
		instruction =i;
		opcode = null;
		FU = null;
		rd = null;
		rs = null;
		rt = null;
		immediate=0;
		offset=0;
		flag = false;
		predicted_target = 0;
	}
}