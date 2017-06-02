//Instruction Cache implemented as object
package simulator;
import java.util.*;
import java.io.*;

public class Memory{
	private static Map<Integer, Double> dataSet = new HashMap<Integer, Double>();
	static Memory instance = null;
	
	static Memory getInstance(String name){
		if (instance ==null)
			instance = new Memory(name);
		return instance;
	}

	private Memory(String name){
		dataSet = new HashMap<Integer, Double>();
		String s = getClass().getName();
		int j = s.lastIndexOf(".");
		if(j > -1) s = s.substring(j + 1);
		s = s + ".class";
		String full_path = this.getClass().getResource(s).toString();
		int begin = full_path.lastIndexOf(":");
		int end = full_path.lastIndexOf("/");
		String final_path = full_path.substring(begin+1,end);
		
		name = final_path+"/"+name+".dat";
		try{
			FileReader inFile  = new FileReader(name);
			BufferedReader bufferedReader = new BufferedReader(inFile);
			boolean instructions=true;
			String line = bufferedReader.readLine();
			while (line!=null){
				if (line.contains("DATA")){
					line = bufferedReader.readLine();
					instructions=false;
				}
				if(!(instructions)&& !line.isEmpty()){
					String splitLine[] = line.split("=");
					Double value = Double.valueOf(splitLine[1]);
					int paren1 = splitLine[0].indexOf('(');
					int paren2 = splitLine[0].indexOf(')');
					line = splitLine[0].substring(paren1+1, paren2);
					Integer key = Integer.valueOf(line);
					dataSet.put(key, value);
				} 
				line = bufferedReader.readLine();
			}
			bufferedReader.close();
		}catch(FileNotFoundException ex){
			ex.printStackTrace();
		}catch(IOException ex){
			ex.printStackTrace();
		}
	}// end of constructor
	
	public static Double load(int memLocation){
		if(dataSet.containsKey(memLocation))
			return dataSet.get(memLocation);
		else
			return 0.0;
	}// end of load
	
	public static void store(int memLoc,double value){
		Integer memLocation = Integer.valueOf(memLoc);
//		Double val = Double.valueOf(String.valueOf(value));
		dataSet.put(memLocation, value);
	}// end of store
	
	public static void dump(){
		Map<Integer, Double> map = new TreeMap<Integer, Double>(dataSet);
		for (Integer key : map.keySet()){
			System.out.println("MEM["+key+"]="+ map.get(key));
		}
	}
	
	public static void dump(int i, int j){
		System.out.println("\n======Memory:======");
		Map<Integer, Double> map = new TreeMap<Integer, Double>(dataSet);
		for (Integer key : map.keySet()){
			if (key>=i && key<=j)
				System.out.println("MEM["+key+"]="+ map.get(key));
		}
	}

}// end of class