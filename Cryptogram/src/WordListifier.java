import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WordListifier {
	
	public static void main(String[] args) {
		
		
		
		ArrayList<LengthString> words = new ArrayList<>();
		
		try {
			
			Scanner in = new Scanner(new File("Common/wordlist.txt"));
			
			while(in.hasNextLine()) {
				
				words.add(new LengthString(in.nextLine().trim().toUpperCase()));
				
			}
			
			Collections.sort(words);
			
			int length = 1;
			PrintWriter out = new PrintWriter(new File("Common/Length1.txt"));
			Pattern p = Pattern.compile("[A-Z]*");
			
			for(LengthString l : words) {
				
				if(l.s.length() > length) {
					length = l.s.length();
					out.close();
					out = new PrintWriter(new File("Common/Length" + length + ".txt"));
				}
				
				if(p.matcher(l.s).matches()) out.println(l.s);
				
			}
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		//*/
		
	}
	
}
