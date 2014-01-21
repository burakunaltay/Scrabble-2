/* Player Class, AnchorSquare Class, and PlaceLetterInfo Class
 * Jolie Nazor and Kristi Yost
 * December 9, 2013
 * Dr. Ravikumar
 * CS 454 - Theory of Computation
 * Semester Project: Scrabble
 *
 * Player Class:  implements everything needed to play a game of Scrabble.  Creating
 *				  more instances of this class in the same program only adds more players
 *				  to the game.  All players will share the same game board and set of
 *				  pieces.
 *
 * AnchorSquare Class:  holds the x and y coordinates for a square on the Scrabble board.
 *
 * PlaceLetterInfo Class:  holds the letter and anchor square value for a letter that is 
 * 						   to be placed on the board.
 */




import java.util.Random;
import java.util.*;
import java.lang.*;
import java.io.*;





public class Player {

	final static int INFINITY = 10;

	
	static ScrabbleBoard board;
	
	int numPieces = 7;			// maximum number of tiles to hold in the rack
	int tempWordTotal;			// holds score of a word until it is deemed a valid word
	int preventInfinity;		// prevents infinite loops to occur from final_word_check
	int preventChkbrdInfinity;	// prevents infinite loops to occur from checkBoard 
	int largest_score;			// holds score of largest scoring word
	int totalScore;				// holds player's total score
	
	static DAWG playerDAWG;		// minimized DFA that holds dictionary of words -
								// directed acyclic word-graph
								
	String largest_score_word;	// the found word with the largest score on the board
	
	AnchorSquare largestAnchor;	// the anchor square to start placing the tiles for the
								// largest scoring word
	
	static boolean first_turn = false;	// denotes if the turn is the first turn of game
	boolean transposed;					// denotes if board is currently tranposed
	boolean didTakePieces;				// denotes if the player took pieces from pile
										// at end of current turn
	
	
	Vector<Character> rack;				// rack to contain tiles for the player to use
	Vector<AnchorSquare> anchSquares;	// all squares where words can start
	Vector<AnchorSquare> tempSquares;	// 

	Vector<AnchorSquare> dontUseSquares;// squares to not look at again on current turn
	Vector<AnchorSquare> dontUseSqs2;	// squares to not look at again on current turn
	Vector<String> dontUseStrs;			// words to not look at again on current turn
	Vector<ValidWordInfo> otherWords;	// holds word info for all found words for 
										// backtracking purposes
	
	
	
	// Player Constructor - initializes all of the data members and creates the DAWG
	// 					    using the DeserialDAWG class.
	public Player() {
		rack = new Vector<Character>();
		board = new ScrabbleBoard();
		
		for(int k = 0; k < 7; k++) {
			rack.addElement(getPiece());	
		}
		
		dontUseSquares = new Vector<AnchorSquare>();
		dontUseSqs2 = new Vector<AnchorSquare>();
		dontUseStrs = new Vector<String>();
		otherWords = new Vector<ValidWordInfo>();
		
		largest_score = -1;
		tempWordTotal = 0;
		preventInfinity = 0;
		preventChkbrdInfinity = 0;
		totalScore = 0;
		
		transposed = false;
		didTakePieces = true;

		largest_score_word = new String();
		anchSquares = new Vector<AnchorSquare>();
		tempSquares = new Vector<AnchorSquare>();
		largestAnchor = new AnchorSquare();
		
		playerDAWG = new DAWG(new Vector<Vector <Node>>());
		playerDAWG = (new DeserialDAWG()).getDAWG();  //deserialize DAWG
		
	}
	
	
	
	
	// getPiece - returns a random letter tile from the ScrabbleBoard class.
	public Character getPiece() {
	
		Random generator = new Random();
		
		int index = generator.nextInt(board.pileSize());
		Character piece = board.getLetter(index);
		return piece;
	}
	
	
	
	
	// getRackSize - returns the size of the player's rack/how many tiles are in it.
	public int getRackSize() {
		return rack.size();
	}
	
	
	
	
	// getRackPiece - returns the tile on the player's rack at the passed index value.
	// Parameters:
	//		index - an integer value to index into the rack vector.
	//				0 <= index < numPieces
	public Character getRackPiece(int index) {
		return rack.get(index);
	}
	
	
	
	
	// removeRackPiece - remove the tile from the rack at the passed index value.
	// Parameters:
	//		index - index value of element in the rack vector to remove.
	//				0 <= index < rack.size()
	public void removeRackPiece(int index) {
		rack.remove(index);
	}
	
	
	
	
	// removeRackPiece - remove the tile from the rack based on the passed Character value
	// Parameters:
	//		ch - Character value that must be a character in the alphabet.
	public void removeRackPiece(Character ch) {
		rack.remove(ch);
	}




