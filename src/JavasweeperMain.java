// Jacob Leighty

// methods you can call:
	// JavasweeperManager(int X, int Y, int mines) - constructor to create object.
	// updateGrid(String userGuess) - plots the user's newest guess on the field and updates map accordingly.
	// printFieldA() - prints the current playing field with mines non-visible.
	// printFielB() - prints the current playing field with mines visible.
	// mineFieldGet() - returns the current map.
	// fieldSize() - returns total number of possible spaces on the field.
	// minesCount() - returns total number of mines.
	// moves() - returns how many moves user has made.
	// flagPresent() - returns if a flag is present at the given coordinate.
	// correctGuessCount() - returns number of flags on mines.
	// flagCount() - returns how many flags the user has left.
	// mineFound(String userGuess) - returns whether or not the given coordinate contains a mine.
	// gameOver() - returns whether or not the game is over.
	
import java.util.*;
import java.io.*;

public class JavasweeperMain {
	
	public static void main(String [] args) {
		// Collects user input for grid size and mine number.
		System.out.println("Welcome to Jacob's Javasweeper game.");
		System.out.println();
		Scanner console = new Scanner(System.in);
		System.out.print("What would you like the grid's dimensions to be? (ex. 2x2) ");
		String size = console.next();
		int[] dimensions = new int[2];
		dimensions = parseCoordinates(size, console);
		while (dimensions[0] <= 0 || dimensions[1] <= 0) {
			System.out.print("Please enter numbers greater than 0. ");
			size = console.next();
			dimensions = parseCoordinates(size, console);
		}
		System.out.print("How many mines would you like? (more than 0) ");
		int mines = console.nextInt();
		while (mines <= 0 || mines > dimensions[0] * dimensions[1]) {
			System.out.print("You can't have more/less mines than there are spaces." + 
				" Please enter another number. ");
			mines = console.nextInt();
		}
		
		// Prepares the game.
		JavasweeperManager javasweeper = new JavasweeperManager(dimensions[0], dimensions[1], mines);
		System.out.println();
		javasweeper.printFieldA();
		System.out.println();
		System.out.println("Remaining spaces: " + javasweeper.fieldSize());
		System.out.println("Total mines: " + javasweeper.minesCount());
		playGame(console, javasweeper);
	}
	
	// Starts a game of minesweeper and continues until the user hits a mine or wins, which is
	// defined by having all mines covered by flags or having all non-mine spaces searched.
	public static void playGame(Scanner console, JavasweeperManager javasweeper) {
		while (!javasweeper.gameOver() && javasweeper.fieldSize() - javasweeper.minesCount() > 0 
					&& javasweeper.correctGuessCount() != javasweeper.minesCount()) {
			String coordinate = "";
			System.out.println();
			if (javasweeper.moves() == 0) {
				System.out.print("Where would you like to search next? Enter a grid coordinate (ex: A1) ");
				coordinate = console.next().toUpperCase();
				while (coordinate.length() <= 1 || coordinate.length() > 4) {
					System.out.print("Please enter a valid coordinate. (ex: A1) ");
					coordinate = console.next().toUpperCase();
				}
			} else {
				coordinate = instructions(console, javasweeper);
			}
			if (coordinate.substring(0, 2).equalsIgnoreCase("FL")) { // placing a flag scenario
				flagScenario(console, javasweeper);
			} else {
				// hitting a mine scenario
				if (javasweeper.mineFound(coordinate) && !javasweeper.flagPresent(coordinate)) {
					mineScenario(javasweeper);
				} else {
					// hitting a flag scenario
					if (javasweeper.flagPresent(coordinate)) {
						System.out.println();
						System.out.println("You already have a flag at coordinate " + coordinate + 
							". Type \"FL\" to remove it.");
					} else {
						javasweeper.updateGrid(coordinate);
					}
					System.out.println();
					javasweeper.printFieldA();
				}
			}
		}
		System.out.println();
		if (!javasweeper.gameOver()) {
			System.out.println("Congratuations, you won!");
		}
		System.out.println("Total turns: " + (javasweeper.moves()));
	}
	
	// Prints information on the current turn and get's the user's next guess.
	public static String instructions(Scanner console, JavasweeperManager javasweeper) {
		System.out.println("Remaining spaces: " + javasweeper.fieldSize());
		System.out.println("Remaining flags/mines (?): " + javasweeper.flagCount());
		System.out.println();
		System.out.print("Where would you like to search next? OR, type \"FL\" to place/remove a flag. ");
		String coordinate = console.next().toUpperCase();
		while (coordinate.length() <= 1 || coordinate.length() > 4) {
			System.out.print("Please enter a valid coordinate. (ex: A1) ");
			coordinate = console.next().toUpperCase();
		}
		return coordinate;
	}
	
	// Enters flag scenario, allowing user to place a flag on the field if they have any left.
	public static void flagScenario(Scanner console, JavasweeperManager javasweeper) {
		System.out.print("Where would you like to place/remove a flag? ");
		String coordinate = console.next().toUpperCase(); 
		if (javasweeper.flagCount() >= 0) {
			javasweeper.flag(coordinate);
		} else {
			System.out.println("You have no more flags to place!");
		}
		System.out.println();
		javasweeper.printFieldA();
	}
	
	// Enters mine scenario, ending the game when the user hits a mine.
	public static void mineScenario(JavasweeperManager javasweeper) {
		System.out.println();
		System.out.println("BOOOOOOOOOOOOOM! You hit a mine! Game over.");
		System.out.println();
		javasweeper.printFieldB();
	}
	
	// Extracts the x and y coordinates from the user input.
	public static int[] parseCoordinates(String size, Scanner console) {
		boolean onlyX = testForX(size);
		while (!onlyX) {
			System.out.print("Please enter dimensions in with an x between them. (ex. 2x2) ");
			size = console.next();
			onlyX = testForX(size);
		}
		String[] numbers = new String[2];
		numbers = size.toLowerCase().split("x+");
		Scanner xDim = new Scanner(numbers[0]);
		Scanner yDim = new Scanner(numbers[1]);
		int[] xAndY = new int[2];
		xAndY[0] = xDim.nextInt();
		xAndY[1] = yDim.nextInt();
		return xAndY;
	}
	
	// Tests to see if the user separated their grid dimensions with an x.
	public static boolean testForX(String size) {
		String alphabetTest = "abcdefghijklmnopqrstuvwyz";
		for (int i = 0; i < alphabetTest.length(); i++) {
			if (size.contains(alphabetTest.substring(i, i + 1))) {
				return false;
			}
		}
		return true;
	}
}