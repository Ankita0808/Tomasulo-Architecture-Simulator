package simulator;
//ISA
import java.util.*;

class ISA{
	static ISA instance = null;
	private static Map<String, String> isaMap = new HashMap<String, String>();
	private static String [] INT = new String[]{"AND", "ANDI", "OR", "ORI", "SLT", "SLTI", "DADD", "DADDI", "DSUB"};
	private static String [] MULT = new String[]{"DMUL"};
	private static String [] LSU = new String[]{"LD", "L.D", "SD", "S.D"};
	private static String [] FPU = new String[]{"ADD.D", "SUB.D", "MUL.D", "DIV.D"};
	private static String [] BU = new String[]{"BEQZ", "BNEZ", "BEQ", "BNE"};
	//Key is the instruction, and the unit is the value
	static ISA getInstance(){
		if (instance==null){
			instance = new ISA();
		}
		return instance;
	}
	
	private ISA(){
		for (int i=0; i<INT.length; i++){
			if(i<INT.length)
				isaMap.put(INT[i],"INT");
			if(i<MULT.length)
				isaMap.put(MULT[i],"MULT");
			if(i<LSU.length)
				isaMap.put(LSU[i],"LSU");
			if(i<FPU.length)
				isaMap.put(FPU[i],"FPU");
			if(i<BU.length)
				isaMap.put(BU[i],"BU");
		}
	}// End of Constructor
	public static String getUnit(String opcode){
		return isaMap.get(opcode);
	}
	

}//end of ISA