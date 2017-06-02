package simulator;
public class RegStatus{
	static RegStatus instance = null;
	
	static RegStatus getInstance(){
		if (instance==null)
			instance = new RegStatus();
		return instance;
	}
	static String[] Rstatus;
	static String[] Fstatus;
	private RegStatus(){
		Rstatus = new String[32];
		Fstatus = new String[32];
	}
	public static void insert(String ROBtag, String reg){
		char type = reg.charAt(0);
		reg = reg.substring(1,reg.length());
		if (type=='F'){
			Fstatus[Integer.parseInt(reg)]=ROBtag;
		}else{
			Rstatus[Integer.parseInt(reg)]=ROBtag;
		}
	}
	public static String get(String reg){
		String ROBtag;
		char type = reg.charAt(0);
		reg = reg.substring(1,reg.length());
		if (type=='F'){
			ROBtag = Fstatus[Integer.parseInt(reg)];
		}else{
			ROBtag = Rstatus[Integer.parseInt(reg)];
		}
		
		return ROBtag;
	}
	
	public static void unassign(String rd, String ROBtag){
		char type = rd.charAt(0);
		rd = rd.substring(1,rd.length());
		if (type=='F'){
			if(Fstatus[Integer.parseInt(rd)].equals(ROBtag))
				Fstatus[Integer.parseInt(rd)]=null;
		}else{
			if(Rstatus[Integer.parseInt(rd)].equals(ROBtag))
				Rstatus[Integer.parseInt(rd)]=null;
		}
	}
	
	public static void flush(){
		for(int i=0; i<Fstatus.length; i++){
			Fstatus[i] = null;
			Rstatus[i] = null;
		}
	}
	
	public static void print(){
		System.out.println("Int Reg Status: ");
		for (int i=0; i<Rstatus.length; i++)
			if (Rstatus[i]!=null)
				System.out.print("R"+i+"->"+Rstatus[i]+" ,      ");
		System.out.println(" ");
		System.out.println("FP Reg Status: ");
		for (int i=0; i<Fstatus.length; i++)
			if (Fstatus[i]!=null)
				System.out.print("F"+i+"->"+Fstatus[i]+" ,      ");
		System.out.println(" ");
	}
	
}