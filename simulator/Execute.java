package simulator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;


public class Execute {
	
	private static final Set<String> INT_INSTR = new HashSet<String>(Arrays.asList("AND", "ANDI", "OR", "ORI", "STL",
												"STLI", "DADD", "DADDI", "DSUB"));
	private static final Set<String> MUL_INSTR = new HashSet<String>(Arrays.asList("DMUL"));
	private static final Set<String> LSU_INSTR = new HashSet<String>(Arrays.asList("LD", "L.D", "SD", "S.D"));
	private static final Set<String> FPU_INSTR = new HashSet<String>(Arrays.asList("ADD.D", "SUB.D", "MUL.D", "DIV.D"));
	private static final Set<String> BU_INSTR = new HashSet<String>(Arrays.asList("BEQZ", "BNEZ", "BEQ", "BNE"));
	
	private Int int0, int1;
	private Mult mult;
	private Fpu fpu;
	private Bu bu;
	private LSU lsu;
	
	static Execute instance = null;
	private int clock;
	static private int priorityWBSize;
	
	
	ArrayList<String> freedStations;
	
	private FunctionalUnit [] units; 
	
	public static Execute getInstance(Integer nb) {
		if(instance == null){
			priorityWBSize = nb;
			instance = new Execute();
			
		}
		return instance;
	}
	
	static Execute getInstance(){
		if(instance == null)
			instance = new Execute();
		return instance;
	}
	/*
	 * Constructor for the Execute Stage
	 * <p>
	 * Creates all functional units
	 */
	public Execute(){
		
		freedStations = new ArrayList<String>();
		int0 = new Int();
		int0.fu_type = "int0";
		int1 = new Int();
		int1.fu_type = "int1";
		mult = new Mult();
		fpu = new Fpu();
		bu = new Bu();
		lsu = new LSU();
		units = new FunctionalUnit[]{int0,int1,mult,fpu,bu,lsu};
		
		
		clock = 0;
	}
	
	/*
	 * Executes one cycle of the Execute stage. 
	 * <p>
	 * Reads the information from  the CDB and simulates 
	 * the execution of all Functional Units.
	 */
	public void exec(){
//		System.out.println("Starting the Execution Stage");
		Station st;
		Status robStatus;
		String robTag;
		int predicted_target, actual_address;
		for (FunctionalUnit fu : units){
//			System.out.println(" - Executing FU: "+fu.fu_type);
			if(fu.fu_type.equals("lsu"))
				lsu.readCDB(CDB.getInstance());
			else
				fu.readCDB(CDB.getInstance());
			fu.exec();
			st = fu.getCur_instruction();
			if(fu.fu_type == "bu" && st != null){
				if(st.isResult_ready()){
					robTag = st.getDestination();
					ROBStatus.getInstance();
					robStatus = ROBStatus.getStatus(robTag);
					if(BHT.checkBHT(robStatus.instruction.address)){
						predicted_target = BHT.getTarget(robStatus.instruction);
						if(st.getResult() == 1.0){
							actual_address = BHT.computeTarget(robStatus.instruction);
							
							
						}
						else
							actual_address = robStatus.instruction.address+1;
						if(predicted_target != actual_address){
							robStatus.flag = true;
	//						BHT.updateBHT(true, robStatus.instruction);
						}
						
						robStatus.instruction.predicted_target = actual_address;
					}
				}
			}
			fu.instr_issued = false;
			//fu.dumpHeaders();
			//fu.dumpContents();
//			if(fu.fu_type == "lsu")
//				((LSU)fu).dumpStations();
//			else
//				fu.dumpStations();
		}
		setPriorityWriteBack();
		//dumpData();
		clock++;
	}
	
