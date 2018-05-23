import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Scanner;

public class CrytographSolver {
	
	public static boolean printedFreq = false;
	public static void main(String[] args) { 
		int tolerance = 0;
		Scanner allWords = null;
		Scanner scn = null;
		boolean quitted = false;
		ArrayList<String> toUser = new ArrayList();
		String letterFreq = "";
		int dictionaryTolerance = 4000; //gets this many words from the dictionary, which is ordered by frequency
		
		
		try {
		scn = new Scanner(System.in);
		allWords = new Scanner(new File("dictionary.txt"));
		} catch (Exception e){
			System.out.println("ERR: Could not establish scanner");
			System.out.println(e.getMessage());
		}
		
		ArrayList<String> dictionary = new ArrayList();
		
		
		int count = 0;
		
		while(allWords.hasNextLine() && count < dictionaryTolerance){
			String nL = allWords.nextLine().toLowerCase();
			dictionary.add(nL);
		}
		
		
		while(quitted == false) {
		System.out.println("enter unsolved cryptograph, or type Q to quit");
		String nextLine = scn.nextLine();
		if(nextLine.toLowerCase().equals("q")){ quitted = true; System.out.println("program ended");} 
		else {
		System.out.println("enter tolerance level");
		String tol = scn.nextLine();
		if(tol.toUpperCase().equals("DEF")) {
			tolerance = nextLine.length()/10;
			System.out.println(tolerance);
		} else { 
			tolerance = Integer.valueOf(tol);
			System.out.println("processing");
		}
		
			//prints all the possible solutions
			toUser = caesarSolve(nextLine.toLowerCase(), dictionary, tolerance);
			boolean doRelFreq = true;
			
			System.out.println("------------------CAESAR CIPHER----------------");
			for(int i = 0; i < toUser.size(); i++) {
					System.out.println(toUser.get(i));
					
				}
			if(toUser.size() > 0) { 
				System.out.println("******\nfound a caesar solution, do you still want to do the relative frequency solve (y for yes, n for no)\n*****");
				String a = scn.nextLine();
				if(!(a.toLowerCase().equals("y"))) doRelFreq = false; 
			}
			if(doRelFreq) {
			System.out.println("------------------RELATIVE FREQUENCY CIPHER----");
			
			File relFreq = new File("relativeFrequencyList.txt");
			Scanner scn2 = null;
			try {
				scn2 = new Scanner(relFreq);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			

			ArrayList<String> sol = new ArrayList();
			int cou = 0;
			while(scn2.hasNextLine()) {
				letterFreq = scn2.nextLine();
				if(cou%500 == 0) {System.out.println("running, checked " + cou + " responses"); }
				cou++;
				sol.add(relativeFrequencySolve(nextLine.toLowerCase(), tolerance, letterFreq));
			}
			sol = goodSolutions(sol, dictionary, tolerance);
				System.out.println("number of solutions: " + sol.size());
			for(int w = 0; w < sol.size(); w++) {
				System.out.println(sol.get(w));
			}
			
			
			System.out.println("-------------------------------------------------------------------------------------------------------------------");
				}	
			}
		}
	}
	
	public static String relativeFrequencySolve(String input, int tol, String letterFrequencies) {
		
		ArrayList<Letter> freqList = new ArrayList();
		int tot = input.length();
		
		//loops through every character in the cipher, if it already has a letter assigned to it, add one to appearances, if not, create a letter
		for(int i = 0; i < tot; i++) {
			boolean isInFreqList = false;
			
		if((int) input.charAt(i) > 96 && (int) input.charAt(i) < 123) {	
			if(freqList.size() < 26) { //if there are 26 characters the alphabet has finished being added
				for(int w = 0; w < freqList.size(); w++) { //checks if it already has a letter assigned to it
					if(freqList.get(w).getNameChar() == input.charAt(i)){
							freqList.get(w).incrementAppearances();
							isInFreqList = true;
						}
					}
				 if(!isInFreqList) {
					freqList.add(new Letter(String.valueOf(input.charAt(i)), input.charAt(i), 1));
				}
			}
		}
	}
		
		Collections.sort(freqList, new Comparator<Letter>() {
			@Override
			public int compare(Letter i1, Letter i2) {
				// TODO Auto-generated method stub
				return i1.appearances - i2.appearances;
			}

	    });
		
		//letterFrequencies = "zqxjkvbpygfwmucldrhsnioate";
		
		letterFrequencies = letterFrequencies.substring((26-freqList.size()));
		
		//calculates relative frequencies for each letter
				for(int j = 0; j < freqList.size(); j++) {
					freqList.get(j).setFrequency(((double) freqList.get(j).getAppearances())/((double) tot));
				}
				
	
		//corresponds each letter to its most likely correspondent
		for(int h = 0; h < freqList.size(); h++) {
			freqList.get(h).setCorrespondent(String.valueOf(letterFrequencies.charAt(h))); freqList.get(h).setCorrespondent(letterFrequencies.charAt(h));
				if(printedFreq==false) {
					System.out.println(freqList.get(h).getNameString() + "||" + freqList.get(h).getFreq() + "||" + freqList.get(h).getCorrespondentString());
				} 
		}
		printedFreq = true;
		
		
		//changes each letter in the input to correspond with its most likely correspondent
		String decrypted = "";
		
		for(int e = 0; e < input.length(); e++) {
			String currentLet = String.valueOf(input.charAt(e));
			
			if(currentLet.equals(" ")){ decrypted = decrypted + " "; } else {
				
			//finds that character in the list
			int index = -1;
			for(int f = 0; f < freqList.size(); f++) {
				if(freqList.get(f).name.equals(currentLet)){
					decrypted = decrypted + freqList.get(f).getCorrespondentString();
					index = 1;
				}
			}
				if(index == -1) {
					decrypted = decrypted + "?";
				} 
			}
		}
		return decrypted;
		
	}
	
	public static ArrayList<String> methodicSolve(String input, ArrayList<String> dic, int tol) {
		String[] inputSplit = input.split(" ");
		ArrayList<String> inputArr = new ArrayList();
		for(int i = 0; i < inputSplit.length; i++) {
			inputArr.add(inputSplit[i]);
		}
		Collections.sort(inputArr, new Comparator<String>() { //sorts list by word length
            @Override
            public int compare(String o1, String o2) {             
                if (o1.length()!=o2.length()) {
                    return o1.length()-o2.length(); 
                }
                return o1.compareTo(o2);
            }
        });
		
		boolean methodFinished = false;
		while(!methodFinished) {
			String temp = input;
			String[] tempArr = input.split(" ");
			ArrayList<RichLetter> tempRich = new ArrayList();
			ArrayList<String> storedRich = new ArrayList();
			for(int i = 0; i < tempArr.length; i++) {
				//get next word in array
				String cur = tempArr[i];
				if(cur.length() == 1 && ! (storedRich.indexOf(cur) > -1)) { //if length one and does not already exist
					RichLetter one = new RichLetter(cur);
					one.addPossibleCorrespondence("a"); one.addPossibleCorrespondence("i");
					tempRich.add(one);
					storedRich.add(one.getName());
				} else if (! (cur.length() == 1)) { //if length is not one (existence checked later)
				
				//get all next word length words from dictionary
				ArrayList<String> thatLength = new ArrayList();
					for(int j = 0; j < dic.size(); j++) {
						if(dic.get(j).length() == thatLength.size()) {
							thatLength.add(dic.get(j)); //adds if it's the same length
						} else if (dic.get(j).length() > thatLength.size()) {
							j = dic.size()+1; //ends loop if the word is longer
						} else { } //do nothing if it's less and let the list increment
					}
			
				//increment all thatLength words and add to the rich letter object for that possibility
				for(int w = 0; w < thatLength.size(); w++) {
					int ind = -1;
					//check if the letter at that index in input already has a richLetter
					String curStr = input.substring(w, w+1); //length 1 string that holds the current letter
					RichLetter a;
						if(storedRich.indexOf(curStr) > -1) { //already exists in list
							ind = storedRich.indexOf(curStr);
							a = tempRich.get(ind);
						} else { //doesn't exist in list
							a = new RichLetter(curStr);
						}
					
					}
				}					
			}
		}
		
		return dic;
		
	}
	
	public static ArrayList<String> caesarSolve(String input, ArrayList<String> dic, int tol) {
		ArrayList<String> possib = new ArrayList();
		
		input = input.toLowerCase();
		
		//loops through string and removes punctuation
		String temp = "";
		for(int j = 0; j < input.length(); j++){
			if(((int) input.charAt(j)) > 96 && ((int) input.charAt(j)) < 123 || (int) input.charAt(j) == 32){
				temp = temp + input.substring(j, j+1);
			}
		}
		input = temp;
		
		//loops through the alphabet, checking for solutions with full words. 
		for(int i = 0; i < 26; i++){
			String curCipher = "";
			//loops through each character in the word, adding specified amount to char value
			for(int w = 0; w < input.length(); w++){
				int intValueOfChar = (int) input.charAt(w);
				
				//keeps spaces
				if(intValueOfChar == 32) {}
				
				//loops if value is past Z
				else if(intValueOfChar+i > 122){
					intValueOfChar = 96 + ((intValueOfChar+i)-122);
				} 
				
				//adds one otherwise
				else {
					intValueOfChar+=i;
				}
				
				curCipher = curCipher + ((char) intValueOfChar);
				
				
			}
			possib.add(curCipher);
		}
		return goodSolutions(possib, dic, tol);
	}
	
	public static boolean isInDictionary(String input, ArrayList<String> dic) {
			//binary searches for that word in the ArrayList of all known words
			if(dic.indexOf(input) > -1){
				return true;
			} else {
				return false;
			}
	}
	
	public static ArrayList<String> goodSolutions(ArrayList<String> possib, ArrayList<String> dic, int tol) {
		//loops through every possibility 
		ArrayList<String> goodSol = new ArrayList();
		for(int i = 0; i < possib.size(); i++) {
			if(i%100 == 0) {
				System.out.println("checking for good solutions, checked " + i + " possibilities, found " + goodSol.size() + " solutions");
			}
			int totalCorrectWords = 0;
			String[] curPossib = possib.get(i).split(" ");
			
			//loops through each word in the possibility
			for(int w = 0; w < curPossib.length; w++){
				
				//binary searches for that word in the ArrayList of all known words
				if(dic.indexOf(curPossib[w]) > -1){
					totalCorrectWords++;
				}
			}
		if(totalCorrectWords > tol) {
				goodSol.add(possib.get(i) + " ||  total correct words: " + totalCorrectWords);
			}	
			
		}
		return goodSol;
		}
	

}