	// findLimit - returns the number of spaces to the left that a word can extend, based
	//			   on the passed Anchor Square.
	// Parameters:
	//		aSquare - a valid Anchor Square on the board.
	public int findLimit(AnchorSquare aSquare) {
		int limit = 0;
		int x = aSquare.getX();
		int y = aSquare.getY() - 1;
		
		while (board.getSquare(x,y) == ' '){
			limit++;
			y = y - 1;
			
		}
		
		return limit;
	}
	
	
	
	
	// LeftPart - finds a valid word to place on the board based on the tiles in the 
	//			  rack and on the board.  This function is structured to use recursion
	//			  to aid in extending the word to the left.
	// Parameters:
	//		argString - a String representing the word.  When this function is initially
	//					called, the passed should be an empty String.  This String is
	//					added to with each recursive call to the function.
	//
	//		dNode - a DAWG Node.  When this function is initially called, the root node
	//				of the DAWG is passed.
	//
	//		limit - the number of squares to the left of the anchor square that the found 
	//				word can extend
	//
	//		aSquare - the anchor square on the board to look for words.
	//
	//		start - the anchor square on the board to look for words.  The value of this
	//				anchor square changes with each recursive call to the function.
	//
	//		trans - "true" - the board is transposed.
	//				"false" - the board is not transposed.
	public void LeftPart(String argString, DAWG_Node dNode, int limit, 
						 AnchorSquare aSquare, AnchorSquare start, boolean trans) {
						 
		ExtendRight(argString, dNode, aSquare, aSquare, start, trans);
		DAWG_Node new_node = new DAWG_Node(new Vector<Node>());
		AnchorSquare next_square = new AnchorSquare();
		
		if(limit > 0 ) {
		
			Set<Character> childNodes = dNode.getChildren().keySet();
			
			for(Character one : childNodes) {
			
				if(rack.contains(one)) {
				
					rack.remove(one);
					new_node = dNode.getChild(one);
					start.setX(start.getX());
					start.setY(start.getY() - 1);
					tempWordTotal = 0;
					LeftPart((one + argString), new_node, limit-1, aSquare, start, trans);
					rack.addElement(one);
				}
			}
		}
	}
	

	
	
