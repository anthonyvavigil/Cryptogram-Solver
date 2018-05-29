
public class LengthString implements Comparable<LengthString>{
	
	public String s;

	public LengthString(String s) {
		this.s = s;
	}
	
	public int compareTo(LengthString o) {
		return this.s.length() - o.s.length();
	}
	
	
	
}
