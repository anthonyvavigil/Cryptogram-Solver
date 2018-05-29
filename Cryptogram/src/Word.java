import java.util.ArrayList;

public class Word implements Comparable<Word>{
	
	public String text;
	public ArrayList<String> possible = new ArrayList<>();
	
	public Word(String text) {
		this.text = text;
	}

	@Override
	public int compareTo(Word w) {
		return (this.possible.size() - w.possible.size());
	}
	
}