	// ExtendRight - extends the word to the right using the tiles on the board and the
	//				 tiles on the rack.  This function is structured to use recursion
	//				 in the process of extending the word to the right.
	// Parameters:
	//		argString - a String representing the word.  When this function is initially
	//					called, the passed should be an empty String.  This String is
	//					added to with each recursive call to the function.
	//
	//		dNode - a DAWG Node.  When this function is initially called, the root node
	//				of the DAWG is passed.
	//
	//		aSquare - the anchor square on the board to look for words.
	//		
	//		newSquare - the anchor square on the board to look for words.  The value of
	//				    this anchor square changes with each recursive call to the 
	//					function.
	//
	//		start - the anchor square on the board to look for words.
	//
	//		trans - "true" - the board is transposed.
	//				"false" - the board is not transposed.
	public void ExtendRight(String argString, DAWG_Node dNode, AnchorSquare aSquare, 
						    AnchorSquare newSquare, AnchorSquare start, boolean trans) {
	
		DAWG_Node new_node = new DAWG_Node(new Vector<Node>());
		AnchorSquare next_square = new AnchorSquare();
	
		if(board.getSquare(newSquare.getX(), newSquare.getY()) == ' ') {
		
			if(playerDAWG.containsWord(argString) && dNode.isWord()) {
			
				if( ((numPieces - rack.size()) < argString.length()) 
					&& (rack.size() < numPieces) && (argString.length() != 1)) {
					
					compute_cross_sums(argString, start.getX(), 
									   start.getY(), true, trans);
				}
				else if (first_turn) {
					compute_cross_sums(argString, start.getX(), 
									   start.getY(), true, trans);	
				}
			}
			
			Set<Character> childNodes = dNode.getChildren().keySet();
						
			for(Character one : childNodes) {

				if(rack.contains(one) && cross_checks(one, newSquare, trans)) {
				
					rack.remove(one);
					new_node = dNode.getChild(one);
					next_square = new AnchorSquare(newSquare.getX(), 
												   newSquare.getY() + 1);
												   
					ExtendRight(argString + one, new_node, aSquare, 
								next_square, start, trans);
					rack.addElement(one);
				}
			}
		}
		else {
			Character letter = board.getSquare(newSquare.getX(), newSquare.getY());
			
			if(dNode.getChild(letter) != null) {
				next_square = new AnchorSquare(newSquare.getX(), newSquare.getY() + 1);
				ExtendRight(argString + letter, dNode.getChild(letter), aSquare, 
							next_square, start, trans);
			}		
		}
	}
	
	
	
		
	// cross_checks - determines if all of the letters to the left, right, up, and down 
	//				  of the passed character value and anchor square are valid words.
	// Parameters:
	//		ch - a character from the word that is being generated to be placed on the
	//			 board.
	//
	//		aSquare - the square on the board where the letter will be placed.
	//
	//		trans - "true" - the board has been transposed
	//				"false" - the board has not been transposed.
	public boolean cross_checks(Character ch, AnchorSquare aSquare, boolean trans) {
	
		int m = aSquare.getX();
		int n = aSquare.getY();
		int r = aSquare.getX();
		int s = aSquare.getY();
		String temp = new String();
		
		
		//Going UP
		temp = ch.toString();
		if(board.getSquare(r - 1, s) != ' ' && board.getSquare(r - 1, s) != '\0') {
			while(board.getSquare(r - 1, s) != ' ' && board.getSquare(r - 1, s) != '\0') {
				temp = board.getSquare(r - 1, s) + temp;
				r--;
			}
			
			if(!playerDAWG.containsWord(temp))
				return false;
		}
	
		//Going Down
		m = aSquare.getX();
		n = aSquare.getY();
		if(board.getSquare(m + 1, n) != ' ' && board.getSquare(m + 1, n) != '\0') {
			while(board.getSquare(m + 1, n) != ' ' && board.getSquare(m + 1, n) != '\0') {
				temp = temp + board.getSquare(m + 1, n);
				m++;
			}

			if(!playerDAWG.containsWord(temp))
				return false;
		}
		
		
		if( temp.length() > 1 && compute_cross_sums(temp, s, r, false, trans) == -1)
			return false;
	
		//Going Left
		r = aSquare.getX();
		s = aSquare.getY();
		temp = ch.toString();
		if(board.getSquare(r, s - 1) != ' ' && board.getSquare(r, s - 1) != '\0') {
			while(board.getSquare(r, s - 1) != ' ' && board.getSquare(r, s - 1) != '\0') {
				temp = board.getSquare(r, s-1) + temp;
				s--;
			}

			if(!playerDAWG.containsWord(temp))
				return false;
		}
		
		//Going Right
		m = aSquare.getX();
		n = aSquare.getY();
		if(board.getSquare(m, n + 1) != ' ' && board.getSquare(m, n + 1) != '\0') {

			while(board.getSquare(m, n + 1) != ' ' && board.getSquare(m, n + 1) != '\0') {
				temp = temp + board.getSquare(m, n + 1);
				n++;
			}

			if(!playerDAWG.containsWord(temp))
				return false;

		}
		
		if( temp.length() > 1 && compute_cross_sums(temp, r, s, false, trans) == -1)
			return false;
		
		return true;
	}
	
	
	
	
	// final_cross_checks - determines if all of the letters to the left, right, up, and 
	//				  		down of the passed character value and anchor square are 
	//						valid words.  This version does not call 
	//						compute_cross_sums(). This function is used during a last
	//						check to make sure that the found largest word can be
	//						placed starting at the anchor square and not violate any
	//						Scrabble rules.
	// Parameters:
	//		ch - a character from the word that is being generated to be placed on the
	//			 board.
	//
	//		aSquare - the square on the board where the letter will be placed.

	public boolean final_cross_checks(Character ch, AnchorSquare aSquare) {
	
		int m = aSquare.getX();
		int n = aSquare.getY();
		String temp = new String();
		
		
		//Going UP
		temp = ch.toString();
		if(board.getSquare(m - 1, n) != ' ' && board.getSquare(m - 1, n) != '\0') {
		
			while(board.getSquare(m - 1, n) != ' ' && board.getSquare(m - 1, n) != '\0') {
				temp = board.getSquare(m - 1, n) + temp;
				m--;
			}
			
			if(!playerDAWG.containsWord(temp))
				return false;
		}
	
		//Going Down
		m = aSquare.getX();
		n = aSquare.getY();
		if(board.getSquare(m + 1, n) != ' ' && board.getSquare(m + 1, n) != '\0') {

			while(board.getSquare(m + 1, n) != ' ' && board.getSquare(m + 1, n) != '\0') {
				temp = temp + board.getSquare(m + 1, n);
				m++;
			}

			if(!playerDAWG.containsWord(temp))
				return false;
		}

		//Going Left
		m = aSquare.getX();
		n = aSquare.getY();
		temp = ch.toString();
		if(board.getSquare(m, n - 1) != ' ' && board.getSquare(m, n - 1) != '\0') {

			while(board.getSquare(m, n - 1) != ' ' && board.getSquare(m, n - 1) != '\0') {
				temp = board.getSquare(m, n-1) + temp;
				n--;
			}

			if(!playerDAWG.containsWord(temp))
				return false;

		}
		
		//Going Right
		m = aSquare.getX();
		n = aSquare.getY();
		if(board.getSquare(m, n + 1) != ' ' && board.getSquare(m, n + 1) != '\0') {

			while(board.getSquare(m, n + 1) != ' ' && board.getSquare(m, n + 1) != '\0') {
				temp = temp + board.getSquare(m, n + 1);
				n++;
			}

			if(!playerDAWG.containsWord(temp))
				return false;

		}
		return true;
	}
	

	
	
