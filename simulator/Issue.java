package simulator;
//Issue Stage
//Includes Decode Queue (NI)

class Issue{
	static Issue instance = null;
	static int sim_issues;
	public static Issue getInstance(Integer issues) {
		if(instance==null){
			instance = new Issue();
			sim_issues = issues;
		}
		return instance;
		
	}
	
	static Issue getInstance(){
		if(instance==null)
			instance = new Issue();
		return instance;
	}
	private Issue(){
		ISA.getInstance();
	}
	
	public static void next(){
		//Send the instruction to the functional unit used.
		//Stops when the same functional unit is being asked to be used twice
		//Stops when the functional unit RS is full
		//
		/*
				1. Look at the FU for the next instruction
				 - Check RS to see if Unit is available (false then break)
				 - Check ROBStatus if it is full (true then break)
				2. Get ROB tags or RegFile value for RS and RT
				3. Write Instruction into ROB
				4. Write(or overwrite) RegStatus Position
				5. Write into Reservation Station
				6. Continue Loop if less than 4 instructions have been issued
		*/
		if (decode.decodeQueue.size()<sim_issues){
			sim_issues=decode.decodeQueue.size();
		}
		
		for (int i=0; i<sim_issues && !decode.decodeQueue.isEmpty(); i++){
			Instruction instruction = decode.peek();
			String FU = Execute.getInstance().availableUnit(instruction.opcode);
			
			if (ROBStatus.isFull() && instruction.FU==null){
				Statistics.incrementROBStall();
				Statistics.incrementRSStall();
				Statistics.incrementStalls();
			}
			else if (ROBStatus.isFull()){
				Statistics.incrementROBStall();
				Statistics.incrementStalls();
			} else if(instruction.FU==null){
				Statistics.incrementRSStall();
				Statistics.incrementStalls();
			}
			
//			System.out.println("ROB Full: "+ROBStatus.isFull());
			//String FU = instruction.FU; //need to remove
			if (FU!=null && !ROBStatus.isFull()){
				//Check register status
				decode.release();
				String ROBTagRS = null;
				String ROBTagRT = null;
				double Vj=0;
				double Vk=0;
				String Qj=null;
				String Qk=null;
				
				//2. =====================================================================
				//Find RS tag or look in reg file
//				System.out.println("Instruction:  "+instruction.instruction);
//				System.out.println("RS:  "+instruction.rs);
//				System.out.println("RTL  "+instruction.rt);
				if(instruction.rs!=null){
					ROBTagRS=RegStatus.get(instruction.rs);
					//CHECKING for ROB Tag in RS
					if (ROBTagRS!=null){
						ROBStatus.getInstance();
						Status RSstatus = ROBStatus.getStatus(ROBTagRS);
						if (RSstatus.state.equals("Write Result"))
							Vj = RSstatus.value;
						else
							Qj = ROBTagRS;
					} else{
						if(instruction.rs.contains("R"))
							Vj=Double.valueOf(String.valueOf(RegFile.getR(instruction.rs)));
						else if (instruction.rs.contains("F")){
							Vj=RegFile.getF(instruction.rs);	
						}
					}
				}
			
				//Find RT tag or look in reg file
				if(instruction.rt!=null){
					ROBTagRT=RegStatus.get(instruction.rt);
					//CHECKING for ROB Tag in RT
					if (ROBTagRT!=null){
						ROBStatus.getInstance();
						Status RTstatus = ROBStatus.getStatus(ROBTagRT);
						if (RTstatus.state.equals("Write Result"))
							Vk = RTstatus.value;
						else
							Qk = ROBTagRT;
					} else{
						if(instruction.rt.contains("R"))
							Vk=Float.valueOf(String.valueOf(RegFile.getR(instruction.rt)));
						else if (instruction.rt.contains("F"))
							Vk=RegFile.getF(instruction.rt);	
					}
				}
				
				//3. =====================================================================
//				System.out.println("Next in issue "+instruction.instruction);
				ROBStatus.getInstance();
				String ROBTagRD = ROBStatus.insert(instruction);
				//4. =====================================================================
				if (instruction.FU.equals("LSU") && instruction.opcode.contains("L"))
					RegStatus.insert(ROBTagRD, instruction.rt);
				else if (!instruction.FU.equals("LSU") && !instruction.FU.equals("BU"))
					RegStatus.insert(ROBTagRD, instruction.rd);
				
				//5. =====================================================================
				Execute.getInstance().insertInstruction(FU, instruction.opcode, Vj, Vk, Qj, Qk, instruction.immediate, ROBTagRD);
				
			}else{
				break;
			}
		}
	}//End of next()
	
}//end of Issue