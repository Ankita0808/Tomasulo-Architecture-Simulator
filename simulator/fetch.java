package simulator;
import java.util.*;

//Fetch Gets the PC and Instruction, Places it into the Queue
public class fetch{
	static fetch instance = null;
	static int capacity;
	static int sim_fetches;
	static Queue<Instruction> fetchQueue;
	
	public static fetch getInstance(Integer fetches, Integer queue_size) {
		if (instance ==null){
			capacity = queue_size;
			sim_fetches = fetches;
			instance = new fetch();
			 
		}
		return instance;
		
	}
	
	static fetch getInstance (){
		if (instance ==null){
			instance = new fetch();
		}
		return instance;
	}
	
	private fetch(){
			fetchQueue = new LinkedList<Instruction>();
		}
	//Gets Next Instruction
	public static void pull(){
		
		Instruction instruction;
		for (int i=PC.get(); i<PC.get()+sim_fetches && fetchQueue.size()<capacity && InstructionCache.instructionSet.size() > PC.get(); i++){
			instruction = new Instruction(PC.get(),InstructionCache.next(PC.get()));
			fetchQueue.add(instruction);
			PC.set(PC.get()+1);
		}
	}
	
	public static Instruction peek(){
		return fetchQueue.peek();
	}
	
	public static Instruction release(){
			return fetchQueue.poll();
	}

	public static void flush(){
		fetchQueue.clear();
	}
	
	public static void print(){
		Iterator<Instruction> printQueue = fetchQueue.iterator();
	}
}
		
		