	// compute_cross_sums - computes the score of a word, determines if it is
	//						the largest scoring word, and stores it if it is the
	//						largest scoring word at the moment.  It's also used
	//						for computing the score of any cross words to the word
	//						being generated and stores it for use in the finalized word
	//						score.
	// Parameters:
	//		wordArg - the string to compute the score for.
	//
	//		x - the x coordinate on the board where the word will start
	//
	//		y - the y coordinate on the board where the word will start
	//
	//		lastcheck - "true" - computing the score for the finalized word.
	//					"false" - computing the score for a cross word
	public int compute_cross_sums(String wordArg, int x, int y, 
								  boolean lastCheck, boolean trans) {
								  
		int result = 0;
		int word_mult = 0;
		int letter_mult = 0;
		char[] word = wordArg.toCharArray();
		ValidWordInfo tempInfo = new ValidWordInfo();
		AnchorSquare tempAnch = new AnchorSquare();
		AnchorSquare temp = new AnchorSquare();
		
		
		temp = checkAnchor(wordArg, new AnchorSquare(x, y));
		final int origx = x;
		final int origy = temp.getY();
		
		//System.out.println("\nalreadyOnBoard: " + !alreadyOnBoard(wordArg, 
		//												new AnchorSquare(origx, origy)));
		//System.out.println("Word: " + wordArg);
		
		
		for(int j = 0; j < wordArg.length(); j++) {
		
			if(x < board.getWidth() && (j + y) < board.getWidth() 
			   && !alreadyOnBoard(wordArg, new AnchorSquare(origx, origy))) {
			   
				word_mult += board.getWordMultiplier(x, j + y);
				
				//System.out.println("\nLetter: " + word[j] + "    Word Mult: " 
				//								+ board.getWordMultiplier(x, j + y));
				
				if(board.getLetterMultiplier(x, j + y) != 0) {
				
					letter_mult += (board.getLetterMultiplier(x, j + y) * 
															board.getLetterVal(word[j]));
					//System.out.println("\nLetter: " + word[j] + "    Letter Mult: " 
					//							+ board.getLetterMultiplier(x, j + y));
				} else {
				
					try {
						letter_mult += board.getLetterVal(word[j]);
					} catch(Exception e) {
						System.out.println("Compute_Cross_Sums() Exception: " + e);
					}
				}
			}
			else {
				return -1;
			}
		}
		
		if (word_mult != 0)
			result = letter_mult * word_mult;
		else
			result = letter_mult;
/*		
		System.out.println("\nResult:");
		System.out.println("Score: " + result);
		System.out.println("Word: " + wordArg);
		System.out.println("Anchor X: " + origx + "   Y: " + origy);
*/		
		if(!lastCheck || first_turn || tempWordTotal <= 0)
			tempWordTotal += result;
		
		if(lastCheck) {
		
			tempAnch = new AnchorSquare(origx, origy);
				
			tempInfo = new ValidWordInfo(wordArg, tempAnch, tempWordTotal, trans);
			otherWords.addElement(tempInfo);
/*			
			System.out.println("\nTempWordTotal: ");
			System.out.println("Score: " + tempWordTotal);
			System.out.println("Word: " + wordArg);
			System.out.println("Anchor X: " + origx + "   Y: " + origy);
*/		
			if(tempWordTotal > largest_score && wordArg.length() > 1) {
			
				largest_score = tempWordTotal;
				largest_score_word = wordArg;
				largestAnchor.setX(origx);
				largestAnchor.setY(origy);
			}
			tempWordTotal = 0;
		}
		
		return result;
	}
	
	
	
	
	// determineAnchorSquare - finds all anchor squares on the board and stores them 
	//							inside the vector data member anchSquares.
	public void determineAnchorSquare() {
	
		AnchorSquare tempSquare = new AnchorSquare();
		anchSquares = new Vector<AnchorSquare>();
		
		if(anchSquares.size() > 0){
			System.out.println(anchSquares.size());
			anchSquares.clear();
		}
		
		if(board.getSquare(8, 8) == ' ') {
		
			tempSquare.setX(8);
			tempSquare.setY(8);
			anchSquares.addElement(tempSquare);
			
		} else {
		
			for(int i = 0; i < board.getWidth(); i++) {
			
				for(int j = 0; j < board.getWidth(); j++) {
				
					if(board.getSquare(i, j) != ' ' && board.getSquare(i, j) != '\0') {
					
						if(j > 1 && board.getSquare(i, j - 1) == ' ') {
						
							tempSquare.setX(i);
							tempSquare.setY(j - 1);
							anchSquares.addElement(tempSquare);
							tempSquare = new AnchorSquare();
						}
					}
				}
			}
		}
	}
	
	
	
	
	// generateMove - generates the next largest scoring word and places it on the board.
	public void generateMove() {
	
		if(preventInfinity >= INFINITY) {
			return;
		}
		
		if(preventChkbrdInfinity >= INFINITY) {
			return;
		}
	
		determineAnchorSquare();
		tempSquares = new Vector<AnchorSquare>(anchSquares);
		int size = anchSquares.size();
		String myString = new String();
		
		String tempLargWord = new String();
		int tempLargScore = -1;
		AnchorSquare tempAnch = new AnchorSquare();
		AnchorSquare tempSquare = new AnchorSquare();
		
		String tempLargWord2 = new String();
		int tempLargScore2;
		AnchorSquare tempAnch2 = new AnchorSquare();
		
		if(dontUseSquares.size() > 0)
			for(int q = 0; q < dontUseSquares.size(); q++)
				anchSquares.remove(dontUseSquares.get(q));
		

		int limit;
		AnchorSquare temp_7 = new AnchorSquare();
		
		for(int m = 0; m < size; m++) {
		
			limit = findLimit(anchSquares.get(m));
			temp_7.setX(anchSquares.get(m).getX());
			temp_7.setY(anchSquares.get(m).getY());
			tempWordTotal = 0;
			
			if(first_turn)
				limit = 0;
			
			LeftPart(myString, playerDAWG.getRoot(), limit, temp_7, temp_7, false);
		}
		
		if(!first_turn) {
		
			for(int n = 0; n < size; n++) {
			
				limit = findLimit(anchSquares.get(n));
				temp_7.setX(anchSquares.get(n).getX());
				temp_7.setY(anchSquares.get(n).getY() + 1);
				tempWordTotal = 0;
			
				if(first_turn)
					limit = 0;
			
				if(board.getSquare(temp_7.getX(), temp_7.getY() + 1) == ' ' 
				   && board.getSquare(temp_7.getX(), temp_7.getY() - 1) == ' ')
					LeftPart(myString, playerDAWG.getRoot(), 
					         limit, temp_7, temp_7, false);
			}
			
		
			tempLargWord = largest_score_word;
			tempLargScore = largest_score;
			tempAnch.setX(largestAnchor.getX());
			tempAnch.setY(largestAnchor.getY());
			
			board.transposeBoard();
		
			determineAnchorSquare();
			size = anchSquares.size();
			myString = new String();
			
			if(dontUseSquares.size() > 0)
				for(int q = 0; q < dontUseSquares.size(); q++)
					anchSquares.remove(dontUseSquares.get(q));
			
			
			for(int m = 0; m < size; m++) {
			
				limit = findLimit(anchSquares.get(m));
				temp_7.setX(anchSquares.get(m).getX());
				temp_7.setY(anchSquares.get(m).getY());
				tempWordTotal = 0;
			
				if(first_turn)
					limit = 0;
			
				LeftPart(myString, playerDAWG.getRoot(), limit, temp_7, temp_7, true);
			}
			
			for(int n = 0; n < size; n++) {
			
				limit = findLimit(anchSquares.get(n));
				temp_7.setX(anchSquares.get(n).getX());
				temp_7.setY(anchSquares.get(n).getY() + 1);
				tempWordTotal = 0;
						
				if(first_turn)
					limit = 0;
					
				if(board.getSquare(temp_7.getX(), temp_7.getY() + 1) == ' ' 
				   && board.getSquare(temp_7.getX(), temp_7.getY() - 1) == ' ')
					LeftPart(myString, playerDAWG.getRoot(), limit, temp_7, temp_7, true);
			}
			
		}
			
		//if(tempLargScore2 < largest_score)
		//	largest_score_word = " " + largest_score_word;
/*
		System.out.println("tempLargScore: " + tempLargScore);
		System.out.println("largest_score: " + largest_score);
		System.out.println("tempLargWord: " + tempLargWord);
		System.out.println("largest_score_word: " + largest_score_word);
		System.out.println("AnchorSquare X: " + largestAnchor.getX());
		System.out.println("AnchorSquare Y: " + largestAnchor.getY());
*/
		if(tempLargScore < largest_score && !first_turn) {
		
			checkAnchor();
			
			if(!checkBoard() && !final_word_check() && !alreadyOnBoard()) {
				placeWord();
				board.transposeBoard();
			}
			else {
				board.transposeBoard();
				placeNextWord();
			}
			
		}
		else {
			board.transposeBoard();
			
			checkAnchor();
			
			if(!checkBoard() && !final_word_check() && !alreadyOnBoard()) {
				placeWord();
			}
			else {
				placeNextWord();
			}
		}
		
		myString = new String();
	}
	
	
	
	
	// printLargestWord - prints the largest word (found so far for that move).
	public void printLargestWord() {
		System.out.println("Largest Word: " + largest_score_word);
	}
	
	
	
	
	// resetLargest - resets all data members used for storing the information for the
	//				  largest word found so far for each move.
	public void resetLargest() {
	
		largest_score = -1;
		largest_score_word = "";
		largestAnchor.setX(0);
		largestAnchor.setY(0);
	}
	
	
	
	
	// printRack - prints the tiles currently on the rack.
	public void printRack() {
	
		System.out.println("Rack: ");
		
		for(int i = 0; i < rack.size(); i++)
			System.out.print(rack.get(i) + "   ");
			
		System.out.println();
	}
	
	
	
	
	// printASquare - prints the values in all anchor squares in the passed vector.
	// Parameters:
	//		s - a Vector of Anchor Squares whose size should be > 0.
	public void printASquare(Vector<AnchorSquare> s) {
	
		System.out.println("Anchor Squares: ");
		
		for(int i = 0; i < s.size(); i++)
			System.out.println("X: " + s.get(i).getX() + "   Y: " + s.get(i).getY());
			
		System.out.println();
	}
	
	
	
	
	// printOneSquare - prints the values in one anchor square.
	// Parameter:
	//		temp - an Anchor Square
	public void printOneSquare(AnchorSquare temp) {
		System.out.println("A Square: ");
		System.out.println("X: " + temp.getX() + "   Y: " + temp.getY() + "\n");
	}
	
	
	
	
	// checkAnchor - checks to make sure that the anchor square for the largest word
	//				 found so far in the move generation is correct.  If the anchor
	//				 square is not correct, then it is properly corrected.
	public void checkAnchor () {
	
		for(int j = 0; j < largest_score_word.length(); j++) {
		
			if(board.getSquare(largestAnchor.getX(), largestAnchor.getY() + j) != ' ' 
			   && board.getSquare(largestAnchor.getX(), 
			                      largestAnchor.getY() + j) != '\0') {
			                      
				if(largest_score_word.charAt(j) != board.getSquare(largestAnchor.getX(), 
				                                             largestAnchor.getY() + j)) {
				                                             
					largestAnchor.setY(largestAnchor.getY() - 1);	
					j = largest_score_word.length();
				}
			}
		}
	
	}
	
	
	

