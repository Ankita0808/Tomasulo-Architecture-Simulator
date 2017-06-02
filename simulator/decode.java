//Decode Stage
//Includes Decode Queue (NI)
package simulator;
import java.util.*;

class decode{
	static decode instance = null;
	//static BHT instance = null
	static int capacity;
	static int sim_decodes;
	static Queue<Instruction> decodeQueue;
	static BHT bht;
	
	public static decode getInstance(Integer decodes, Integer queue_size) {
		if(instance==null){
			capacity = queue_size;
			sim_decodes = decodes;
			instance = new decode();
			
		}
		return instance;
		
	}
	
	static decode getInstance(){
		if(instance==null)
			instance = new decode();
		return instance;
	}
	
	private decode(){
			decodeQueue = new LinkedList<Instruction>();	
			ISA.getInstance();
	}
	
	public static boolean isEmpty(){
		return decodeQueue.isEmpty();
	}
	
	public static boolean pull(){
		if (fetch.fetchQueue.isEmpty()){
			return false;
		}
		for (int i=0; i<sim_decodes && !(fetch.fetchQueue.isEmpty()) && decodeQueue.size()<capacity; i++){
			Instruction instruction = fetch.release();
			if (instruction.instruction==null)
				break;
			decodeQueue.add(instruction);
			//Get and Set OPCODE and FU in INSTRUCTION ==============================
			String opcode;
			String[] instSplit = null;
			if(instruction.instruction.contains(":")){
				instSplit = instruction.instruction.split(":");
				instSplit = instSplit[1].split("_");
				opcode = instSplit[0];
			}else{
				instSplit = instruction.instruction.split("_");
				opcode = instSplit[0];
			}
			instruction.opcode=opcode;
			instruction.FU = ISA.getUnit(opcode);
			// ===============================================================
			// Assign Registers ==============================================
			if (instruction.FU.equals("LSU")){
				LSDecode(instruction, opcode, instSplit[1]);
			}else if (instruction.FU.equals("BU")){
				BUDecode(instruction, opcode, instSplit[1]);
			}else{
				ALUDecode(instruction, opcode, instSplit[1]);
			}
			
		}
		return true;
	}
	
	public static Instruction release(){
		return decodeQueue.poll();
	}
	
	public static Instruction peek(){
		return decodeQueue.peek();
	}
	
	private static void ALUDecode(Instruction instruction, String opcode, String RsandI){
		String [] regSplit = RsandI.split(",");
		instruction.rd=regSplit[0];
		instruction.rs=regSplit[1];
		if (opcode.charAt(opcode.length()-1)=='I'){
			instruction.immediate=Integer.parseInt(regSplit[2]);
		}else{
			instruction.rs = regSplit[1];
			instruction.rt = regSplit[2];
		}
	}//End of ALU DECODE
	
	private static void LSDecode(Instruction instruction, String opcode, String RsandO){
		String [] regSplit = RsandO.split(",");
		instruction.rt = regSplit[0];
		regSplit = regSplit[1].split("\\(");
		instruction.immediate = Integer.parseInt(regSplit[0]);
		regSplit = regSplit[1].split("\\)");
		instruction.rs = regSplit[0];	
	} //End of LS Decode
	
	private static void BUDecode(Instruction instruction, String opcode, String RsandI){
		String [] regSplit = RsandI.split(",");
		if(opcode.equals("BEQZ") || opcode.equals("BNEZ")){
			instruction.rs = regSplit[0];
		}else{
			instruction.rs = regSplit[0];
			instruction.rt = regSplit[1];
		}
		instruction.predicted_target = BHT.getTarget(instruction); //Changes the PC
		if (instruction.predicted_target != instruction.address+1){ //Then we need to perform a flush
			PC.set(instruction.predicted_target);
			fetch.flush();
		}
		
	}// End of BU Decode 

	public static void print(){
		Iterator<Instruction> printQueue = decodeQueue.iterator();
	}
	public static void flush(){
		decodeQueue.clear();
	}

	
	
} //End of Decode

