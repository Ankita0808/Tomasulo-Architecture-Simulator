package simulator;

import java.util.ArrayList;

public class Mult extends FunctionalUnit{
	public Mult(){
		//creates and initializes reservation stations
		fu_type = "mult";
		num_reserv_stations = 2;
		latency = 4;
		stations = new ArrayList<Station>();
		cur_instruction = null;
	}
	
	void computeResult(Station st){
		
		//change the long bits to double to compute the result 
		double rs = st.getVj();
		double rt = st.getVk();
		
		st.setResult(rs * rt);
	}
}
