package simulator;

import java.util.ArrayList;

public class Fpu extends FunctionalUnit{
	public Fpu(){
		//creates and initializes reservation stations
		fu_type = "fpu";
		num_reserv_stations = 5;
		latency = 4;
		stations = new ArrayList<Station>();
		cur_instruction = null;
	}
	
	void computeResult(Station st){
		
		//change the long bits to double to compute the result 
		double rs = st.getVj();
		double rt = st.getVk();
		double result = 0;
		switch(st.getInstruction()){
			case "ADD.D":
				result = rs + rt;
				break;
			case "SUB.D":
				result = rs - rt;
				break;
			case "MUL.D":
				result = rs * rt;
				break;
			case "DIV.D":
				result = rs / rt;
				break;
		}
		st.setResult(result);
	}
}
