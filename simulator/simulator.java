package simulator;
//Simulator
import java.util.*;

public class simulator{
	public simulator(){
	}

	public static void main (String[] args){ //argument use this to send in a file name
		String program = args[args.length-1];
		String simulator_params = "";
		HashMap<String,List<Integer>> default_options = new HashMap<String,List<Integer>>();
		default_options.put("NF", Arrays.asList(4)); // # of simultaneous fetches
		default_options.put("NQ", Arrays.asList(8)); // size of fetch queue
		default_options.put("ND", Arrays.asList(4)); // # of simultaneous decodes
		default_options.put("NI", Arrays.asList(8)); // size of decode queue
		default_options.put("NW", Arrays.asList(4)); // # of simultaneous issues
		default_options.put("NR", Arrays.asList(16)); // # of ROBs
		default_options.put("NC", Arrays.asList(4)); // CDB size
		default_options.put("NB", Arrays.asList(4)); // # of instructions that can be simultaneously written to the ROB
		
		
		HashMap<String,List<Integer>> input_args = parseInputs(args);
		for(String param : input_args.keySet())
			default_options.put(param, input_args.get(param));
		
		for(String param : default_options.keySet())
			if(param.length() == 2){
				simulator_params+= (param+"="+default_options.get(param).get(0)+"-");
			}
		
		InstructionCache.getInstance(program);
		Memory.getInstance(program);
		fetch.getInstance(default_options.get("NF").get(0), default_options.get("NQ").get(0));
		BHT bht = BHT.getInstance();
		decode.getInstance(default_options.get("ND").get(0), default_options.get("NI").get(0));
		Issue.getInstance(default_options.get("NW").get(0));
		ROBStatus.getInstance(default_options.get("NR").get(0));
		RegFile.getInstance();
		PC.getInstance();
		RegStatus.getInstance();
		Execute.getInstance(default_options.get("NB").get(0));
		Statistics.getInstance();
		
		CDB.getInstance(default_options.get("NC").get(0));
		//boolean incrementPC = false;
		while (PC.get()<InstructionCache.instructionSet.size() || 
				!(fetch.fetchQueue.isEmpty()) || 
				!(decode.decodeQueue.isEmpty()) || 
				Execute.getInstance().hasInstruction() ||
				!ROBStatus.isEmpty())
				{
			WriteResult.getInstance().exec();
			RegFile.updateValues();
			Execute.getInstance().exec();
			if (!decode.decodeQueue.isEmpty()){
				Issue.next();
			}
			if(!fetch.fetchQueue.isEmpty()){
				decode.pull();
			}
			if (PC.get()<InstructionCache.instructionSet.size()){
				fetch.pull();
			}
			
			fetch.print();
			decode.print();
			//RegStatus.print();
			//ROBStatus.print(); 
			Statistics.incrementCycles();
		}
		
		if(default_options.containsKey("dump_branch"))
			BHT.dump();
		if(default_options.containsKey("dump_regs"))
			RegFile.dump();
		if(default_options.containsKey("dump_mem"))
			Memory.dump(default_options.get("dump_mem").get(0),default_options.get("dump_mem").get(1));
		
		Statistics.print(simulator_params);
	}
	
	public static HashMap<String,List<Integer>> parseInputs(String []args){
		final HashMap<String, List<Integer>> params = new HashMap<String,List<Integer>>();
		String arg = "";
		List<Integer> options = null;
		for (int i = 0; i < args.length; i++) { // -1 because we skip the last parameter, corresponding to the program to be executed
		    final String a = args[i];

		    if (a.charAt(0) == '-') {
		        if (a.length() < 3) {
		        	if(a.equals("-h") || a.equals("-help")){
		        		printUsage();
		        		System.exit(0);
		        	}
		            System.err.println("Error at argument " + a);
		            System.exit(0);
		        }
		        
		        if(!a.equals("-dump_regs") && !a.equals("-dump_branch")){
			        options = new ArrayList<>();
			        arg = a.substring(1);
			        params.put(a.substring(1), options);
		        }
		        else{
		        	options = null;
		        	params.put(a.substring(1), options);
		        }
		    }
		    else if (options != null && options.size() == 0){
		        options.add(Integer.parseInt(a));
		    }else if(options != null && options.size() == 1 && arg.equals("dump_mem"))
		    	options.add(Integer.parseInt(a));
		}
		return params;
	}

	private static void printUsage() {
		System.out.println("Usage: java simulator/simulator [options] {programName}");
		
	}
}//End of Simulator