	// checkAnchor - checks to make sure that the anchor square for the largest word
	//				 found so far in the move generation is correct.  Returns the
	//				 adjusted anchor square.
	// Parameters:
	//		wordArg - the word String to be placed on the board.
	//		anch - the anchor square where the placing of the word should begin.
	public AnchorSquare checkAnchor (String wordArg, AnchorSquare anch) {
	
		for(int j = 0; j < wordArg.length(); j++) {
		
			if(board.getSquare(anch.getX(), anch.getY() + j) != ' ' 
			   && board.getSquare(anch.getX(), anch.getY() + j) != '\0') {
			   
				if(wordArg.charAt(j) != board.getSquare(anch.getX(), anch.getY() + j)) {
					anch.setY(anch.getY() - 1);	
					j = wordArg.length();
				}
			}
		}
		
		return anch;
	
	}
	
	
	
	
	// FIX ME!!!!  NOW!!!!!
	// checkBoard - checks the board to make sure that the word to be placed on the board
	//				can be placed there by making sure that no letters in the word
	//				will replace already existing letters on the board that are different
	//				from the letter in the word that would be placed in that same cell.
	public boolean checkBoard() {
	
		boolean check = false;
	
		for(int k = 0; k < largest_score_word.length() && !check; k++) {
		
			if(board.getSquare(largestAnchor.getX(), largestAnchor.getY() + k) != ' ' 
			   && board.getSquare(largestAnchor.getX(), 
			                      largestAnchor.getY() + k) != '\0') {
			                      
				if(largest_score_word.charAt(k) != board.getSquare(largestAnchor.getX(), 
															 largestAnchor.getY() + k)) {
															 
					dontUseSquares.addElement(largestAnchor);
					check = true;
					preventChkbrdInfinity++;
					largest_score = -1;
					largest_score_word = "";
					//System.out.println("Inside checkBoard.");
				}	
			}
		}
	
		return check;	
	
	}
	
	
	
	
	// final_word_check - checks to make sure that the word to be placed on the board
	//					  can still be placed on the board at the anchor square stored
	//					  in the data member "largestAnchor".  This is used after calling
	//					  checkAnchor(), which may adjust the value of "largestAnchor".
	public boolean final_word_check() {
	
		boolean check = false;
		
		if(dontUseSqs2.contains(largestAnchor) 
		   && dontUseStrs.contains(largest_score_word)) {
		   
			preventInfinity++;
			//System.out.println("Inside final_word_check.");
			return true;
		}
	
		AnchorSquare temp = new AnchorSquare(largestAnchor.getX(), largestAnchor.getY());
	
		for(int q = 0; q < largest_score_word.length() && !check; q++) {
		
			temp.setY(largestAnchor.getY() + q);
			//System.out.println("final_Check char: " + largest_score_word.charAt(q));
			
			if(board.getSquare(temp.getX(), temp.getY()) == ' ') {
			
				if(!final_cross_checks(largest_score_word.charAt(q), temp)) {
				
					dontUseSqs2.addElement(largestAnchor);
					dontUseStrs.addElement(largest_score_word);
					check = true;
					preventInfinity++;
					//System.out.println("Inside final_word_check.");
				}
			}
		}
	
		return check;
	
	}
	
	
	
	
	// placeWord - places the largest found word on the board, taking tiles from the 
	//			   rack if the tiles are not already on the board.
	public void placeWord() {
		
		for(int i = 0; i < largest_score_word.length(); i++) {
		
			if(board.getSquare(largestAnchor.getX(), largestAnchor.getY() + i) == ' ')
					rack.remove((Character) largest_score_word.charAt(i));
		
			board.setSquare(largestAnchor.getX(), largestAnchor.getY() + i, 
							largest_score_word.charAt(i));
		}

	}
	
	
	
	
	// placeNextWord - places the next word on the board using the stored other word
	//				   options.  This is used if the largest found word cannot be placed
	//				   on the board.  It uses data members of this class.
	public void placeNextWord() {
		boolean trans = false;
		boolean placedWord = false;
		ValidWordInfo temp = new ValidWordInfo();
		
		for(int q = otherWords.size() - 1; q >= 0 && !placedWord; q--) {
			
			temp = findNextLargest();
			
			largest_score = temp.getScore();
			largest_score_word = temp.getWord();
			largestAnchor = temp.getAnchor();
			trans = temp.getTranspose();

			if(trans)
				board.transposeBoard();
			
			
			checkAnchor();
			
			if(!checkBoard() && !final_word_check() && !alreadyOnBoard()) {
		
				for(int i = 0; i < largest_score_word.length(); i++) {
		
					if(board.getSquare(largestAnchor.getX(), 
									   largestAnchor.getY() + i) == ' ')
							rack.remove((Character) largest_score_word.charAt(i));
		
					board.setSquare(largestAnchor.getX(), largestAnchor.getY() + i, 
									largest_score_word.charAt(i));
				}
				
				placedWord = true;
			}
			
			if(trans)
				board.transposeBoard();
		}

	}
	
	
	
	
	// alreadyOnBoard - checks to see if the given word is already on the board at the
	//					given anchor square. Checks using the data members of this class.
	public boolean alreadyOnBoard() {
	
		boolean check = true;
	
		for(int y = 0; y < largest_score_word.length(); y++) {
			if(largest_score_word.charAt(y) != board.getSquare(largestAnchor.getX(), 
															largestAnchor.getY() + y)) {
				check = false;	
			}
		}
	
		return check;
	
	}
	
	
	
	
	// alreadyOnBoard - checks to see if the given word is already on the board at the
	//					given anchor square.
	// Parameters:
	//		word - a String containing a valid word to be placed on the board.
	//		aSquare - the anchor square at which to begin placing the word.
	public boolean alreadyOnBoard(String word, AnchorSquare aSquare) {
	
		boolean check = true;
	
		for(int y = 0; y < word.length() && check; y++) {
			if(word.charAt(y) != board.getSquare(aSquare.getX(), aSquare.getY() + y)) {
				check = false;
			}
		}
	
		return check;
	
	}
	
	
	
	
	// findNextLargest - gets the next stored largest word found in during the move
	//					 generation process and places the necessary information into
	//					 the needed data members for placing it on the board.
	public ValidWordInfo findNextLargest() {
		
		ValidWordInfo result = new ValidWordInfo();
		int largest = -1;
		int index = -1;
	
		for(int x = 0; x < otherWords.size(); x++) {
		
			if(otherWords.get(x).getScore() > largest) {
				largest = otherWords.get(x).getScore();
				index = x;
			}
		}
		
		result = otherWords.get(index);
		otherWords.remove(index);
		
		return result;
	
	}
	
	
	
	
	// getBoard - returns the ScrabbleBoard object "board".
	public ScrabbleBoard getBoard() {
		return board;
	}
	
	
	
	
	// pickPieces - picks tile pieces from the pile in ScrabbleBoard and places them
	//		        in the rack.
	public void pickPieces() {
	
		if(rack.size() < 7)
			didTakePieces = true;
		else
			didTakePieces = false;
	
		while(rack.size() < 7 && board.pileSize() > 0)
			rack.addElement(getPiece());
	}
	
	
	
	
	// gotPieces - returns "true" - the player picked up pieces to fill the rack.
	//					   "false" - the player did not pick up pieces to fill the rack.
	//			   This function is used to help determine whether or not the players
	//			   are able to continue playing the game or if there are no more moves
	//			   left for the players to make, effectively ending the game.
	public boolean gotPieces() {
		return didTakePieces;
	}
	
	
	
	
	// getTotalScore - returns the value of the data member "totalScore".
	public int getTotalScore () {
		return totalScore;
	}
	
	
	
	
	// emptyRack - removes the tiles in the rack and places them back in the pile in
	//			   the ScrabbleBoard object "board".
	public void emptyRack () {
		for(int i = rack.size() - 1; i >= 0; i--)
			board.addLetter(rack.remove(i));
	}
	
	
	
	
	// takeTurn - calls the necessary functions in order to complete one
	//			  turn in the game of Scrabble
	public void takeTurn() {
		printRack();
		
		generateMove();
		
		while(rack.size() == 7 && otherWords.size() > 0) {
			largest_score = -1;
			largest_score_word = "";
			placeNextWord();
		}
		
		if(rack.size() == 7) {
			largest_score = -1;
			largest_score_word = "";
		}
		
		System.out.println();
		printLargestWord();
		board.print();
		
		System.out.println();
		printRack();
		
		if(largest_score != -1) 
			totalScore += largest_score;
		else {
			emptyRack();
			System.out.println("\nNo moves were made:  Swapping entire rack.\n");
		}
		
		resetLargest();
		pickPieces();
		
		printRack();
		System.out.println();
		
		preventInfinity = 0;
		preventChkbrdInfinity = 0;
		
		dontUseSquares.clear();
		dontUseSqs2.clear();
		dontUseStrs.clear();
		otherWords.clear();
	}
	
	
	
	
	// plays a game of Scrabble between two computer opponents
	public static void main(String[] args) {
	
		Player player1 = new Player();
		Player player2 = new Player();
		
		Scanner input = new Scanner(System.in);
		String decision = new String();
		System.out.println("Do Another Turn (yes/no)?  ");
		decision = input.next();
		//player1.printRack();
		player1.first_turn = true;
		
		while ( decision.equals("yes") ) {
			player1.takeTurn();
			
			player1.first_turn = false;
			
			player2.takeTurn();
			
			System.out.println("Player1 Score: " + player1.getTotalScore());
			System.out.println("Player2 Score: " + player2.getTotalScore());
			
			if(!player1.gotPieces() && !player2.gotPieces())
				decision = "no";
			else {
				System.out.println("Do Another Turn (yes/no)?  ");
				decision = input.next();
			}
		
		}
		
		System.out.println();
		if(player1.getTotalScore() > player2.getTotalScore())
			System.out.println("Player 1 wins!");
		else if (player1.getTotalScore() < player2.getTotalScore())
			System.out.println("Player 2 wins!");
		else
			System.out.println("It's a tie!");
		System.out.println();
	
	}	

}





