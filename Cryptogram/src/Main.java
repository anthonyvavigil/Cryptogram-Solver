import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {

	public static ArrayList<Letter> combos = new ArrayList<>();
	public static ArrayList<ArrayList<String>> wordList = new ArrayList<>();
	public static ArrayList<Word> encrypted = new ArrayList<>();
	
	public static ArrayList<String> caesarDictionary = new ArrayList<>();
	
	public static void main(String[] args) {
		
		
		
		
		// Caesar stuff
		
		setupCaesar(new File("ExclusiveDictionary.txt"));
		int totalCaesar = setupCaesarQuotes(new File("Input/AllCaesarQuotes.txt"));
		
		int counter = 0;
		
		while(counter < totalCaesar) {
			
			try {
				
				Scanner in = new Scanner(new File("Input/CaesarQuote" + counter + ".txt"));
				String s = in.nextLine();
				
				ArrayList<String> solutions = caesarSolve(s, caesarDictionary, s.split("[ ]").length / 10);
				
				if(solutions.isEmpty()) {
					
					System.err.println("Caesar cipher failed on quote " + counter + " (no solutions found). Copy the quote to non-caesar cipher to solve using that.");
					
				} else {
					
					PrintWriter p = new PrintWriter(new File("Output/SolvedCaesar" + counter + ".txt"));
					
					for(String x : solutions) {
						p.println(x);
					}
					
					p.close();
					
				}
				
			} catch(FileNotFoundException e) {
				e.printStackTrace();
			}
			
			counter++;
			
		}
		
		// Do non-caesar stuff
		
		setupWordList("Large");
		int totalQuotes = setupQuotes(new File("Input/AllNonCaesarQuotes.txt"));

		counter = 0;

		while(counter < totalQuotes){

			combos = new ArrayList<>();
			encrypted = new ArrayList<>();

			for(int i = 'A'; i <= 'Z'; i++) {
				combos.add(new Letter((char) i)); 
			}

			grabWordsFromFile(new File("Input/NonCaesarQuote" + counter + ".txt"));
			purifyWords();
			deleteDuplicates();

			populatePossible();
			Collections.sort(encrypted);

//			printWords(new File("Output/NonCaesarBefore.txt"));

			int count = 0;
			boolean changed = true;

			while(changed) {
				changed = false;

//				changed = checkReverseCombos() || changed;
//				changed = sudokuCheck() || changed;

				changed = singleCharDeduct() || changed;

//				printWords(new File("Output/NonCaesarAfter" + count + ".txt"));
//				count++;

			}
			
//			printWords(new File("Output/NonCaesarPossible" + count + ".txt"));
//			printCombos(new File("Output/NonCaesarCombos" + counter + ".txt"));

			printPossibleQuotes(new File("Input/NonCaesarQuote" + counter + ".txt"), new File("Output/SolvedNonCaesar" + counter + ".txt"));

			counter++;

		}

	}

	public static int setupQuotes(File f) {

		try {

			Scanner in = new Scanner(f);

			int counter = 0;

			while(in.hasNextLine()) {

				PrintWriter p = new PrintWriter(new File("Input/NonCaesarQuote" + counter + ".txt"));
				p.print(in.nextLine());
				p.close();

				counter++;

			}

			in.close();
			
			return counter;
			
		} catch(FileNotFoundException e) {
			e.printStackTrace();
			return 0;
		}

	}

	public static void printPossibleQuotes(File original, File output) {

		try {

			Scanner in = new Scanner(original);
			String quote = in.nextLine();

			for(Letter l : combos) {

				String s;

				if(l.possible.size() == 1) {
					s = (l.possible.get(0) + "").toLowerCase();
				} else {
					s = "{";
					for(char c : l.possible) {
						s += c;
					}
					s += "}";
					s = s.toLowerCase();
				}

				quote = quote.replaceAll("[" + l.c + "]", s);

			}

			PrintWriter p = new PrintWriter(output);
			p.print(quote);
			p.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

	}

	public static void purifyWords() {

		for(int i = encrypted.size() - 1; i >= 0; i--) {

			if(encrypted.get(i).text.matches("[A-Z]+[!?.,;:]")) {
				encrypted.get(i).text = encrypted.get(i).text.substring(0, encrypted.get(i).text.length() - 1);
			}

			if(!encrypted.get(i).text.matches("[A-Z]+")) encrypted.remove(i);

		}

	}

	public static void setupWordList(String folderName) {

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

			for(String s : wordList.get(w.text.length() - 1)) {

				if(possible(w.text, s)) w.possible.add(s);

			}

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

//			System.out.println("combo removed: " + encrypted + " -> " + destination);

			if(combos.get(encrypted - 'A').possible.size() == 0) {

				System.err.println("Error: impossibility on character " + encrypted);

			} else if(combos.get(encrypted - 'A').possible.size() == 1) {

				for(int i = 'A'; i < 'Z'; i++) {
					if(i != encrypted) {

						removeCombo((char) i, combos.get(encrypted - 'A').possible.get(0));

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
			reverseCombos.get(i - 'A').possible = new ArrayList<>();
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

	public static void printCombos(File f) {

		try {

			PrintWriter p = new PrintWriter(f);

			p.print("+");

			for(int i = 0; i < 26; i++) {
				p.print("---+");
			}

			p.print("\n|");

			for(int i = 0; i < 26; i++) {
				p.print(" " + ((char) (i + 'A')) + " |");
			}

			p.print("\n+");

			int maxLength = 0;

			for(int i = 0; i < 26; i++) {
				p.print("---+");

				maxLength = Math.max(maxLength, combos.get(i).possible.size());

			}

			for(int i = 0; i < maxLength; i++) {

				p.print("\n|");

				for(Letter l : combos) {

					if(l.possible.size() > i) {
						p.print(" " + l.possible.get(i) + " |");
					} else {
						p.print("   |");
					}

				}

			}

			p.print("\n+");

			for(int i = 0; i < 26; i++) {
				p.print("---+");
			}

			p.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

	}
	
	//// Tony's stuff I transferred ////
	
	public static ArrayList<String> caesarSolve(String input, ArrayList<String> dic, int tol) {
		ArrayList<String> possib = new ArrayList();

		input = input.toLowerCase();

		// loops through string and removes punctuation
		String temp = "";
		for (int j = 0; j < input.length(); j++) {
			if (((int) input.charAt(j)) > 96 && ((int) input.charAt(j)) < 123 || (int) input.charAt(j) == 32) {
				temp = temp + input.substring(j, j + 1);
			}
		}
		input = temp;

		// loops through the alphabet, checking for solutions with full words.
		for (int i = 0; i < 26; i++) {
			String curCipher = "";
			// loops through each character in the word, adding specified amount to char
			// value
			for (int w = 0; w < input.length(); w++) {
				int intValueOfChar = (int) input.charAt(w);

				// keeps spaces
				if (intValueOfChar == 32) {
				}

				// loops if value is past Z
				else if (intValueOfChar + i > 122) {
					intValueOfChar = 96 + ((intValueOfChar + i) - 122);
				}

				// adds one otherwise
				else {
					intValueOfChar += i;
				}

				curCipher = curCipher + ((char) intValueOfChar);

			}
			possib.add(curCipher);
		}
		return goodSolutions(possib, dic, tol);
	}
	
	public static ArrayList<String> goodSolutions(ArrayList<String> possib, ArrayList<String> dic, int tol) {
		// loops through every possibility
		ArrayList<String> goodSol = new ArrayList();
		for (int i = 0; i < possib.size(); i++) {
			if (i % 100 == 0) {
				System.out.println("checking for good solutions, checked " + i + " possibilities, found "
						+ goodSol.size() + " solutions");
			}
			int totalCorrectWords = 0;
			String[] curPossib = possib.get(i).split(" ");

			// loops through each word in the possibility
			for (int w = 0; w < curPossib.length; w++) {

				// binary searches for that word in the ArrayList of all known words
				if (dic.indexOf(curPossib[w]) > -1) {
					totalCorrectWords++;
				}
			}
			if (totalCorrectWords > tol) {
				goodSol.add(possib.get(i) + " ||  total correct words: " + totalCorrectWords);
			}

		}
		return goodSol;
	}
	
	public static int setupCaesarQuotes(File f) {
		
		try {

			Scanner in = new Scanner(f);

			int counter = 0;

			while(in.hasNextLine()) {

				PrintWriter p = new PrintWriter(new File("Input/CaesarQuote" + counter + ".txt"));
				p.print(in.nextLine());
				p.close();

				counter++;

			}

			in.close();
			
			return counter;
			
		} catch(FileNotFoundException e) {
			e.printStackTrace();
			return 0;
		}
		
	}
	
	public static void setupCaesar(File f) {
		
		try {
			
			Scanner in = new Scanner(f);
			
			while(in.hasNextLine()) {
				caesarDictionary.add(in.nextLine());
			}
			
		} catch(FileNotFoundException e) {
			e.printStackTrace();
		}
		
	}

	
}
