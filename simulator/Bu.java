package simulator;

import java.util.ArrayList;

public class Bu extends FunctionalUnit{
	int target; //integer to store the target of branch
	boolean mispredicted;
	
	public Bu(){
		fu_type = "bu";
		num_reserv_stations = 2;
		latency = 1;
		stations = new ArrayList<Station>();
		cur_instruction = null;
		target = 0;
		mispredicted = false;
	}
	
	void computeResult(Station st){
		//change the long bits to double to compute the result
		double rs = st.getVj();
		double rt = st.getVk();
		double result = new Double(0);
		PC.getInstance();
		int pc = PC.get();
		
		this.target = 0;
		int immediate = st.getMem_param();
		switch(st.getInstruction()){
			case "BEQZ":
				target = pc + immediate;
				if (rs == 0) 		//check the condition
					st.setResult(1.0); //branch is taken
				else
					st.setResult(0.0); //branch not taken
				break;
			case "BNEZ":
				if (rs != 0.0) 		//check the condition
					st.setResult(1.0); //branch is taken
				else
					st.setResult(0.0); //branch not taken
				break;
				
			case "BEQ":
				target = pc + immediate;
				if (rs == rt) 		//check the condition
					st.setResult(1.0); //branch is taken
				else
					st.setResult(0.0); //branch not taken
				break;
			case "BNE":
				target = pc + immediate;
				if (rs != rt) 		//check the condition
					st.setResult(1.0); //branch is taken
				else
					st.setResult(0.0); //branch not taken
				break;
		}
	}
	
}
