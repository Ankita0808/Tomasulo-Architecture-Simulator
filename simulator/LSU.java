package simulator;

import java.util.ArrayList;

public class LSU extends FunctionalUnit{ 
	private ArrayList<Station> loadBuffer; 
	private ArrayList<Station> storeBuffer;
	int store_station_index, load_station_index;
	
	private boolean hasMemAddr;
	
	public LSU(){
		fu_type = "lsu";
		num_reserv_stations = 3;
		latency = 1;
		loadBuffer = new ArrayList<Station>();
		storeBuffer = new ArrayList<Station>();
		hasMemAddr = false;
		cur_instruction = null;
	}
	
	public void exec(){
		if(cur_instruction == null){
			// Fetch the next instruction based on the statuses of the 
			// reservation stations
			cur_instruction = getNextInstr();
			if(cur_instruction != null){
				//update status table
				// we need the call to the status table here to 
				// indicate which instruction is using the reservation station
				hasMemAddr = false;
				cur_instr_cycles = latency - 1;
			}
		}
		// If there is something being executed
		else{
//			System.out.println(" --- Decrement remaining cycles of RS "+cur_instruction.getRs_name());
			cur_instr_cycles = cur_instr_cycles - 1; // Decrement the remaining cycles for the current instruction
			// If instruction finished execution, but did not wrote back its result
			if(cur_instr_cycles <= 0){
				//update status table
				// we need the call to the status table here to 
				// indicate that the instruction finished using the reservation station
				
				// computeResult will calculate the memory address of the operation
				if(!hasMemAddr){
					computeAddress(cur_instruction);
					hasMemAddr = true;
				}else{
					if(!cur_instruction.isResult_ready()){
						computeResult(cur_instruction);
						cur_instruction.setResult_ready(true);
					}
				}		
			}
		}
	}
	
	/*
	 * Because the LSU has 2 separate buffers (load and store) with reservation stations
	 * the getNextInstr method will be overwritten to iterate over both buffers. 
	 * <p>
	 * It is necessary to choose when to prioritize the requests for loads over stores.
	 * The basic idea is that, if the write buffer is not full, give priority to the
	 * load requests because they cause pipeline stalls. If the write buffer is full,
	 * execute the next store instruction.
	 * 
	 * @return station  the next reservation station that has all of its 
	 *                   operands available
	 */
	@Override
	protected Station getNextInstr() {
		Station st_load, st_store;
		if (storeBuffer.size() == 0 && loadBuffer.size() == 0)
			return null;
		
		if(storeBuffer.size() == 0){
			if(loadBuffer.get(0).rs_ready())
				return loadBuffer.get(0);
			else
				return null;
		}else if(loadBuffer.size() == 0){
			if(storeBuffer.get(0).rs_ready())
				return storeBuffer.get(0);
			else
				return null;
		}else{
			st_load = loadBuffer.get(0);
			st_store = storeBuffer.get(0);
			if(st_load.clock_issued < st_store.clock_issued)
				if(st_load.rs_ready())
					return st_load;
				else
					return null;
			else
				if(st_store.rs_ready())
					return st_store;
				else
					return null;
		}
	}
	
	/*
	 * Reads the data from CDB and check whether or not the 
	 * source operands in the load and store buffers are waiting for it
	 */
	@Override
	protected void readCDB(CDB cdb){
//		System.out.println(" - LSU reading CDB...");
		for(Station st : loadBuffer){
			// If the data in the CDB matches with the first source operand
			if(cdb.hasROB_WB(st.getQj())){
				st.setVj(cdb.getValue(st.getQj()));
				st.setQj(null);
			}
			// If the data in the CDB matches with the second source operand
			if(cdb.hasROB_WB(st.getQk())){
				st.setVk(cdb.getValue(st.getQk()));
				st.setQk(null);
			}
		}
		for(Station st : storeBuffer){
			// If the data in the CDB matches with the first source operand
			if(cdb.hasROB_WB(st.getQj())){
				st.setVj(cdb.getValue(st.getQj()));
				st.setQj(null);
			}
			// If the data in the CDB matches with the second source operand
			if(cdb.hasROB_WB(st.getQk())){
				st.setVk(cdb.getValue(st.getQk()));
				st.setQk(null);
			}
		}
	}
	
	/*
	 * Since the LSU unit servers both loads and stores, we need identify which
	 * instruction was added in order to add the reservation station into 
	 * the correct buffer
	 */
	public void addStation(Station st, String instr) {
//		System.out.println("Addding to buffers: "+st.getRs_name()+" "+instr);
		if(instr.equals("LD") || instr.equals("L.D"))
			loadBuffer.add(st);
		else
			storeBuffer.add(st);
	}
	private void computeAddress(Station st) {
		st.setMem_param(((Double)(st.getVj() + st.getMem_param())).intValue());
	}
	
	void computeResult(Station st) {
		if((st.getInstruction().equals("LD")) || (st.getInstruction().equals("L.D"))){
			st.setResult(Memory.load(st.getMem_param()));
		}
		else
			st.setResult(st.getVk());
	}

	public ArrayList<Station> getLoadBuffer() {
		return loadBuffer;
	}
	public ArrayList<Station> getStoreBuffer() {
		return storeBuffer;
	}
	
	@Override
	public void dumpStations(){
//		System.out.println("Load Buffers");
		Station.dumpHeaders();
		for(Station st : loadBuffer){
			st.dumpContents();
		}
//		System.out.println("Store Buffers");
		Station.dumpHeaders();
		for(Station st : storeBuffer){
			st.dumpContents();
		}
	}

	public void setLoadBuffer(ArrayList<Station> stations) {
		loadBuffer = stations;
		
	}

	public void setStoreBuffer(ArrayList<Station> stations) {
		storeBuffer = stations;
		
	}
	
	
	
}
