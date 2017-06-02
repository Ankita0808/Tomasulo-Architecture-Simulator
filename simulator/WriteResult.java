package simulator;

import java.util.HashMap;

public class WriteResult {
	private static int WB = 0;
	private static int COMMIT = 1;
	
	// cdb_priority indicates which operation
	// will use the CDB in the current cycle
	// Write Back and Commit take turns in using 
	// the CDB
	private int cdb_priority = WB;
	
	private HashMap<String,Status> priorityWB;
	
	private int nextCommit;
	
	static WriteResult instance = null;
	
	static WriteResult getInstance(){
		if(instance == null){
			instance = new WriteResult();
		}
		return instance;
	}
	public WriteResult(){
		priorityWB = new HashMap<String, Status>();
		nextCommit = 0;
	}
	
	public void exec(){
//		System.out.println("Starting the WriteResult Stage");
		CDB cdb = CDB.getInstance();
		
//		System.out.println(" - Clearing CDB...");
		cdb.clear();
		// If the Write Back has priority in the CDB
		String instr;
		if(cdb_priority == WB){
//			System.out.println(" - Writeback has priority on the CDB!!");
			// Writes all the results from the priorityWB
			HashMap<String, Status> items = new HashMap<String, Status>(priorityWB);
			for(String robTag : items.keySet())
				cdb.add(robTag, priorityWB.remove(robTag), WB);
			
			Statistics.increaseWB(cdb.size());
//			System.out.println(" - Writeback added "+cdb.size()+" items to the CDB.");
			// If the priorityWB had less than 4 results,
			// use the remaining space in the CDB to add
			// the results from the Commit
			boolean hasCommitInstr = true;
			Status next_rob;
			
			while(cdb.size() < CDB.cdb_size && hasCommitInstr){
//				System.out.println(" - Adding extra info from Commit");
				next_rob = ROBStatus.getStatus(nextCommit);
				if(next_rob.busy && next_rob.state.equals("Write Result") && !cdb.getOrigin().containsKey("ROB"+nextCommit)){
					cdb.add("ROB"+nextCommit, next_rob, COMMIT);
					instr = next_rob.instruction.opcode;
					if(instr.equals("SD") || instr.equals("S.D")){
						Memory.store(Integer.parseInt(next_rob.dest), next_rob.value);
					}
					if(next_rob.instruction.FU.equals("BU")){
						BHT.updateBHT(next_rob.flag, next_rob.instruction);
					}
					
					ROBStatus.release(nextCommit);
					if (next_rob.instruction.FU.equals("LSU") && next_rob.instruction.opcode.contains("L"))
						RegStatus.unassign(next_rob.instruction.rt, "ROB"+nextCommit);
					else if (!next_rob.instruction.FU.equals("LSU") && !next_rob.instruction.FU.equals("BU"))
						RegStatus.unassign(next_rob.instruction.rd, "ROB"+nextCommit);
					
					Statistics.increaseCommit(1);
					
					if(next_rob.flag){
						flushSystem();
						PC.set(next_rob.instruction.predicted_target);
					}
					else
						nextCommit = (nextCommit+1)%ROBStatus.maxSize;
				}
				else
					hasCommitInstr = false;
			}
		}
		// If the ROB has priority in the CDB
		else{
//			System.out.println(" - Commit has priority on the CDB!!");
			// Commits up to 4 instructions from the ROB
			boolean hasCommitInstr = true;
			Status next_rob;
			while(cdb.size() < CDB.cdb_size && hasCommitInstr){
				next_rob = ROBStatus.getStatus(nextCommit);
				if(next_rob.busy && next_rob.state.equals("Write Result")){
					cdb.add("ROB"+nextCommit, next_rob, COMMIT);
					instr = next_rob.instruction.opcode;
					if(instr.equals("SD") || instr.equals("S.D")){
						Memory.store(Integer.parseInt(next_rob.dest), next_rob.value);
					}
					if(next_rob.instruction.FU.equals("BU")){
						BHT.updateBHT(next_rob.flag, next_rob.instruction);
					}
					
					ROBStatus.release(nextCommit);
					
					if (next_rob.instruction.FU.equals("LSU") && next_rob.instruction.opcode.contains("L"))
						RegStatus.unassign(next_rob.instruction.rt, "ROB"+nextCommit);
					else if (!next_rob.instruction.FU.equals("LSU") && !next_rob.instruction.FU.equals("BU"))
						RegStatus.unassign(next_rob.instruction.rd, "ROB"+nextCommit);
					
					Statistics.increaseCommit(cdb.size());
					
					if(next_rob.flag){
						flushSystem();
						PC.set(next_rob.instruction.predicted_target);
					}
					else
						nextCommit = (nextCommit+1)%ROBStatus.maxSize;
				}
				else
					hasCommitInstr = false;
			}
			
//			System.out.println(" - Commit added "+cdb.size()+" items to the CDB.");
			// If the ROB had less than 4 results available for committing,
			// use the remaining space in the CDB to add
			// the results from the writeback
			HashMap<String, Status> items = new HashMap<String, Status>(priorityWB);
			for(String robTag : items.keySet()){
				if(cdb.size() == CDB.cdb_size)
					return;
//				System.out.println(" - Adding extra info from Writeback");
				if(!cdb.getOrigin().containsKey(robTag)){
					cdb.add(robTag, priorityWB.remove(robTag), WB);
					Statistics.increaseWB(1);
				}
			}	
		}
		// If cdb_priority == 0, then it receives 1
		// otherwise, receives 0
//		System.out.println(" - CDB priority was "+cdb_priority);
		cdb_priority = cdb_priority == 0?1:0;
//		System.out.println(" - CDB priority now is "+cdb_priority);
	}
	
	private void flushSystem() {
		// Flush the ROB
		for(int rob_entry=0; rob_entry < ROBStatus.maxSize; rob_entry++)
			if(ROBStatus.getStatus(rob_entry).busy)
				ROBStatus.release(rob_entry);
		
		// Flush the priorityWB
		priorityWB.clear();
		
		// Flush the reservation stations and kill instructions in the FUs
		FunctionalUnit [] units = Execute.getInstance().getUnits();
		for(int i=0; i<units.length; i++){
			units[i].getStations().clear();
			if(units[i].fu_type.equals("lsu")){
				((LSU)units[i]).getLoadBuffer().clear();
				((LSU)units[i]).getStoreBuffer().clear();
			}
			units[i].instr_issued = false;
			units[i].cur_instruction = null;
			units[i].cur_instr_cycles = 0;
		}
		
		ROBStatus.nextPosition = nextCommit = 0;
		RegStatus.flush();
		
		// Flush the Decode Queue
		decode.flush();
		
		// Flush the Fetch Queue
		fetch.flush();
	}
	
	public HashMap<String, Status> getPriorityWB() {
		return priorityWB;
	}
	
	public void setPriorityWB(HashMap<String, Status> priorityWB) {
		this.priorityWB = priorityWB;
	}
	
	
}