	/*
	 * Insert a new instruction into the Execute Stage
	 * <p>
	 * If the values of vj and/or vk are unknown, you can pass whatever value you want. 
	 * In those cases qj and/or qk will be different than null, so the readCDB method will
	 * check for the arrival of the correct data for either vj or vk.
	 * @return True if the instruction was successfully inserted; false, otherwise
	 */
	public boolean insertInstruction(String unit, String instr, double vj, double vk, String qj, String qk, int immediate, String rob){
		Station st;
		switch (unit) {
		case "int0":
			st = new Station("INT0"+int0.station_index);
			int0.station_index=(int0.station_index+1)%2;
			int0.addStation(st);
			break;
		
		case "int1":
			st = new Station("INT1"+int1.station_index);
			int1.station_index=(int1.station_index+1)%2;
			int1.addStation(st);
			break;
			
		case "mult":
			st = new Station("MUL"+mult.station_index);
			mult.station_index=(mult.station_index+1)%2;
			mult.addStation(st);
			break;
			
		case "fpu":
			st = new Station("FPU"+fpu.station_index);
			fpu.station_index=(fpu.station_index+1)%5;
			fpu.addStation(st);
			break;
			
		case "bu":
			st = new Station("BU"+bu.station_index);
			bu.station_index=(bu.station_index+1)%2;
			bu.addStation(st);
			break;
			
		case "lsu":
			if (instr.equals("LD") || instr.equals("L.D")){
				// Check for instructions in the store buffer 
				// that will change the same address.
				// If there is something there, what to do?
				st = new Station("LOAD"+lsu.load_station_index);
				lsu.load_station_index=(lsu.load_station_index+1)%3;
			}
			else{
				// Check the store buffer for instructions writing to the
				// same address. If there is something, flush it.
				st = new Station("STORE"+lsu.store_station_index);
				lsu.store_station_index=(lsu.store_station_index+1)%3;
			}
			lsu.addStation(st,instr);
			break;
			
		default:
			return false;
		}
		st.setInstruction(instr);
		st.setVk(vk);
		st.setVj(vj);
		st.setQj(qj);
		st.setQk(qk);
		st.setDestination(rob);
		st.setBusy(true);
		st.setMem_param(immediate);
		st.clock_issued = clock;
		return true;
	}
	
	/* Returns the functional unit that has reservation stations available for the
	 * instruction given in 'instr'. Returns null if there is not reservation station
	 * available, or if an instruction was already issued to the unit in the same clock cycle
	 * 
	 * @param instr String corresponding to the instruction to be inserted
	 * @return String representing the functional unit; null if there is not reservation station
	 * available or if an instruction was already issued to the unit in the same clock cycle
	 */
	public String availableUnit(String instr){
		if(!int0.instr_issued && INT_INSTR.contains(instr) && (int0.getStations().size() < int0.getNum_reserv_stations())){
			int0.instr_issued = true;
			return "int0";
		}
		if(!int1.instr_issued && INT_INSTR.contains(instr) && (int1.getStations().size() < int1.getNum_reserv_stations())){
			int1.instr_issued = true;
			return "int1";
		}
		if(!mult.instr_issued && MUL_INSTR.contains(instr) && (mult.getStations().size() < mult.getNum_reserv_stations())){
			mult.instr_issued = true;
			return "mult";
		}
		if(!fpu.instr_issued && FPU_INSTR.contains(instr) && (fpu.getStations().size() < fpu.getNum_reserv_stations())){
			fpu.instr_issued = true;
			return "fpu";
		}
		if(!bu.instr_issued && BU_INSTR.contains(instr) && (bu.getStations().size() < bu.getNum_reserv_stations())){
			bu.instr_issued = true;
			return "bu";
		}
		if(LSU_INSTR.contains(instr)){
			if(instr.equals("LD") || instr.equals("L.D")){
				if(!lsu.instr_issued && (lsu.getLoadBuffer().size() < lsu.getNum_reserv_stations())){
					lsu.instr_issued = true;
					return "lsu";
				}
			}
			else{
				if(!lsu.instr_issued && (lsu.getStoreBuffer().size() < lsu.getNum_reserv_stations())){
					lsu.instr_issued = true;
					return "lsu";
				}
			}
		}
		return null;
	}
	
