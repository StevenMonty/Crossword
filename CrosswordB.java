import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;


// TODO final output should just be the number of solutions found
// TODO if no solution print no solution

public class CrosswordB {
	
	private final int PREFIX = 1;			
	private final int WORD = 2;
	private final int WORD_AND_PREFIX = 3;	// Return values from searchPrefix	
	private final char OPEN_SPACE  = '+';	// Sentinel char representing an open, playable board space
	private final char SOLID_SPACE = '-';	// Sentinel char representing a solid, non-playable board space
	private static int size;				// Number of rows and cols in the board
	private static int solutions = 0;		// Number of possible solutions found
	private static char[][] theBoard;		// The game board
	private static boolean[][] finalChar;	// Represents given letters that are immutable
	private static DictInterface dict;		// The data structure to store the English dictionary
	private static StringBuilder[] strRow;	// Each index contains the contents of the row for the current solution being attempted
	private static StringBuilder[] strCol;	// Each index contains the contents of the col for the current solution being attempted
	private long startTime, endTime;		// Used in runtime calculations

	public static void main (String[] args) {
		new CrosswordB(args);
	}

	public CrosswordB(String[] args) {

		startTime = System.nanoTime();

		Scanner dictionary = null, board = null;	// Create Scanners for the dictionary and board files
		String dictType = null;
		
		try {										// Attempt to open the files and initialize the Scanners 
			dictType = args[0];
			dictionary = new Scanner (new FileInputStream(args[1]));
			board = new Scanner (new FileInputStream(args[2]));
		} catch (ArrayIndexOutOfBoundsException e) {
			System.err.println("Missing CLI Arguments");
			System.err.println("Usage: java CrosswordB DICT_MODE dictionaryFile.txt crosswordBoard.txt");
			System.err.println("DICT_MODE = 'DLB' to use the De la Briandais trie, else to use the MyDictionary");
			System.exit(1);
		} catch (FileNotFoundException e) {
			System.err.println("One of more of your files were not found. Try again.");
			System.exit(1);
		}
		
		if (dictType.equalsIgnoreCase("DLB"))			
			dict = new DLB();
		else
			dict = new MyDictionary();
		
		size = board.nextInt();	//	Read the integer at the top of the file representing the rows and cols of the board
		board.nextLine();		//	Consume the next line char

		String line;
		while (dictionary.hasNext()) {		//	Populate the MyDictionary from the dictionary file contents
			line = dictionary.nextLine();
			
			if (line.length() <= size)		//	Prune the dictionary to only contain words that will fit on the board
				dict.add(line);
		}
		dictionary.close();

		theBoard = new char[size][size];	//	Initialize the board to be of size x size
		finalChar = new boolean[size][size];

		for (int i = 0; i < size; i++)		//	Fill in the starting board from the text file
		{
			line = board.nextLine();
			for (int j = 0; j < line.length(); j++)	
				theBoard[i][j] = line.charAt(j);
		}
		board.close();

		strRow = new StringBuilder[size];	// Allocate the array of string builders
		strCol = new StringBuilder[size];	// Allocate the array of string builders

		for (int i = 0; i < size; i++) {
			strRow[i] = new StringBuilder();	// Initialize the StringBuilders to hold the row data
			strCol[i] = new StringBuilder();	// Initialize the StringBuilders to hold the col data
		}
		
		transformBoard(theBoard);	// Populate the finalChar array using the contents of the board

		solve(0,0);	//	Start the recursive solve method at the first board space
		
		endGame();	// Exits the program, prints the number of solutions found
	}

	/**
	 * Recursive backtracking algorithm to determine the next correct solution 
	 * @param row
	 * @param col
	 */
	public void solve(int row, int col) {

		if (theBoard[row][col] == SOLID_SPACE) {
			append(strRow[row], strCol[col], SOLID_SPACE);	// Perform the actual append
			if (row == size -1 && col == size -1) {
				solutions++;		// Solution found, increment counter
			} else {
				if(col < size -1)	//	Move to the next column to the right
					solve(row, col+1);
				else				//	At the rightmost column, move to the first col of the next row
					solve(row+1, 0);
			}
			unAppend(strRow[row], strCol[col]);
		}
		else {
			for (char c = 'a'; c <= 'z'; c++) {
				if (isValid(row, col, c)) {
					append(strRow[row], strCol[col], c);	// Perform the actual append
					if (row == size -1 && col == size -1) {
						solutions++;	// Solution found, increment counter
					} else {
						if(col < size -1)	//	Move to the next column to the right
							solve(row, col+1);
						else				//	At the rightmost column, move to the first col of the next row
							solve(row+1, 0);
					}
					unAppend(strRow[row], strCol[col]);
				}
			}
		}
	}

