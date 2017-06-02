package simulator;

public class Status{
	boolean busy;
	String state;
	String dest;
	double value;
	Instruction instruction;
	boolean flag;
	public Status(Instruction ins){
		instruction=ins;
}
} 
