import java.util.ArrayList;

public class Letter {
	public char c;
	public ArrayList<Character> possible = new ArrayList<>();
	
	public Letter(char c) {
		
		this.c = c;
		for(int i = 'A'; i <= 'Z'; i++) possible.add((char) i);
		
	}
}
