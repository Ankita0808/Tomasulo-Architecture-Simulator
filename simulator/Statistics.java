package simulator;

import java.io.FileWriter;
//Statistics Collection
import java.io.IOException;

class Statistics{
	static Statistics instance = null;
	static private String final_path;
	
	static Statistics getInstance(){
		if (instance==null)
			instance = new Statistics();
		
		return instance;
	}
	static int numberCycles=0;
	static int numberWB=0;
	static int numberCommits=0;
	static int ROBstalls=0;
	static int RSstalls=0;
	static int totalStalls=0;
	static int total_branches=0;
	static int mispredicted_br=0;
	
	public Statistics(){
	}
	
	public static void incrementStalls(){
		totalStalls++;
	}
	public static void incrementRSStall(){
		RSstalls++;
	}
	public static void incrementROBStall(){
		ROBstalls++;
	}
	public static void incrementCycles(){
		numberCycles++;
	}
	public static void increaseWB(int i){
		numberWB=numberWB+i;
	}
	public static void increaseCommit(int i){
		numberCommits=numberCommits+i;
	}
	public static void increaseBranches(int i){
		total_branches=total_branches+i;
	}
	public static void increaseMisBranches(int i){
		mispredicted_br=mispredicted_br+i;
	}
	public static void print(String params){
		System.out.println("\n=======Simulation Summary=======");
		System.out.println("Number of Cycles: "+numberCycles);
		System.out.println("Number of Write Backs: "+numberWB);
		System.out.println("Number of Commits: "+numberCommits);
		System.out.println("Number of Total Stalls: "+totalStalls);
		System.out.println("Number of ROB Stalls: "+ROBstalls);
		System.out.println("Number of RSstalls: "+RSstalls);
		System.out.println("Number of Total Branches: "+total_branches);
		System.out.println("Number of Mispredicted Branches: "+mispredicted_br);
		System.out.println("Number of Misprediction rate: "+(float)mispredicted_br/total_branches);
		
		FileWriter writer;
		try {
			writer = new FileWriter(params+".csv",true);
			writer.append(new Float((float)numberCycles/numberCommits).toString()+","); // CPI
			writer.append(new Integer(totalStalls).toString()+","); // Total stalls
			writer.append(new Integer(ROBstalls).toString()+","); // ROB stalls
			writer.append(new Integer(RSstalls).toString()+","); // RS stalls
			writer.append(new Float((float)mispredicted_br/total_branches).toString()); // Mispredict rate
			writer.append("\n"); // Endline
			writer.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
}