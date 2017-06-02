package simulator;

public class Station {
	private boolean busy;
	private String rs_name;
	private String instruction;
	private double Vj, Vk;
	private String Qj, Qk;
	private String destination;
	private int mem_param;
	private double result;
	private boolean result_ready, writeback_done;
	int stage;
	
	int clock_issued;
	
	public Station(String rs_name){
		busy = false;
		this.rs_name = rs_name;
        instruction = null;
        Vj = Vk = mem_param = 0;
        Qj = Qk = null;
        result_ready = false;
        writeback_done = false;
        stage = 0;
    }
	
	/*
	 * Verify if the operands are available
	 */
	public boolean rs_ready(){
        return (busy == true && Qj == null && Qk == null && 
                result_ready == false);
    }
	
	/*
	 * Reset the reservation station so that other
	 * instructions can use it
	 */
	public void reset(){
        busy = false;
        instruction = null;
        Vj = Vk = mem_param = 0;
        Qj = Qk = null;
        result_ready = false;
        writeback_done = false;
    }

	public String getRs_name() {
		return rs_name;
	}

	public void setRs_name(String rs_name) {
		this.rs_name = rs_name;
	}

	public String getInstruction() {
		return instruction;
	}

	public void setInstruction(String instruction) {
		this.instruction = instruction;
	}

	public double getVj() {
		return Vj;
	}

	public void setVj(double vj) {
		Vj = vj;
	}

	public double getVk() {
		return Vk;
	}

	public void setVk(double vk) {
		Vk = vk;
	}

	public String getQj() {
		return Qj;
	}

	public void setQj(String qj) {
		Qj = qj;
	}

	public String getQk() {
		return Qk;
	}

	public void setQk(String qk) {
		Qk = qk;
	}

	public int getMem_param() {
		return mem_param;
	}

	public void setMem_param(int mem_param) {
		this.mem_param = mem_param;
	}

	public double getResult() {
		return result;
	}

	public void setResult(Double result) {
		this.result = result;
	}

	public boolean isResult_ready() {
		return result_ready;
	}

	public void setResult_ready(boolean ready){
		this.result_ready = ready;
	}

	public boolean isBusy() {
		return busy;
	}

	public void setBusy(boolean busy) {
		this.busy = busy;
	}

	public void setDestination(String rob) {
		this.destination = rob;
	}
	
	public String getDestination() {
		return this.destination;
	}
	
	public int getStage() {
		return stage;
	}

	public void setStage(int stage) {
		this.stage = stage;
	}

	public void dumpContents(){
		System.out.print(Util.pad(rs_name, 8, " "));
		System.out.print(Util.pad(Boolean.toString(busy), 8, " "));
		System.out.print(Util.pad(instruction, 8, " "));
		System.out.print(" ");
		System.out.print(Util.pad(Double.toString(Vj), 16, " "));
		System.out.print(Util.pad(Double.toString(Vk), 16, " "));
		System.out.print(Util.pad(Qj, 8, " "));
		System.out.print(Util.pad(Qk, 8, " "));
		System.out.print(" ");
		System.out.print(Util.pad(Integer.toString(mem_param), 16, " "));
		System.out.print(" ");
		System.out.print(Util.pad(destination, 8, " "));
		System.out.print(Util.pad(Double.toString(result), 16, " "));
		System.out.print(Util.pad(Boolean.toString(result_ready), 8, " "));
		System.out.println(Util.pad(Boolean.toString(writeback_done), 8, " "));
		
	}
	
	public static void dumpHeaders(){        
        System.out.print(Util.pad("Name", 8, " "));
        System.out.print(Util.pad("Busy", 8, " "));
        System.out.print(Util.pad("Instr", 8, " "));
        System.out.print(" ");
        System.out.print(Util.pad("Vj", 16, " "));
        System.out.print(Util.pad("Vk", 16, " "));
        System.out.print(Util.pad("Qj", 8, " "));
        System.out.print(Util.pad("Qk", 8, " "));
        System.out.print(" ");
        System.out.print(Util.pad("Immediate", 16, " "));
        System.out.print(" ");
        System.out.print(Util.pad("Dest", 8, " "));
        System.out.print(Util.pad("Result", 16, " "));
        System.out.print(Util.pad("ResRead", 8, " "));
        System.out.println(Util.pad("WB_done", 8, " "));
    }
}
