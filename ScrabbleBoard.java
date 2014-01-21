/* ScrabbleBoard Class
 * Jolie Nazor and Kristi Yost
 * December 9, 2013
 * Dr. Ravikumar
 * CS 454 - Theory of Computation
 * Semester Project: Scrabble
 * 
 * Creates the character array that represents the board
 * Creates the integer arrays that represent where the
 * word and letter multipliers are on the board
 * Creates letter pieces and what the values of each letter is
 */


import java.util.*;
import java.lang.*;


public class ScrabbleBoard {
	
	int[][] wordmult;
	int[][] lettermult;
	char[][] board;
	Hashtable<Character, Integer> letter_val;
	Vector<Character> pieces;
	
	// Default constructor - initializes wordmult, lettermult, board,
	//						letter_val and pieces to represent a Scrabble board
	public ScrabbleBoard () {
			 
		wordmult = new int[][]{{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
				 			   {0, 3, 0, 0, 0, 0, 0, 0, 3, 0, 0, 0, 0, 0, 0, 3, 0},
				 			   {0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 0, 0},
				 			   {0, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 0, 0, 0},
				 			   {0, 0, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 2, 0, 0, 0, 0},
				 			   {0, 0, 0, 0, 0, 2, 0, 0, 0, 0, 0, 2, 0, 0, 0, 0, 0},
				 			   {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
				 			   {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
				 			   {0, 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 3, 0},
				 			   {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
				 			   {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
				 			   {0, 0, 0, 0, 0, 2, 0, 0, 0, 0, 0, 2, 0, 0, 0, 0, 0},
				 			   {0, 0, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 2, 0, 0, 0, 0},
				 			   {0, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 0, 0, 0},
				 			   {0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 0, 0},
				 			   {0, 3, 0, 0, 0, 0, 0, 0, 3, 0, 0, 0, 0, 0, 0, 3, 0}, 
				 			   {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}};
				 			   
		lettermult = new int[][]{{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
								 {0, 0, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 2, 0, 0, 0, 0},
								 {0, 0, 0, 0, 0, 0, 3, 0, 0, 0, 3, 0, 0, 0, 0, 0, 0},
								 {0, 0, 0, 0, 0, 0, 0, 2, 0, 2, 0, 0, 0, 0, 0, 0, 0},
								 {0, 2, 0, 0, 0, 0, 0, 0, 2, 0, 0, 0, 0, 0, 0, 2, 0},
								 {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
								 {0, 0, 3, 0, 0, 0, 3, 0, 0, 0, 3, 0, 0, 0, 3, 0, 0},
								 {0, 0, 0, 2, 0, 0, 0, 2, 0, 2, 0, 0, 0, 2, 0, 0, 0},
								 {0, 0, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 2, 0, 0, 0, 0},
								 {0, 0, 0, 2, 0, 0, 0, 2, 0, 2, 0, 0, 0, 2, 0, 0, 0},
								 {0, 0, 3, 0, 0, 0, 3, 0, 0, 0, 3, 0, 0, 0, 3, 0, 0},
								 {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
								 {0, 2, 0, 0, 0, 0, 0, 0, 2, 0, 0, 0, 0, 0, 0, 2, 0},
								 {0, 0, 0, 0, 0, 0, 0, 2, 0, 2, 0, 0, 0, 0, 0, 0, 0},
								 {0, 0, 0, 0, 0, 0, 3, 0, 0, 0, 3, 0, 0, 0, 0, 0, 0},
								 {0, 0, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 2, 0, 0, 0, 0},
								 {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}};
								 
		board = new char[lettermult[0].length][lettermult[0].length];
		
		for(int i = 0; i < getWidth(); i++) {
			for(int j = 0; j < getWidth(); j++) {
				if( (i < 1 || i > getWidth() - 2) || (j < 1 || j > getWidth() - 2))
					board[i][j] = '\0';
				else
					board[i][j] = ' ';
			}
		}
		
		letter_val = new Hashtable<Character, Integer>();
		letter_val.put('a', 1);
		letter_val.put('b', 3);
		letter_val.put('c', 3);
		letter_val.put('d', 2);
		letter_val.put('e', 1);
		letter_val.put('f', 4);
		letter_val.put('g', 2);
		letter_val.put('h', 4);
		letter_val.put('i', 1);
		letter_val.put('j', 8);
		letter_val.put('k', 5);
		letter_val.put('l', 1);
		letter_val.put('m', 3);
		letter_val.put('n', 1);
		letter_val.put('o', 1);
		letter_val.put('p', 3);
		letter_val.put('q', 10);
		letter_val.put('r', 1);
		letter_val.put('s', 1);
		letter_val.put('t', 1);
		letter_val.put('u', 1);
		letter_val.put('v', 4);
		letter_val.put('w', 4);
		letter_val.put('x', 8);
		letter_val.put('y', 4);
		letter_val.put('z', 10);
		letter_val.put(' ', 0); 
		
		pieces = new Vector<Character>();
		
		generateAPiece((Character)'a', 9);
		generateAPiece((Character)'b', 2);
		generateAPiece((Character)'c', 2);
		generateAPiece((Character)'d', 4);
		generateAPiece((Character)'e', 12);
		generateAPiece((Character)'f', 2);
		generateAPiece((Character)'g', 3);
		generateAPiece((Character)'h', 2);
		generateAPiece((Character)'i', 9);
		generateAPiece((Character)'j', 1);
		generateAPiece((Character)'k', 1);
		generateAPiece((Character)'l', 4);
		generateAPiece((Character)'m', 2);
		generateAPiece((Character)'n', 6);
		generateAPiece((Character)'o', 8);
		generateAPiece((Character)'p', 2);
		generateAPiece((Character)'q', 1);
		generateAPiece((Character)'r', 6);
		generateAPiece((Character)'s', 4);
		generateAPiece((Character)'t', 6);
		generateAPiece((Character)'u', 4);
		generateAPiece((Character)'v', 2);
		generateAPiece((Character)'w', 2);
		generateAPiece((Character)'x', 1);
		generateAPiece((Character)'y', 2);
		generateAPiece((Character)'z', 1);		 
	}
	
	
	// transposeBoard - transposes the board so the vertical words
	//					are represented as horizontal, and the horizontal
	//					words are represented as vertical
	public void transposeBoard() {
	    char[][] temp = new char[getWidth()][getWidth()];
		for(int i = 0; i < getWidth(); i++)
			for(int j = 0; j < getWidth(); j++)
				temp[j][i] = board[i][j];
				
		board = temp;
	}

	
	// getWidth - returns the with of the board
	public int getWidth() {
		return board[0].length;
	}
	

	// print - prints out board in a way that represents a Scrabble board
	public void print() {
		for(int i = 0; i < getWidth(); i++) {
			for(int j = 0; j < getWidth(); j++)
				System.out.print(board[i][j] + " ");
			System.out.println();
		}
		System.out.println();
	}
	

	// getWordMultiplier - returns the integer value at wordmult[x][y]
	// Parameters:
	//		x - the first index
	//		y - the second index
	public int getWordMultiplier(int x, int y) {
		return wordmult[x][y];
	}
	

	// getLetterMultiplier - returns the integer value at lettermult[x][y]
	// Parameters:
	//		x - the first index
	//		y - the second index
	public int getLetterMultiplier(int x, int y) {
		return lettermult[x][y];
	}
	

	// getLetterVal - returns the value in letter_val of a letter
	// Parameters:
	//		c - the letter to get the value of
	public int getLetterVal(char c) {
		return letter_val.get(c);
	}
	

	// generateAPiece - puts a letter in pieces the number of 
	//					times should be in the game
	// Parameters:
	//		ch - the letter to add to pieces
	//		num - the number of times to add ch to pieces
	public void generateAPiece(Character ch, int num) {
		for(int j = 0; j < num; j++)
			pieces.addElement(ch);
	}

	
	// pileSize - returns the size of pieces
	public int pileSize() {
		return pieces.size();
	}

	
	// getLetter - returns and removes a character at index in pieces
	// Is used when a player takes a piece from the pile and
	// and puts it in their rack.
	// Parameters:
	//		index - index of the character to remove in pieces
	public Character getLetter(int index) {
		return pieces.remove(index);
	}
	
	
	// addLetter - adds a character value to the pieces vector
	// Parameters:
	//		letter - a char value that should be a letter in the alphabet
	public void addLetter(char letter) {
		pieces.addElement(letter);
	}

	
	// getSquare - returns the character at board[x][y]
	// Parameters:
	//		x - first index
	//		y - second index
	public char getSquare(int x, int y) {
		if(x >= 0 && x < getWidth() && y >= 0 && y < getWidth() && board[x][y] != '\0')
			return board[x][y];
		return '\0';
	}
	

	// setSquare - sets the character a board[x][y] to ch
	// Parameters:
	//		x - the first index
	//		y - the second index
	//		ch - the character to set board[x][y] to
	public void setSquare(int x, int y, char ch) {
		if(x >= 0 && x < getWidth() && y >= 0 && y < getWidth() && board[x][y] != '\0')
			board[x][y] = ch;
	}

}