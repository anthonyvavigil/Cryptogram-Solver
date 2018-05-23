
public class Letter {
	String name;
	char charName;
	double freq;
	int appearances;
	String correspondsTo;
	char charCorrespondsTo;
	
	public Letter(String i1, char i2, int i3){
		name = i1;
		charName = i2;
		appearances = i3;
	}
	
	public void incrementAppearances() {
		appearances++;
	}
	public void setName(String i1) {
		name = i1;
	}
	public void setName(char i1) {
		charName = i1;
	}
	public void setFrequency(double i1) {
		freq = i1;
	}
	public void setAppearances(int i1) {
		appearances = i1;
	}
	public void setCorrespondent(String i1) {
		correspondsTo = i1;
	}
	public void setCorrespondent(char i1) {
		charCorrespondsTo = i1;
	}
 	public String getNameString() {
		return name;
	}
	public char getNameChar() {
		return charName;
	}
	public double getFreq() {
		return freq;
	}
	public int getAppearances() {
		return appearances; 
	}
	public String getCorrespondentString() {
		return correspondsTo;
	}
	public char getCorrespondentChar() {
		return charCorrespondsTo;
	}
}
