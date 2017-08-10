// Jacob Leighty

import java.util.*;
import java.io.*;

public class JavasweeperManager {
	private int gridSizeX;
	private int gridSizeY;
	private Map<String, String> mineField; // field with all locations visible.
	private Map<String, String> mineField2; // field with all blank fields for user display.
	private int mineCount;
	private int moveCount;
	private boolean detonation;
	private int fieldSize;
	private int flagCount;
	private int correctGuesses;
	
	public static final int MAX_Y_SIZE = 200; // Arbitrary Y axis limit.
	
	// Creates a new JavasweeperManager object with grid size, total spaces, a field with
	// invisible mines, a field with visible mines, and a mine count.
	public JavasweeperManager(int x, int y, int mines) {
		gridSizeX = x;
		if (x > 26) {
			gridSizeX = 26; // Keeps user from making a grid wider than the alphabet.
		}
		gridSizeY = y;
		if (y > MAX_Y_SIZE) {
			gridSizeY = MAX_Y_SIZE;
		}
		fieldSize = gridSizeX * gridSizeY;
		mineField = new TreeMap<String, String>();
		mineField = createMap(x, y, mines);
		mineField2 = createMap(x, y, 0);
		mineCount = mines;
		flagCount = mines;
	}
	
	// Fills a mine field with empty spaces and mines.
	private Map<String, String> createMap(int x, int y, int mines) {
		if (x <= 0 && y <= 0) {
			throw new IllegalArgumentException("Your grid dimensions are invalid!");
		}
		Map<String, String> mineFieldX = new TreeMap<String, String>();
		for (int i = 0; i < gridSizeY; i++) {
			for (int j = 0; j < gridSizeX; j++) {
				mineFieldX.put("" + ((char) ('A' + j)) + (i + 1), " - ");
			}
		}
		populateMines(mines, mineFieldX);
		return mineFieldX;
	}
	
	// Adds given number of mines to the field in random placement.
	private void populateMines(int mines, Map<String, String> mineFieldX) {
		Random r = new Random();
		Random r2 = new Random();
		int temp = mines;
		while (temp != 0) {
			int keyPart1 = 'A' + r.nextInt(gridSizeX);
			int keyPart2 = r2.nextInt(gridSizeY) + 1;
			String key = "" + ((char) keyPart1) + keyPart2;
			if (mineFieldX.get(key) != " * ") {
				mineFieldX.remove(key);
				mineFieldX.put(key, " * ");
				temp--;
			}
		}
	}

	// Plots the user's correct guess on the grid.
	public void updateGrid(String coordinate) {
		if (coordinate.length() <= 1 || coordinate.length() > 4) {
			throw new IllegalArgumentException("Your guessed coordinate must be of valid length.");
		}
		char x = coordinate.charAt(0);
		int y = Integer.parseInt(coordinate.substring(1));
		if (mineField2.get(coordinate) != " x " && y <= gridSizeY && 
				coordinate.charAt(0) <= gridSizeX + 'A') {
			String value = checkSurroundings(coordinate);
			mineField.remove(coordinate);
			mineField.put(coordinate, " x ");
			mineField2.remove(coordinate);
			mineField2.put(coordinate, value);
			moveCount++;
			fieldSize--;
		}
	}
	
	// Checks the given coordinate's surroundings for mines.
	private String checkSurroundings(String coordinate) {
		if (coordinate.length() <= 1 || coordinate.length() > 4) {
			throw new IllegalArgumentException("Your guessed coordinate must be of valid length.");
		}	
		int surroundings = 0;
		char x = coordinate.charAt(0);
		int y = Integer.parseInt(coordinate.substring(1));
		
		surroundings += checkRight(x, y);
		surroundings += checkLeft(x, y);
		surroundings += checkUp(x, y);
		surroundings += checkUpRight(x, y);  // I need these to properly reveal all surrounding empty
		surroundings += checkUpLeft(x, y);   // spaces without getting overwhelmed with recursion things
		surroundings += checkDown(x, y);     // in updateGrid.
		surroundings += checkDownRight(x, y);
		surroundings += checkDownLeft(x, y);

		if (surroundings == 0) {
			return " x ";
		} else {
			return " " + surroundings + " ";
		}
	}
	
	// Checks the given coordinate's right side for mines.
	private int checkRight(int x, int y) {
		if (mineField.get("" + ((char) x) + (y + 1)) == " * ") {
			return 1;
		}
		return 0;
	}
	
	// Checks the given coordinate's left side for mines.
	private int checkLeft(int x, int y) {
		if (mineField.get("" + ((char) x) + (y - 1)) == " * ") {
			return 1;
		}
		return 0;	
	}
	