	/* Iterates over the functional units, checks which ones have
	 * instructions that finished the execution and sets the priority
	 * write-back array from the WriteResult stage with the next (up to) 
	 * 4 results to be written back.
	 * <p>
	 * The priorityWB array contains the ROB tags that should be written 
	 * first in the CDB. The ROB tags are used because we use them when
	 * checking if the data on the CDB corresponds to a register dependency 
	 * of the reservation stations
	 * <p>
	 * The reservation stations that are being sent to the write back are deleted.
	 * The priorityWB is cleared by the WriteResult stage 
	 */
	public void setPriorityWriteBack(){
		// HashMap containing the ROB tag (key) and the result stored in the ROB (value)
		HashMap<String,Status> priorityWB = WriteResult.getInstance().getPriorityWB();
//		System.out.println(" - Setting priorityWB...");
		freedStations.clear();
		for (FunctionalUnit fu : units){
			if(priorityWB.size() == priorityWBSize)
				return;
			
			if(fu.fu_type.equals("lsu")){
				ArrayList<Station> stations = new ArrayList<Station>();
				for(Station st : ((LSU)fu).getLoadBuffer() ){
					if(st.isResult_ready()){
//						System.out.println(" --- Changing ROB Status to 'Write Result'");
						ROBStatus.getInstance();
						ROBStatus.getStatus(st.getDestination()).state = "Write Result";
						
//						System.out.println(" --- Setting result to ROB");
						ROBStatus.getStatus(st.getDestination()).value = st.getResult();
						
						if(st.getInstruction().equals("SD") || st.getInstruction().equals("S.D")){
//							System.out.println(" --- Setting the mem address to ROB");
							ROBStatus.getStatus(st.getDestination()).dest = Integer.toString(st.getMem_param());
						}
						
//						System.out.println(" --- Adding "+st.getDestination()+" to priorityWB");
						priorityWB.put(st.getDestination(), ROBStatus.getStatus(st.getDestination()));
						
						freedStations.add(st.getRs_name());
						fu.cur_instruction = null;
					}else{
						stations.add(st);
					}
				}
				// If there were no load instructions ready, check if there is 
				// some store instruction ready to be put in the Write Result stage
				if(stations.size() == ((LSU)fu).getLoadBuffer().size()){
					stations.clear();
					for(Station st : ((LSU)fu).getStoreBuffer() ){
						if(st.isResult_ready()){
//							System.out.println(" --- Changing ROB Status to 'Write Result'");
							ROBStatus.getInstance();
							ROBStatus.getStatus(st.getDestination()).state = "Write Result";
							
//							System.out.println(" --- Setting result to ROB");
							ROBStatus.getStatus(st.getDestination()).value = st.getResult();
							
							if(st.getInstruction().equals("SD") || st.getInstruction().equals("S.D")){
//								System.out.println(" --- Setting the mem address to ROB");
								ROBStatus.getStatus(st.getDestination()).dest = Integer.toString(st.getMem_param());
							}
							
//							System.out.println(" --- Adding "+st.getDestination()+" to priorityWB");
							priorityWB.put(st.getDestination(), ROBStatus.getStatus(st.getDestination()));
							
							freedStations.add(st.getRs_name());
							fu.cur_instruction = null;
						}else{
							stations.add(st);
						}
					}
					((LSU)fu).setStoreBuffer(stations);
				}
				else
					((LSU)fu).setLoadBuffer(stations);
			}
			else{
				ArrayList<Station> stations = new ArrayList<Station>();
				for (Station st : fu.getStations()){
					if(st.isResult_ready()){
						ROBStatus.getInstance();
						ROBStatus.getStatus(st.getDestination()).state = "Write Result";
						
//						System.out.println(" --- Setting result to ROB");
						ROBStatus.getStatus(st.getDestination()).value = st.getResult();
						
						if(st.getInstruction().equals("SD") || st.getInstruction().equals("S.D")){
//							System.out.println(" --- Setting the mem address to ROB");
							ROBStatus.getStatus(st.getDestination()).dest = Integer.toString(st.getMem_param());
						}
						
//						System.out.println(" --- Adding "+st.getDestination()+" to priorityWB");
						priorityWB.put(st.getDestination(), ROBStatus.getStatus(st.getDestination()));
						
						freedStations.add(st.getRs_name());
						fu.cur_instruction = null;
					}
					else
						stations.add(st);
				}
				fu.setStations(stations);
			}
		}
	}
	
	public FunctionalUnit[] getUnits() {
		return units;
	}
	
	public void dumpData(){
		System.out.println("PriorityWB set by Execute Stage:");
		for(String rs_name : freedStations)
			System.out.print(rs_name+" ");
		System.out.print("");
	}
	public boolean hasInstruction() {
		for(FunctionalUnit fu : units)
			if(fu.getStations().size() > 0)
				return true;
		if(lsu.getLoadBuffer().size() > 0 || lsu.getStoreBuffer().size() > 0)
			return true;
		return false;
	}
	
	
}
