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
		int dictionaryTolerance = 1498; //gets this many words from the dictionary, which is ordered by frequency
		
		
		try {
		scn = new Scanner(System.in);
		allWords = new Scanner(new File("exclusiveDictionary.txt"));
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
				//sol.add(relativeFrequencySolve(nextLine.toLowerCase(), tolerance, letterFreq));
			}
			//sol = goodSolutions(sol, dictionary, tolerance);
			//	System.out.println("number of solutions: " + sol.size());
			for(int w = 0; w < sol.size(); w++) {
				System.out.println(sol.get(w));
			}
			
			sol = methodicSolve(nextLine.toLowerCase(), dictionary, tolerance);
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
	
	public static String depunctuate(String input) {
		String tempStr = "";
		for(int j = 0; j < input.length(); j++){
			if(((int) input.charAt(j)) > 96 && ((int) input.charAt(j)) < 123 || (int) input.charAt(j) == 32){
				tempStr = tempStr + input.substring(j, j+1);
			}
		}
		input = tempStr;
		return input;
	}
	
	public static ArrayList<String> methodicSolve(String input, ArrayList<String> dic, int tol) {
		//loops through string and removes punctuation
		input = depunctuate(input);
		
		ArrayList<RichLetter> allLetters = new ArrayList();
		ArrayList<String> possibleWords = new ArrayList();
		int lengthWithoutSpaces = 0;
		String[] inputSplit = input.split(" ");
		ArrayList<String> inputArr = new ArrayList();
		for(int i = 0; i < inputSplit.length; i++) {
			inputArr.add(inputSplit[i]);
			lengthWithoutSpaces += inputSplit[i].length();
		}
		
		Collections.sort(inputArr, new Comparator<String>() { //sorts sentence by word length
            @Override
            public int compare(String o1, String o2) {             
                if (o1.length()!=o2.length()) {
                    return o1.length()-o2.length(); 
                }
                return o1.compareTo(o2);
            }
        });
		
		//initialize letter possibilities - one letter for each of the alphabet, one letter correspondence for each letter of the alphabet
		String abcs = "a b c d e f g h i j k l m n o p q r s t u v w x y z";
		String[] abcSplit = abcs.split(" ");
		
		for(int i = 0; i < abcSplit.length; i++) {
			RichLetter temp = new RichLetter(abcSplit[i]);
				for(int j  = 0; j < abcSplit.length; j++) {
					temp.addPossibleCorrespondence(abcSplit[j]);
				}
			allLetters.add(temp);
		}
		
		
		boolean methodFinished = false;
		int count = 0;
		while(!methodFinished) { //loops through words in list
			
			String curWord = inputArr.get(count);
			
			if(curWord.length() == 1) {
				ArrayList<String> a = new ArrayList(); a.add("a"); a.add("i");
				allLetters.get(((int) curWord.charAt(0)-97)).setPossibilities(a);
			}
			/*
			 * 097 corresponds to a, 122 to z
			 */
			
			//loops through thatLength words
			int w = 0;
			

			int n = 0;
			while(dic.get(w).length() <= inputArr.get(count).length() && n < dic.size()) { //loops until it hits words longer than those from the input array				
				if(dic.get(w).length() == inputArr.get(count).length()) {
					String tempDic = dic.get(w);
					String tempInp = inputArr.get(count);
					
					//handles letters in pairs to speed up processing
					for(int j = 0; j < tempDic.length(); j++) { //loops through word from dictionary
						if(tempDic.length()-j >= 2) { // if there are two more letters to look at
							
							char char1 = tempInp.charAt(j); //gets pair of characters to be analyzed - (characters so they can easily be switched to index)
							char char2 = tempInp.charAt(j+1);
							
							RichLetter l1 = allLetters.get(((int) char1)-97); //converts char to numbers
							RichLetter l2 = allLetters.get(((int) char2)-97);
							
							for(int t = 0; t < l1.getCorrespondences().size(); t++) {
								for(int y = 0; y < l2.getCorrespondences().size(); y++) {
									//check if the word has any possibilities
									
									
									String tempA = l1.getCorrespondences().get(t) + l2.getCorrespondences().get(y);
									String tempB = tempDic.substring(j, j+2);
									
									
									//if those two letter pairs match the letters from the word
									if(tempA.equals(tempB)) {
										//System.out.println(tempA + ":" + tempB);
										possibleWords.add(tempDic);
										
										allLetters.get(((int) char1)-97).addTempPossibility(tempDic.substring(j, j+1));
										allLetters.get(((int) char2)-97).addTempPossibility(tempDic.substring(j+1, j+2));
									} else {
										
									}
								}
							}
						}						
					}									
				}	
				if(w < dic.size()-1) { 
					w++;
				} else {
					w = 0;
				}
				n++;
			}
			
			if(count%20 == 0) {
				System.out.println("at word " + count + " from the input, total words are " + (inputArr.size()-1)); 
			}
			if(count < inputArr.size()-1) { //keeps looping through the input by word
				count++;
			} else {
				
				dic = crossCheck(dic, possibleWords);
				
				for(int i = 0; i < allLetters.size(); i++) {
					ArrayList<String> tempCor = allLetters.get(i).getCorrespondences();
					ArrayList<String> tempPos = allLetters.get(i).getTempPossibilities();
					
					tempCor = crossCheck(tempCor, tempPos);
				//	System.out.println(tempCor);
				//	System.out.println(tempPos);
					System.out.println(allLetters.get(i).getName() + ":corresp size:" + tempCor.size());
					allLetters.get(i).setPossibilities(tempCor);
					//System.out.println("end of line");
				}
				
				count = 0;
				
			}
			//System.out.println("dic: " + dic.size());
			//System.out.println("inpArr: " + inputArr.size() + ":" + count); 
			
		}
		return dic;
	}
	
	public static ArrayList<String> removeDuplicatesInOrderedList(ArrayList<String> input) {
		for(int i = 0; i < input.size(); i++) {
			if(i < input.size()-1) {
				if(input.get(i).equals(input.get(i+1))) {
					input.remove(i+1);
					i--;
				}
			}
		}
		
		
		return input;
	}
	
	public static ArrayList<String> crossCheck(ArrayList<String> base, ArrayList<String> toStay) {
		ArrayList<String> temp = new ArrayList();
		for(int i = 0; i < base.size(); i++) {
			for(int j = 0; j < toStay.size(); j++) {
				if(base.get(i).equals(toStay.get(j))) { //if it's in both arrayLists
					temp.add(base.get(i));
				}
			}	
		}
		temp = removeDuplicatesInOrderedList(temp);
		return temp;
		
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