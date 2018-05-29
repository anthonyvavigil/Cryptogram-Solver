import java.util.ArrayList;

public class RichLetter {
	ArrayList<String> possibleCorrespondences = new ArrayList();
	ArrayList<String> tempPossibilities = new ArrayList();
	ArrayList<String> tempImpossibilities = new ArrayList();
	String name;
	char charName;
	boolean certain = false;
	public boolean isAorI = false;
	

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
	public void addTempPossibility(String a) {
		tempPossibilities.add(a);
	}
	public void setImpossibilities(ArrayList<String> impossibilities) {
		this.tempImpossibilities = impossibilities;
	}
	public void setPossibilities(ArrayList<String> possibilities) {
		this.possibleCorrespondences = possibilities;
	}
	public void setTempPossibilities(ArrayList<String> tempPossibilities) {
		this.tempPossibilities = tempPossibilities;
	}
	public void setAorI() {
		isAorI = true;
	}
	
	
	public ArrayList<String> getImpossibilities() {
		return tempImpossibilities;
	}
	public void removeImpossibility(String a) {
		if(tempImpossibilities.indexOf(a) > -1) {
			tempImpossibilities.remove(tempImpossibilities.indexOf(a));
		}
	}
	public ArrayList<String> getDependencies() {
		return tempPossibilities;
	}
	public String getName() {
		return name;
	}
	public ArrayList<String> getCorrespondences() {
		return possibleCorrespondences;
	}
	public ArrayList<String> getTempPossibilities() {
		return tempPossibilities;
	}
	public boolean isCertain() {
		return certain;
	}
	public boolean isAorI() {
		return isAorI;
	}
}
