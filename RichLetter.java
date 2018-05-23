import java.util.ArrayList;

public class RichLetter {
	ArrayList<String> possibleCorrespondences = new ArrayList();
	ArrayList<RichLetter> dependencies = new ArrayList();
	String name;
	char charName;
	boolean certain = false;
	

	public RichLetter(String name) {
		this.name = name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	public void addPossibleCorrespondence(String a) {
		possibleCorrespondences.add(a);
	}
	public void setCertain() {
		certain = true;
	}
	public void addDependency(RichLetter a) {
		dependencies.add(a);
	}
	
	public ArrayList<RichLetter> getDependencies() {
		return dependencies;
	}
	public String getName() {
		return name;
	}
	public ArrayList<String> getPossibleCorrespondences() {
		return possibleCorrespondences;
	}
	public boolean isCertain() {
		return certain;
	}
}
