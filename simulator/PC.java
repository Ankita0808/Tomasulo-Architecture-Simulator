package simulator;

public class PC {
	static int value;  //time in cycles
    static PC instance = null;

    //class is a singleton so constructor is private
    private PC(){
        value = 0;
    }

    //returns singleton instance
    static PC getInstance(){
        if (instance == null)
            instance = new PC();
        return instance;
    }

    //returns current PC in cycles
    static int get(){
        return value;
    }

    //set the PC
    static void set(int newValue){
        value = newValue;
    }
}
