package simulator;
//Memory implemented as object
import java.util.*;
import java.io.*;

public class InstructionCache{
	static ArrayList<String> instructionSet;
	
	static InstructionCache instance = null;
	static InstructionCache getInstance(String name){
		if(instance ==null){
			instance = new InstructionCache(name);
		}
		return instance;
	}
	
	private InstructionCache(String name){
		instructionSet = new ArrayList<String>();
		String s = getClass().getName();
		int j = s.lastIndexOf(".");
		if(j > -1) s = s.substring(j + 1);
		s = s + ".class";
		String full_path = this.getClass().getResource(s).toString();
		int begin = full_path.lastIndexOf(":");
		int end = full_path.lastIndexOf("/");
		String final_path = full_path.substring(begin+1,end);
		
		name = final_path+"/"+name+".dat";
		System.out.println("Program File:  "+name);
		try{
			
			FileReader inFile  = new FileReader(name);
			BufferedReader bufferedReader = new BufferedReader(inFile);
			String line = bufferedReader.readLine();
			ISA.getInstance();
			while (!line.isEmpty() && line.length() > 2 && !line.contains("DATA")){
				if(!line.isEmpty() || line!=null){
					String sline[] = line.trim().split("\\s+");
					String newLine=null;
						
					for(int i=0; i<sline.length; i++){
						if (sline[i].contains(":"))
							newLine=sline[i];
						else if (ISA.getUnit(sline[i])!=null)
							if (newLine==null){
								newLine=sline[i]+'_';}
							else
								newLine=newLine+sline[i]+'_';
						else
							newLine=newLine+sline[i];
					}
					instructionSet.add(newLine);
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
	
	
	public static String next(int PC){
		if (PC>=instructionSet.size()){
			return null;
		}
		return instructionSet.get(PC);
	}// end of nextInstruction

}// end of class