	// Checks the given coordinate's upper side for mines.
	private int checkUp(int x, int y) {
		if (mineField.get("" + ((char) (x + 1)) + y) == " * ") {
			return 1;
		}
		return 0;	
	}
	
	// Checks the given coordinate's upper right side for mines.
	private int checkUpRight(int x, int y) {
		if (mineField.get("" + ((char) (x + 1)) + (y + 1)) == " * ") {
			return 1;
		}
		return 0;	
	}
	
	// Checks the given coordinate's upper left side for mines.
	private int checkUpLeft(int x, int y) {
		if (mineField.get("" + ((char) (x + 1)) + (y - 1)) == " * ") {
			return 1;
		}
		return 0;	
	}
	
	// Checks the given coordinate's lower side for mines.
	private int checkDown(int x, int y) {
		if (mineField.get("" + ((char) (x - 1)) + y) == " * ") {
			return 1;
		}
		return 0;	
	}
	
	// Checks the given coordinate's lower right side for mines.
	private int checkDownRight(int x, int y) {
		if (mineField.get("" + ((char) (x - 1)) + (y + 1)) == " * ") {
			return 1;
		}
		return 0;	
	}
	
	// Checks the given coordinate's lower left side for mines.
	private int checkDownLeft(int x, int y) {
		if (mineField.get("" + ((char) (x - 1)) + (y - 1)) == " * ") {
			return 1;
		}
		return 0;	
	}
								
	// Prints the current mine field without mines visible. Guesses with mines surrounding
	// them are displayed as numbers.
	public void printFieldA() {
		printField(mineField2);
	}
	
	// Prints the current mine field with mines and guesses visible.
	public void printFieldB() {
		printField(mineField);
	}

	// Prints the given mine field.
	private void printField(Map<String, String> mineField) {
		if (gridSizeX < 1 || gridSizeY < 1) {
			throw new IllegalArgumentException("Your guessed coordinate must be of valid length.");
		}		
		String row1 = "    A";
		for (int i = 1; i < gridSizeX; i++) {
			row1 += "  " + ((char) ('A' + i));
		}
		System.out.println(row1);
		
		String rows = "";
		for (int i = 0; i <= gridSizeY - 1; i++) {
			if (i < 9) {
				rows += " " + (i + 1) + " ";
			} else if (i >= 9 && i < 99) {
				rows += (i + 1) + " ";
			} else {
				rows += (i + 1);
			}
			for (int j = 0; j < gridSizeX; j++) {
				rows += mineField.get("" + ((char) ('A' + j)) + (i + 1));
			}			
			System.out.println(rows);
			rows = "";
		}
	}
	
	// Adds or removes a flag at the given coordinate.
	public void flag(String key) {
		if (flagCount < 0) {
			throw new IllegalStateException("You can't have less than 0 flags!");
		}
		if (mineField2.get(key) == " > ") {
			mineField2.remove(key);
			mineField2.put(key, " - ");
			flagCount++;
			moveCount++;
		} else {
			if (flagCount() > 0) {
				mineField2.remove(key);
				mineField2.put(key, " > ");
				if (mineField.get(key) == " * ") { 
					correctGuesses++;
				}
				flagCount--;
				moveCount++;
			} else {
				if (mineField2.get(key) == " > ") {
					mineField2.remove(key);
					mineField2.put(key, " - ");
					flagCount++;
					moveCount++;
				}
			}
		}		
	}
		
	// Returns the current minefield.
	public Map<String, String> mineFieldGet() {
		return mineField;
	}
	
	// Returns the total number of spaces in the minefield.
	public int fieldSize() {
		return fieldSize;
	}
	
	// Returns how many mines left there supposedly are.
	public int minesCount() {
		return mineCount;
	}
	
	// Returns number of turns user has taken.
	public int moves() {
		return moveCount;
	}

	// Returns whether or not chosen space has a flag on it.
	public boolean flagPresent(String key) {
		return (mineField2.get(key) == " > ");
	}
	
	// Returns how many flags are on mines.
	public int correctGuessCount() {
		return correctGuesses;
	}
	
	// Returns how many flags the user has left.
	public int flagCount() {
		return flagCount;
	}
	
	// Returns whether or not chosen space has a mine in it.
	public boolean mineFound(String key) {
		if (mineField.get(key) == " * " && !flagPresent(key)) {
			detonation = true;
			moveCount++; 
			return true;
		}
		return false;
	}
	
	// Determines if game is over.
	public boolean gameOver() {
		return detonation;
	}
}