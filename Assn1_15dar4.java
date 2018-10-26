/* Assignment 1: Game of Pig (Big Pig version)
  Course: CMPE 212
  Code by: Declan Rowett
  NetID: 15dar4
  Student number: 10211314
  Last updated: February 2nd, 2018

  Big Pig Rules:
   The first player to accumulate a score of 100 or more wins.
   The human goes first.
   After one roll, a player has the choice to "hold" or to roll again.
   You roll two dice. Certain conditions apply:
  	- If both dice are ones, then you add 25 to your turn score, and you must roll again.
  	- If one die is one, then your turn is over and your turn score is set to zero.
  	- If both dice match ("doubles"), other than ones, then you gain twice the sum of the dice, and you must roll again.
  	- For any other dice combination, you just add the dice total to your turn score and you have the choice of rolling again.
   When your turn is over, either through your choice or you rolled a one, then your turn sum is added to your accumulated score.

  This code is able to play a "best-of" game by changing the constant variable BEST_OF.
  For example, changing it to 3 would mean you would play games until someone wins twice.
  Games are always played back-to-back.
 */

import java.util.Random;
import java.util.Scanner;

public class Assn1_15dar4 {

	private static Scanner screenInput = new Scanner(System.in);
	private static Random generator = new Random(System.currentTimeMillis());
	// Useful constants that allow for easy alteration of winning score, computer strategy, etc.
	private static final int WIN_SCORE = 100;
	private static final int COMP_HOLD = 35;
	private static final int BEST_OF = 1; // Modify this if you want to play a best-of
	private static final String ROLL_AGAIN = "y";
	private static final String HOLD = "n";
	private static final String PLAYER_WIN_MESSAGE = "*****PLAYER WINS*****";
	private static final String COMP_WIN_MESSAGE = "*****COMPUTER WINS*****";

	public static void main(String[] args) {
		// Used in a single game
		int humanScore;
		int compScore;
		int round;
		// Used to keep track of best-of games
		int humanWins = 0;
		int compWins = 0;
		int game = 1;
		// Variables to be used by both players
		int die1 = 0;
		int die2 = 0;
		int turnScore = 0;
		while (humanWins < (BEST_OF/2) + 1 && compWins < (BEST_OF/2) + 1) {
			// Reset scores and round counter in between best-of games
			humanScore = 0;
			compScore = 0;
			round = 1;
			// Loop until either player or computer reaches winning score
			while (humanScore < WIN_SCORE && compScore < WIN_SCORE ) {
				// Human turn
				int humanRoundPoints = humanTurn(turnScore, die1, die2, humanScore);
				humanScore += humanRoundPoints;
				System.out.println("Player total score is " + humanScore + ".\n");
				if (humanScore >= WIN_SCORE) {
					System.out.println(PLAYER_WIN_MESSAGE);
					humanWins++;
					break;
				}
				// Computer turn
				int compRoundPoints = compTurn(turnScore, die1, die2, compScore);
				compScore += compRoundPoints;
				System.out.println("Computer total score is " + compScore + ".\n");
				if (compScore >= WIN_SCORE) {
					System.out.println(COMP_WIN_MESSAGE);
					compWins++;
					break;
				}
				System.out.println("Player current total: " + humanScore + ", Computer current total: " + compScore);
				round++;
				// Pause game
				System.out.println("Press <ENTER> to start round " + round + ".");
				screenInput.nextLine();
			} // End game loop
			game++;
			if (humanWins < (BEST_OF/2) + 1 && compWins < (BEST_OF/2) + 1) { // Exists to prevent from showing when best-of is over
				System.out.println("\nPlayer wins: " + humanWins + ", Computer wins: " + compWins + ".");
				System.out.println("Playing best-of " + BEST_OF + " games.");
				System.out.println("Press <ENTER> to start game " + game + ".");
				screenInput.nextLine();
			}
		} // End best-of loop
		gameSummary (compWins, humanWins);
	}