// AnchorSquare holds the x and y coordinates for a square on the Scrabble board.
class AnchorSquare {
	int x;
	int y;
	
	
	
	
	// AnchorSquare constructor - the x and y coordinates are both set to 0.
	AnchorSquare () {
		x = y = 0;
	}
	
	
	
	
	// AnchorSquare overloaded constructor - the x and y coordinate values are set
	//				set to those of "new_x" and "new_y", respectively.
	// Parameters:
	//		new_x - an integer value for an x coordinate on the Scrabble board.
	//		new_ y - an integer value for an y coordinate on the Scrabble board.
	AnchorSquare(int new_x, int new_y) {
		x = new_x;
		y = new_y;
	}
	
	
	
	
	// getX = returns the x coordinate value.
	public int getX() {
		return x;
	}
	
	
	
	
	// getY - returns the y coordinate value.
	public int getY() {
		return y;
	}
	
	
	
	
	// setX - sets the value of x to that of "num".
	// Parameters:
	//		num - an integer value for an x coordinate on the Scrabble board.
	public void setX(int num) {
		x = num;
	}
	
	
	
	
	// setY- sets the value of y to that of "num".
	// Parameters:
	//		num - an integer value for a y coordinate on the Scrabble board.
	public void setY(int num) {
		y = num;
	}
	
	
	
	
	// setXY - sets the value of x to that of "num1" and y to "num2".
	// Parameters:
	//		num1 - an integer value for an x coordinate on the Scrabble board.
	//		num2 - an integer value for a y coordinate on the Scrabble board.
	public void setXY(int num1, int num2) {
		x = num1;
		y = num2;
	}
	
}





// PlaceLetterInfo holds the letter and anchor square value for a letter that is to
//		be placed on the board.
class PlaceLetterInfo {

	AnchorSquare square;
	char letter;




	// PlaceLetterInfo constructor
	// Parameters:
	//		a - an anchor square
	//		l - a character.  It should be a character in the alphabet.
	public PlaceLetterInfo (AnchorSquare a, char l) {	
		letter = l;
		square = new AnchorSquare( a.getX(), a.getY() );
	}




	// getChar - returns the character value of the data member "letter".
	public char getChar() {
		return letter;
	}
	
	
	
	
	// getX - returns the x coordinate value of the data member "square".
	public int getX() {
		return square.getX();
	}
	
	
	
	
	// getY - returns the y coordinate value of the data member "square".
	public int getY() {
		return square.getY();
	}

}
