package simulator;

public class ROBStatus {

	
	//private HashMap<String,Status> statusTable;
	static Status[] ROBTable;
	static int maxSize;
	static ROBStatus instance = null;
	static int numBusy;
	static int nextPosition;
	
	public static ROBStatus getInstance(Integer num_rob) {
		if (instance == null){
			maxSize = num_rob;
            instance = new ROBStatus();
            
		}
        return instance;
		
	}
	
	static ROBStatus getInstance(){
        if (instance == null)
            instance = new ROBStatus();
        return instance;
    }
	
	private ROBStatus(){
		ROBTable = new Status[maxSize];
		Instruction inst = null;
		Status iStatus = new Status(inst);
		iStatus.busy=false;
		numBusy = 0;
		nextPosition =0;
		for (int i=0; i<maxSize; i++)
			ROBTable[i]=iStatus;
	}
	
	public static Status getStatus(String robTag){
		return ROBTable[Integer.parseInt(robTag.substring(3,robTag.length()))];
	}
	
	static boolean isFull(){
		for (int i=0; i<ROBTable.length; i++)
			if (!ROBTable[i].busy)
				return false;
		return true;
	}
	
	static boolean isEmpty(){
		for (int i=0; i<ROBTable.length; i++)
			if (ROBTable[i].busy)
				return false;
		return true;
	}
	
	/* Insert assumes that you have checked that the ROB has space
		If the position is about to be greater than the ROBTable, then
		set the position to the initial position
	*/
	static String insert(Instruction ins){
		
		nextPosition = nextPosition%maxSize;
		
		ROBTable[nextPosition].flag=false;
		ROBTable[nextPosition].dest=null;
		ROBTable[nextPosition].value=0;
		ROBTable[nextPosition].state=null;
		
		Status status = new Status(ins);
		status.busy=true;
		status.state = "Issued";
		if (ins.opcode.equals("LD") || ins.opcode.equals("L.D"))
			status.dest = ins.rt;
		else
			status.dest = ins.rd;
		ROBTable[nextPosition]=status;
		String robTag = "ROB" + String.valueOf(nextPosition);
		nextPosition++;
		numBusy++;
		return robTag;
	}

	static Instruction release(int commitPointer){
		ROBTable[commitPointer].busy=false;
		numBusy--;
		return ROBTable[commitPointer].instruction;
	}
	
	static Status getStatus(int index){
		return ROBTable[index];
	}
	
	static void print(){
		System.out.println("ROB Status: ");
		for (int i=0; i<ROBTable.length; i++)
			if (ROBTable[i].instruction!=null && (ROBTable[i].busy == true))
				System.out.print("ROB"+i+"->"+ROBTable[i].instruction.instruction+" , ");
		System.out.println(" ");
	}

	
	
}