/* ValidWordInfo Class
 * Jolie Nazor and Kristi Yost
 * December 9, 2013
 * Dr. Ravikumar
 * CS 454 - Theory of Computation
 * Semester Project: Scrabble
 * 
 * Represents a valid word that could be place on the board
 * 
 * word - the valid word
 * square - the AnchorSquare the word starts at
 * score - the score of the word
 * tranpose - true if the word is represented as vertical on the board
 *			  false if the word is represented as horizontal on the board
 */

import java.lang.*;
import java.util.*;


public class ValidWordInfo {

	String word;
	AnchorSquare square;
	int score;
	boolean transpose;
	

	// Default Constructor - initializes word to a new string,
	//		square to a new AnchorSquare, score to 0 and transpose to false
	public ValidWordInfo() {
	
		word = new String();
		square = new AnchorSquare();
		score = 0;
		transpose = false;
		
	}


	// Overloaded Constructor - initializes word, square, score and transpose
	//							to the values passed to it
	// Parameters:
	//		myWord - string to assign to word
	//		myAnch - AnchorSquare to assign to square
	//		myScore - integer to assign to score
	//		trans - boolean to assign to transpose
	public ValidWordInfo(String myWord, AnchorSquare myAnch, int myScore, boolean trans) {
	
		word = myWord;
		square = myAnch;
		score = myScore;
		transpose = trans;
		
	}
	

	// getScore - returns score
	public int getScore() {
		return score;
	}


	// getAnchor - returns square
	public AnchorSquare getAnchor() {
		return square;
	}
	

	// getWord - returns word
	public String getWord() {
		return word;
	}


	// getTranspose - returns transpose
	public boolean getTranspose() {
	
		return transpose;
	}

}