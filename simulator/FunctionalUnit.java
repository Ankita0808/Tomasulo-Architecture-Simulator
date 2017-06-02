package simulator;

import java.util.ArrayList;

public class FunctionalUnit {
	protected String fu_type;
	protected int latency;
	protected int num_reserv_stations;
	protected String[] supported_instr;
	protected Station cur_instruction;
	protected int cur_instr_cycles;
	protected boolean instr_issued; // Indicates if, in a given cycle, a instruction was issued to one of its Reservation Stations
	int station_index;
	
	ArrayList<Station> stations = new ArrayList<Station>();
	/*
	 * Performs the generic execution of a Functional Unit. This is 
	 * common to all units.
	 * <p>
	 * If the unit is available, get the next instruction to execute.
	 * Otherwise, decrement the number of remaining cycles of the 
	 * current instruction.
	 * If the execution has finished, set result ready (in the RS) as true,
	 * and update the state of the ROB to 'Write Result'.
	 */

	public void exec(){
		// If no instruction is currently executing
		if(cur_instruction == null){
//			System.out.println(" --- No instruction executing. Fetching a new one... ");
			// Fetch the next instruction based on the statuses of the 
			// reservation stations
			cur_instruction = getNextInstr();
			if(cur_instruction != null){
				//update status table
				// we need the call to the status table here to 
				// indicate which instruction is using the reservation station
				cur_instr_cycles = latency - 1;
			}
		}
		// If there is something being executed
		else{
//			System.out.println(" --- Decrement remaining cycles of RS "+cur_instruction.getRs_name());
			cur_instr_cycles = cur_instr_cycles - 1; // Decrement the remaining cycles for the current instruction
			// If instruction finished execution, but did not wrote back its result
			if(cur_instr_cycles <= 0 && !cur_instruction.isResult_ready()){
//				System.out.println(" ---- RS finished");
				//update status table
				// we need the call to the status table here to 
				// indicate that the instruction finished execution
				computeResult(cur_instruction);
				cur_instruction.setResult_ready(true);
			}
		}
	}
	
	
	/*
	 * Returns the next instruction from the Reservation Stations
	 * 
	 * @return station  the next reservation station that has all of its 
	 *                   operands available
	 */
	protected Station getNextInstr() {
		for(Station st : stations){
			if(st.rs_ready()){
				st.stage = 0;
				return st;
			}
		}
		return null;
	}

	/*
	 * Override this function to implement the specific computation
	 * of each functional unit
	 */
	void computeResult(Station cur_instruction2) {
		
	}
	
	/*
	 * Reads the data from CDB and check whether or not the 
	 * source operands in the reservation stations are waiting for it
	 */
	protected void readCDB(CDB cdb){
//		System.out.println(" -- Reading CDB...");
		for(Station st : stations){
			// If the RS is busy
			if(st.isBusy()){
//				System.out.println(" --- Checking for data from Station "+st.getRs_name());
				// If the data in the CDB matches with the first source operand
				if(cdb.hasROB_WB(st.getQj())){
//					System.out.println(" ---- Found Vj from "+st.getQj());
					st.setVj(cdb.getValue(st.getQj()));
					st.setQj(null);
				}
				// If the data in the CDB matches with the second source operand
				if(cdb.hasROB_WB(st.getQk())){
//					System.out.println(" ---- Found Vk from "+st.getQk());
					st.setVk(cdb.getValue(st.getQk()));
					st.setQk(null);
				}
			}
		}
	}

	public int getNum_reserv_stations() {
		return num_reserv_stations;
	}

	public void setNum_reserv_stations(int num_reserv_stations) {
		this.num_reserv_stations = num_reserv_stations;
	}

	public Station getCur_instruction() {
		return cur_instruction;
	}

	public void setCur_instruction(Station cur_instruction) {
		this.cur_instruction = cur_instruction;
	}

	public ArrayList<Station> getStations() {
		return stations;
	}

	public void setStations(ArrayList<Station> stations) {
		this.stations = stations;
	}
	
	public void addStation(Station st) {
		this.stations.add(st);
	}
	
	public void dumpContents(){
		if(cur_instruction != null){
			System.out.print(Util.pad(cur_instruction.getRs_name(), 10, " "));
			System.out.print(Util.pad(Integer.toString(cur_instr_cycles), 12, " "));
			System.out.println(Util.pad(Boolean.toString(instr_issued), 8, " "));
		}else
			System.out.println("    No instruction executing");
	}
	
	public void dumpHeaders(){
        System.out.print(Util.pad("Cur_RS", 10, " "));
        System.out.print(Util.pad("CyclLeft", 12, " "));
        System.out.println(Util.pad("Issued", 8, " "));
    }
	
	public void dumpStations(){
		Station.dumpHeaders();
		for(Station st : stations){
			st.dumpContents();
		}
	}

}