	// Plays human turn until human chooses to end or rolls a one
	public static int humanTurn(int turnScore, int d1, int d2, int humanScore) {
		turnScore = 0; // Reset turn score at beginning of turn
		do {
			d1 = rollDie();
			d2 = rollDie();
			System.out.println("Player rolled " + numToWord(d1) + " + " + numToWord(d2));
			int rollScore = checkRoll(d1,d2);
			if (rollScore == 0)
				turnScore = 0;
			// End automatically if means a win and didn't roll doubles
			else if (d1 != d2 && checkWin(turnScore, rollScore, humanScore)) {
				turnScore += rollScore;
				break;
			}
			// Roll again if doubles
			else if (d1 == d2) {
				turnScore += rollScore;
				updateScore(turnScore, humanScore);
				System.out.println("Player must roll again!");
				continue;
			}
			// Any other roll combination gives player choice of rolling again
			else {
				turnScore += rollScore;
				updateScore(turnScore, humanScore);
				if(!playerChoice()) // Ends turn if player typed 'n'
					break;		
			}
		} while (turnScore != 0); // End human turn
		return turnScore;
	}

	// Plays computer turn until threshold for holding is met or a one is rolled
	public static int compTurn(int turnScore, int d1, int d2, int compScore) {
		turnScore = 0; // Reset turn score and beginning of turn
		do {
			d1 = rollDie();
			d2 = rollDie();
			System.out.println("Computer rolled " + numToWord(d1) + " + " + numToWord(d2));
			int rollScore = checkRoll(d1,d2);
			if (rollScore == 0)
				turnScore = 0;
			// End automatically if means a win and didn't roll doubles
			else if (d1 != d2 && checkWin(turnScore, rollScore, compScore)) {
				turnScore += rollScore;
				break;
			}
			// Roll again if doubles
			else if (d1 == d2) {
				turnScore += rollScore;
				updateScore(turnScore, compScore);
				System.out.println("Computer must roll again!");
				continue;
			}
			// Any other roll combination gives computer choice of rolling again
			else {
				turnScore += rollScore;
				updateScore(turnScore, compScore);
				if(turnScore >= COMP_HOLD)
					break;					
			}
		} while (turnScore != 0); // End computer turn
		return turnScore;
	}

	// Returns a random integer from 1-6 to simulate die roll
	public static int rollDie() {
		int a = generator.nextInt(6) + 1;
		return a;
	}

	// Converts number to word equivalent using an array
	public static String numToWord(int a) {
		String[] numbers = {"zero", "one", "two", "three", "four", "five", "six"};
		String die = numbers[a];
		return die;
	}

	// Returns score to be added to turnScore based on roll types
	public static int checkRoll(int a, int b) {
		int turnScore = 0;
		if (a == 1 && b == 1) {
			System.out.println("Double Ones!!");
			return turnScore = 25;
		}
		else if (a == b) {
			System.out.println("Doubles!!");
			return turnScore = 2*(a+b);
		}
		else if (a == 1 || b == 1) {
			System.out.println("Turn over!");
			return turnScore;
		}
		else
			return turnScore = a+b;
	}

	// Prints out turn score
	public static void updateScore(int turnScore, int playerScore) {
		System.out.println("Turn score is " + turnScore + "." + " Total score would be " + (turnScore + playerScore) + ".");
	}

	// Checks if holding roll would mean a win
	public static boolean checkWin(int turnScore, int rollScore, int playerScore) {
		return (turnScore += rollScore) + playerScore >= WIN_SCORE;
	}

	// Gets user input until 'y' or 'n' was entered
	public static boolean playerChoice() {
		String userEntry = "";
		boolean validInput = false;
		while (!validInput) {
			System.out.print("Roll again? (type 'y' or 'n'): ");
			userEntry = screenInput.nextLine();
			if (userEntry.equals(ROLL_AGAIN) || userEntry.equals(HOLD))
				validInput = true;
			else
				System.out.println("Not a valid answer!");
		}
		return userEntry.equals(ROLL_AGAIN);
	}

	// Prints who wins and how many wins each player got
	public static void gameSummary (int compWins, int humanWins) {
		String winner = "Computer";
		String plural = "s";
		int winnerGames = compWins;
		int loserGames = humanWins;
		if (humanWins > compWins) {
			winner = "Human";
			winnerGames = humanWins;
			loserGames = compWins;
		}
		if (winnerGames == 1)
			plural = "";
		System.out.println(winner + " wins " + winnerGames + " game" + plural + " to " + loserGames + ".");
	}
}