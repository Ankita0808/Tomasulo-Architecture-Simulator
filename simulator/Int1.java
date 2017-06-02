package simulator;

import java.util.ArrayList;

public class Int1 extends FunctionalUnit{
	public Int1(){
		//creates and initializes reservation stations
		fu_type = "int1";
		num_reserv_stations = 2;
		latency = 1;
		stations = new ArrayList<Station>();
		cur_instruction = null;
	}
	
void computeResult(Station st){
		
		//change the long bits to double to compute the result 
		Integer rs = ((Double)st.getVj()).intValue();
		Integer rt = ((Double)st.getVk()).intValue();
		Integer immediate = st.getMem_param();
		switch(st.getInstruction()){
			case "AND":
				st.setResult(new Double(rs & rt));
				break;
			case "ANDI":
				st.setResult(new Double(rs & immediate));
				break;
			case "OR":
				st.setResult(new Double(rs | rt));
				break;
			case "ORI":
				st.setResult(new Double(rs | immediate));
				break;
			case "SLT":
				if(rs<rt)
					st.setResult(new Double(1));
				else
					st.setResult(new Double(0));
				break;
			case "SLTI":
				if(rs<immediate)
					st.setResult(new Double(1));
				else
					st.setResult(new Double(0));
				break;
			case "DADD":
				st.setResult(new Double(rs + rt));
				break;
			case "DADDI":
				st.setResult(new Double(rs + immediate));
				break;
			case "DSUB":
				st.setResult(new Double(rs - rt));
				break;
		}
	}
}
