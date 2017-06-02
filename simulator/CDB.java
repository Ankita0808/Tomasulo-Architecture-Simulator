package simulator;

import java.util.ArrayList;
import java.util.HashMap;

public class CDB {
	private ArrayList<Status> cdb;
	private ArrayList<String> robTags;
	private HashMap<String,Integer> origin;
	static int cdb_size;
	
	static CDB instance = null;
	
	public static CDB getInstance(Integer cdb_width) {
		if (instance == null){
			cdb_size = cdb_width;
            instance = new CDB();
		}
        return instance;
		
	}
	
	static CDB getInstance(){
        if (instance == null)
            instance = new CDB();
        return instance;
    }
	
	private CDB(){
		 cdb = new ArrayList<Status>();
		 origin = new HashMap<String,Integer>();
		 robTags = new ArrayList<String>();
	}
	
	public void add(String rob, Status robStatus, int origin){
		cdb.add(robStatus);
		robTags.add(rob);
		this.origin.put(rob,origin);
	}
	
	public void clear(){
		cdb.clear();
		origin.clear();
		robTags.clear();
	}
	
	public int size(){
		return cdb.size();
	}

	public boolean hasROB_WB(String robTag) {
		for(String wb_tag : origin.keySet())
			if(wb_tag.equals(robTag) && origin.get(wb_tag) == 0)
				return true;
		return false;
	}
	
	public boolean hasROB_Commit(String robTag) {
		for(String wb_tag : origin.keySet())
			if(wb_tag.equals(robTag) && origin.get(wb_tag) == 1)
				return true;
		return false;
	}

	public double getValue(String robTag) {
		ROBStatus.getInstance();
		return ROBStatus.getStatus(robTag).value;
	}
	
	/*
	 * Returns an ArrayList containing the status information of the ROBs 
	 * coming from the Commit stage.
	 * Entries from the Write Back are ignored
	 * 
	 * @return ArrayList<Status> status object corresponding to the registers to 
	 * be updated
	 */
	public ArrayList<Status> getCommitValues() {
		ArrayList<Status> commitValues = new ArrayList<Status>();
		for(String robTag : robTags){
			if(origin.get(robTag) == 1) {
				ROBStatus.getInstance();
				commitValues.add(ROBStatus.getStatus(robTag));
			}
		}
		return commitValues;
	}

	public ArrayList<String> getRobTags() {
		return robTags;
	}

	public void setRobTags(ArrayList<String> robTags) {
		this.robTags = robTags;
	}

	public HashMap<String, Integer> getOrigin() {
		return origin;
	}

	public void setOrigin(HashMap<String, Integer> origin) {
		this.origin = origin;
	}

	
	
	
}