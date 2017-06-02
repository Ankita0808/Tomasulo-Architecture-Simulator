package simulator;
//Branch History Table

public class BHT{

	static int [][]BHTable;
	static BHT instance = null;
	//BHT[address][0] contains active bit
	//BHT[address][1] contains instruction address
	//BHT[address][2] contains contains 2-bit counter
	//BHT[address][3] contains target address
	static int BHTsize = 512;
	
	static BHT getInstance(){
		if (instance==null){
			instance = new BHT();
		}
		return instance;
	}
	
	private BHT(){
		BHTable = new int[512][4];
		for (int i=0; i<BHTable.length; i++){
			for (int j=0; j<BHTable[i].length; j++){
				BHTable[i][j]=0;
			}
		}
	}

	public static int getTarget(Instruction instruction){ //False if branch not taken, True otherwise
		int target=instruction.address+1;//assumes the target is the next instruction, or not taken
		int index = instruction.address%BHTsize;
		if (checkBHT(instruction.address)){
			if (BHTable[index][1]== instruction.address && BHTable[index][2]>1){ //if the instructions matches the ful address, and the bit counter is greater than 1, take the branch
					target=BHTable[index][3];
			}
		}
		if(BHTable[index][0]==0){//initialize position in table
			initializeBranch(instruction);
		}
		return target;
	}
	
	public static void initializeBranch(Instruction instruction){
		int index = instruction.address%BHTsize;
		BHTable[index][0]=1;
		BHTable[index][1]=instruction.address;
		BHTable[index][3]=computeTarget(instruction);
	}
	
	public static int computeTarget(Instruction instruction){ 
		String[] sInstr =  instruction.instruction.split(",");
		String targetName = sInstr[sInstr.length-1];
		for (int i=1; !(i+instruction.address>=InstructionCache.instructionSet.size() && instruction.address-1<=0) ; i++){
			if (instruction.address+i<InstructionCache.instructionSet.size()){ //looking at future instructions
				if (InstructionCache.next(instruction.address+i).contains(":")){
					String[] checkInst = InstructionCache.next(instruction.address+i).split(":");
					if(checkInst[0].equals(targetName)){
						return instruction.address+i;
					}
				}
			}
			if (instruction.address-i>-1){ //looking at previous instructions
				if (InstructionCache.next(instruction.address-i).contains(":")){
					String[] checkInst = InstructionCache.next(instruction.address-i).split(":");
					if(checkInst[0].equals(targetName)){
						return instruction.address-i;
					}
				}
			}	
		}
		return -1;
	}
	
	//Checks BHT to make sure position is filled, returns false if it is not
	public static boolean checkBHT(int address){
		int index = address%BHTsize;
		if (BHTable[index][0]==1)
			return true;
		else
			return false;
	}

	public static void updateBHT(boolean flag, Instruction instruction){ //
		int index = instruction.address%BHTsize;
		Statistics.increaseBranches(1);
		if (flag==true && BHTable[index][0]==1 && BHTable[index][1]==instruction.address){ //if mispredicted
			if (BHTable[index][2]>1){
				BHTable[index][2]--;}
			else{
				BHTable[index][2]++;}
			Statistics.increaseMisBranches(1);
		}else if(BHTable[index][1]==instruction.address){
			if (BHTable[index][2]==2){
				BHTable[index][2]++;}
			if(BHTable[index][2]==1){
				BHTable[index][2]--;}
		}
	}
	
	public static void dump(){
		System.out.println("\n=======Branch Prediction Buffer=======");
		System.out.println("Note only printing entries with a valid bit");
		for (int i=0; i<BHTable.length; i++){
			if (BHTable[i][0]==1)
				System.out.println("BHT Prediction["+i+"]= " +BHTable[i][2]);
		}
	}


}//End of BHT