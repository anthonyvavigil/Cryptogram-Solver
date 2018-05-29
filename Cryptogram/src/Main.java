import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {

	public static ArrayList<Letter> combos = new ArrayList<>();
	public static ArrayList<ArrayList<String>> wordList = new ArrayList<>();



	public static ArrayList<Word> encrypted = new ArrayList<>();

	public static void main(String[] args) {

		setup("Large");
		grabWordsFromFile(new File("Input/Test1.txt"));
		deleteDuplicates();
		populatePossible();
		Collections.sort(encrypted);
		printWords(new File("Output/Before.txt"));
		int count = 0;
		boolean changed = true;
		while(changed) {
			changed = false;
			changed = checkReverseCombos() || changed;
			changed = sudokuCheck() || changed;
			changed = singleCharDeduct() || changed;
			printWords(new File("Output/After" + count + ".txt"));
			count++;
		}
		
	}

	public static void setup(String folderName) {

		for(int i = 'A'; i <= 'Z'; i++) {
			combos.add(new Letter((char) i)); 
		}

		//

		for(int i = 0; i < 15; i++) {

			wordList.add(new ArrayList<>());

			try {

				Scanner in = new Scanner(new File(folderName + "/Length" + (i + 1) + ".txt"));

				while(in.hasNextLine()) {

					wordList.get(i).add(in.nextLine().trim().toUpperCase());

				}

			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}

		}

	}
	
	public static void deleteDuplicates() {
		
		for(int i = encrypted.size() - 1; i >= 0; i--) {
			
			for(int j = i + 1; j < encrypted.size(); j++) {
				
				if(encrypted.get(i).text.equals(encrypted.get(j).text)) {
					encrypted.remove(i);
					break;
				}
				
			}
			
		}
		
	}
	
	public static void grabWordsFromFile(File f) {

		encrypted = new ArrayList<>();

		try {

			Scanner in = new Scanner(f);

			while(in.hasNext()) {
				encrypted.add(new Word(in.next().trim().toUpperCase()));
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

	}

	public static void populatePossible() {

		for(Word w : encrypted) {

			w.possible.addAll(wordList.get(w.text.length() - 1));

		}

	}

	public static boolean singleCharDeduct() {

		int counter = encrypted.size();
		int index = 0;
		boolean changed = false;

		while(counter >= 0) { // loop through encrypted words

			Word word = encrypted.get(index);

			for(int i = 0; i < word.text.length(); i++) { // loop through letters

				ArrayList<Character> unfoundChars = new ArrayList<>();
				for(int c = 'A'; c <= 'Z'; c++) unfoundChars.add((char) c);

				for(int j = word.possible.size() - 1; j >= 0 ; j--) { // loop through possible words
					
					// if there is not a combo between the encrypted letter and the proposed one
					if(!combos.get(word.text.charAt(i) - 'A').possible.contains(word.possible.get(j).charAt(i))) { // What a mess
						word.possible.remove(j);
						counter = encrypted.size();
						changed = true;
					} else {
						unfoundChars.remove((Character) word.possible.get(j).charAt(i));
					}

				}
				
				for(char c : unfoundChars) {
					if(removeCombo(word.text.charAt(i), c)) {
						counter = encrypted.size();
						changed = true;
					}
				}
				
			}

			counter--;
			index = (index + 1) % encrypted.size();

		}
		
		return changed;
		
	}
	
	public static boolean removeCombo(char encrypted, char destination) {
		
		if(combos.get(encrypted - 'A').possible.remove((Character) destination)) {
			
			System.out.println("combo removed: " + encrypted + " -> " + destination);
			
			if(combos.get(encrypted - 'A').possible.size() == 0) {
				
				System.err.println("Error: impossibility on character " + encrypted);
				
			} else if(combos.get(encrypted - 'A').possible.size() == 0) {
				
				for(int i = 0; i < combos.size(); i++) {
					if(i != encrypted - 'A') {
						
						removeCombo((char) i, destination);
						
					}
				}
				
			}
			
			return true;
			
		}
		
		return false;
		
	}
	
	public static boolean possible(String encrypted, String word) {

		for(int i = 0; i < word.length(); i++) {

			for(int j = 0; j < i; j++) {

				if((word.charAt(j) == word.charAt(i)) != (encrypted.charAt(j) == encrypted.charAt(i))) return false;

			}

		}

		return true;

	}

	public static void printWords(File f) {

		try {

			PrintWriter p = new PrintWriter(f);

			//// print header

			// Print top line

			p.print("+");

			for(Word w : encrypted) {
				for(char c : w.text.toCharArray()) {
					p.print("-");
				}
				p.print("--+");
			}

			// Print encrypted words

			p.print("+\n|");

			for(Word w : encrypted) {
				p.print(" " + w.text + " |");
			}

			p.print("\n+");

			// Print dividing line

			int maxLength = 0;

			for(Word w : encrypted) {
				for(char c : w.text.toCharArray()) {
					p.print("-");
				}
				p.print("--+");

				maxLength = Math.max(maxLength, w.possible.size());

			}

			//// Print bottom

			for(int i = 0; i < maxLength; i++) {
				
				p.print("\n|");
				
				for(Word w : encrypted) {

					if(w.possible.size() > i) {
						p.print(" " + w.possible.get(i) + " |");
					} else {
						for(char c : w.text.toCharArray()) {
							p.print(" ");
						}
						p.print("  |");
					}

				}
				
			}
			
			p.print("\n+");
			
			for(Word w : encrypted) {
				for(char c : w.text.toCharArray()) {
					p.print("-");
				}
				p.print("--+");

			}

			p.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

	}

	public static ArrayList<Letter> getReverseCombos() {
		
		ArrayList<Letter> reverseCombos = new ArrayList<>();
		
		for(int i = 'A'; i <= 'Z'; i++) {
			reverseCombos.add(new Letter((char) i)); 
		}
		
		for(Letter l : combos) {
			
			for(char c : l.possible) {
				
				reverseCombos.get(c - 'A').possible.add(l.c);
				
			}
			
		}
		
		return reverseCombos;
		
	}

	public static boolean checkReverseCombos() {
		
		ArrayList<Letter> reverse = getReverseCombos();
		
		boolean changed = false;
		
		for(Letter l : reverse) {
			
			if(l.possible.size() == 0) {
				System.err.println("Error: impossibility on reverse character " + l.c);
			} else if(l.possible.size() == 1) {
				
				for(int i = 'A'; i <= 'Z'; i++) {
					
					if(i != l.c) {
						
						changed = removeCombo(l.possible.get(0), (char) i) || changed;
						
					}
					
				}
				
			}
			
		}
		
		return changed;
		
	}
	
	// Wow, the variable names here are atrocious
	// Beware those who traverse these lands (and sorry)
	public static boolean sudokuCheck() {
		
		boolean[][] possibilities = getPossibilitySquare();
		boolean changed = false;
		
		for(int i = 0; i < 26; i++) {
			
			// Tally number of true in the line - we need to know how many of the same line we have
			
			int rowTrue = -1;
			int colTrue = -1;
			
			for(int j = 0; j < 26; j++) {
				if(possibilities[i][j]) rowTrue++;
				if(possibilities[j][i]) colTrue++;
			}
			
			// a list of indices where the row is less than or equal to 
			
			boolean[] rowLess = new boolean[26];
			boolean[] colLess = new boolean[26];
			
			rowLess[i] = true;
			colLess[i] = true;
			
			// loop through the other lines
			
			for(int j = 0; j < 26; j++) {
				if(j != i) {
					
					// loop through both lines to see if other has trues that this doesn't
					
					boolean rowLessThan = true;
					boolean colLessThan = true;
					
					for(int k = 0; k < 26; k++) {
						
						if(!possibilities[i][k] && possibilities[j][k]) {
							rowLessThan = false;
						}
						if(!possibilities[k][i] && possibilities[k][j]) {
							colLessThan = false;
						}
						
					}
					
					if(rowLessThan) {
						rowLess[j] = true;
						rowTrue--;
					}
					if(colLessThan) {
						colLess[j] = true;
						colTrue--;
					}
					
				}
			}
			
			for(int j = 0; j < 26; j++) {
				
				if(rowTrue <= 0 && !rowLess[j]) {
					changed = removeCombo((char) (i + 'A'), (char) (j + 'A')) || changed; 
				}
				
				if(colTrue <= 0 && !colLess[j]) {
					changed = removeCombo((char) (j + 'A'), (char) (i + 'A')) || changed; 
				}
				
			}
			
		}
		
		return changed;
		
	}
	
	public static boolean[][] getPossibilitySquare() {
		
		boolean[][] possibilities = new boolean[26][26];
		
		for(int i = 0; i < 26; i++) {
			
			for(char c : combos.get(i).possible) {
				
				possibilities[i][c - 'A'] = true;
				
			}
			
		}
		
		return possibilities;
		
	}
	
}