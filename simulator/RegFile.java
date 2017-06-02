package simulator;
import java.util.*;
import java.lang.*;
//Reg Files for Rs and Fs

class RegFile{
	
	static RegFile instance = null;
	static int [] aluRegs = new int[32];
	static double [] fpRegs = new double[32];
	
	static RegFile getInstance(){
		if(instance==null){
			instance = new RegFile();

		}
		return instance;
	}
	
	public RegFile(){
			Arrays.fill(aluRegs,0);
			Arrays.fill(fpRegs,0);
	}
	
	public static int getR(String reg){
		return aluRegs[Integer.parseInt(reg.substring(1,reg.length()))];
	}
	public static double getF(String reg){
		return fpRegs[Integer.parseInt(reg.substring(1,reg.length()))];
	}
	public static void writeR(String rd, int value){
		aluRegs[Integer.parseInt(rd.substring(1, rd.length()))]=value;
	}
	public static void writeF(String rd, double value){
		fpRegs[Integer.parseInt(rd.substring(1, rd.length()))]=value;
	}
	
	public static void updateValues(){
		ArrayList<Status> commitvalues = CDB.getInstance().getCommitValues();
		char type;
		for (int i=0; i<commitvalues.size(); i++){
			Status nextStatus = commitvalues.get(i);
			
			if (!nextStatus.instruction.FU.equals("BU") && !nextStatus.instruction.opcode.equals("SD") && !nextStatus.instruction.opcode.equals("S.D")){
				if(nextStatus.instruction.opcode.equals("LD") || nextStatus.instruction.opcode.equals("L.D"))
					type = nextStatus.instruction.rt.charAt(0);
				else
					type = nextStatus.instruction.rd.charAt(0);
				if(type == 'F' ){
					fpRegs[Integer.parseInt(nextStatus.dest.substring(1, nextStatus.dest.length()))]=nextStatus.value;
				}else{
					aluRegs[Integer.parseInt(nextStatus.dest.substring(1, nextStatus.dest.length()))]=((Double)nextStatus.value).intValue();
				}
			}
		}
	}
	public static void dump(){
		System.out.println("\n======Integer Registers:======");
		for (int i=0; i<aluRegs.length; i++)
			System.out.println("INT_REG["+i+"]= "+aluRegs[i]);
		
		System.out.println("\n======Floating Point Registers:======");
		for (int i=0; i<fpRegs.length; i++)
			System.out.println("FLOAT_REG["+i+"]= "+fpRegs[i]);
		
	}
	
}// End of Reg File