	/**
	 * Validation method to determine if the next possible move is either a prefix or entire word
	 * @param row
	 * @param col
	 * @param c
	 * @return
	 */
public boolean isValid(int row, int col, char c) {
		
		if (finalChar[row][col] && theBoard[row][col] != c)	// Check if the current char is a given immutable char from the board file
			return false;
		else if (theBoard[row][col] != OPEN_SPACE && theBoard[row][col] != c)
			return false;
		else if (row <= size -1 && col <= size -1) 	// Valid mode - Temp append to check if prefix/word exists
			append(strRow[row], strCol[col], c);
		
		String SOLID = String.valueOf(SOLID_SPACE);	// Cast the solid space char into a string for the lastIndexOf method
		int rowRes = dict.searchPrefix(strRow[row], strRow[row].lastIndexOf(SOLID)+1, strRow[row].length()-1);
		int colRes = dict.searchPrefix(strCol[col], strCol[col].lastIndexOf(SOLID)+1, strCol[col].length()-1);
		unAppend(strRow[row], strCol[col]);

		// At the final space of the board
		// Row must be a word or word and prefix, Col must be a word or a word and prefix
		if (col == size-1 && row == size -1) {
			return (rowRes == WORD || rowRes == WORD_AND_PREFIX) && (colRes == WORD || colRes == WORD_AND_PREFIX);
		}

		// In the last column, or to the left of a solid space
		// Row must be a word or word and prefix
		 if (col == size-1 || theBoard[row][col+1] == SOLID_SPACE) {
			if  (rowRes != WORD && rowRes != WORD_AND_PREFIX)
				return false;
		} else {	// not at last col, row must be prefix 
			if (rowRes != PREFIX && rowRes != WORD_AND_PREFIX)
				return false;
		}

		// In the bottom row, or currently above a solid space
		// Col must be a word or word and prefix
		 if (row == size-1 || theBoard[row+1][col] == SOLID_SPACE) {
			if (colRes != WORD && colRes != WORD_AND_PREFIX)
				return false;
		}

		// Somewhere in the middle of the board
		// Row must be a word or word and prefix, Col must be a prefix or a word and prefix
		else {
			if (colRes != PREFIX && colRes != WORD_AND_PREFIX)
				return false;
		}
		 
		 return true;	// If none of the above cases were true, then the move is valid
	}
	
	/**
	 * Utility method to append a character to the StringBuilders
	 * @param row
	 * @param col
	 * @param c
	 */
	public void append(StringBuilder row, StringBuilder col, char c) {
		row.append(c);
		col.append(c);
	}
	
	/**
	 * Removes the last char from the given StringBuilders to aid in the isValid check method
	 * @param row
	 * @param col
	 */
	public void unAppend(StringBuilder row, StringBuilder col) {
		row.deleteCharAt(row.length()-1);
		col.deleteCharAt(col.length()-1);
	}

	/**
	 * Change the board from the initial input form into a playable form.
	 * Sets the finalChar array to finalize the letters read in from the board file.
	 * @param board
	 */
	public void transformBoard(char[][] board){

		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				if (board[i][j] != OPEN_SPACE && board[i][j] != SOLID_SPACE) {	
					finalChar[i][j] = true;	// if the current space is a provided letter, it can't be changed by the solve method
				}
			}
		}
	}
	
	/**
	 * Displays the solutions contained in the StringBuilders representing the row data
	 */
	public void printBoard() {
		
//		System.out.println("Solution Found: ");
		
		for (int i = 0; i < size; i++)
			System.out.println(strRow[i].toString());
		
		System.out.println();
	}
	
	/**
	 * Ends the game after one solution is found and prints the runtime
	 */
	public void endGame() {			

//		endTime = System.nanoTime();
//		
//		System.out.printf("Solutions Found: %d\n", solutions);
//
//		long durNano = (endTime - startTime);
//		long durMins = TimeUnit.NANOSECONDS.toMinutes(durNano);
//		long durSec = TimeUnit.NANOSECONDS.toSeconds(durNano) - (durMins * 60);
//		long durMili = TimeUnit.NANOSECONDS.toMillis(durNano);
//
//		System.out.printf("%d:%d Min:Sec \t%d  Total Miliseconds\n", durMins, durSec, durMili);
		
		System.out.println(solutions);

		System.exit(0);
	}
}
