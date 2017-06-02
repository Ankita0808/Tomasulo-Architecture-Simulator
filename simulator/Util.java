package simulator;

public class Util {
	public static String pad(String field, int len, String padChar){
	int i;
	String padding = padChar;
	int count;
	if(field == null)
		count = len;
	else
		count = len - field.length();
	
	for (i = 0; i < count - 1; i++) 
		padding = padding + padChar;
	
	if (count != 0 && field != null) 
		field = padding + field;
	else
		field = padding;
	return field;
	}
}
