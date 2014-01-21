/* Trie class
 * Base class taken from Wikipedia, "Trie" entry
 * 		The code has since been removed from the article.
 * Modified by Kristi Yost and Jolie Nazor
 * December 9, 2013
 * Dr. Ravikumar
 * CS 454 - Theory of Computation
 * Semester Project:  Scrabble
 * 
 * This takes in a dictionary and stores it in a Trie structure.
 * We have added the ability to minimize the Trie into a Vector of Vectors of Nodes,
 * and for this resulting Vector of Vectors of Nodes to be serialized into an output 
 * file.
 *
 * All comments were added by Kristi Yost and Jolie Nazor
 */

import java.util.*;
import java.io.*;

public class Trie {
 
        private Node root = new Node('\0', "");
 
 
 
 
 		// Default Constructor - does nothing
        public Trie() {}
 
 
 
 
 		// Overloaded Constructor - takes in a List of Strings which
 		// are the words from the dictionary and creates the Trie.
 		// Parameters:
 		//		argInitialWords - a list of words from a dictionary.
 		//						  It's size should be > 0.
        public Trie(List<String> argInitialWords) {
                for (String word:argInitialWords) {
                        addWord(word);
                }
        }
 
 
 
 
 		// addWord - adds a single word to the Trie.
 		// Parameters:
 		//		argWord - a single String whose length should be > 0.
        public void addWord(String argWord) {
                addWord(argWord.toCharArray());
        }
 
 
 
 
 		// addWord - adds a single word to the Trie, using a char array
 		// Parameters:
 		//		argWord - a char array whose size should be > 0.
        public void addWord(char[] argWord) {
            Node currentNode = root;
 
            for (int i = 0; i < argWord.length; i++) {
                if (!currentNode.containsChildValue(argWord[i])) {
                    currentNode.addChild(new Node(argWord[i], 
                    					 currentNode.getValue() + argWord[i]));
                }
 
                currentNode = currentNode.getChild(argWord[i]);
            }
 
            currentNode.setIsWord(true);
        }
 
 
 
 
 		// contains Prefix - checks if the passed String to it is a valid word prefix
 		// Parameters:
 		//		argPrefix - a String whose length should be > 0.
        public boolean containsPrefix(String argPrefix) {
                return contains(argPrefix.toCharArray(), false);
        }
 
 
 
 
 		// containsWord - checks if the passed String is a valid word.
 		// Parameters:
 		//		argWord - a String whose length should be > 0.
        public boolean containsWord(String argWord) {
                return contains(argWord.toCharArray(), true);
        }
 
 
 
 
 		// getWord - determines if the passed String is a valid word.
 		// Parameters:
 		//		argString - a String whose length should be > 0.
        public Node getWord(String argString) {
                Node node = getNode(argString.toCharArray());
                return node != null && node.isWord() ? node : null;
        }
 
 
 
 
 		// getPrefix - determines if the passed prefix is a valid word prefix.
 		// Parameters:
 		//		argString - a String whose length should be > 0.
        public Node getPrefix(String argString) {
                return getNode(argString.toCharArray());
        }
        
        
        
        
        // Added by Krisit Yost and Jolie Nazor
        // addDeadNode - adds dead nodes to all Trie nodes for all characters in the
        //				 alphabet for which the node does not have a transition.
        // Parameters:
        //		deadValue - the char value to use to indicate dead nodes
        //		alpha - an array of characters whose size should be > 0.
        //				It should contain all of the valid letters in the alphabet.
        public void addDeadNode(char deadValue, char[] alpha) {
            addDeadNode(deadValue, alpha, root);
        }
           
           
           
           
        // Added by Kristi Yost and Jolie Nazor
        // addDeadNode - adds dead nodes to all Trie nodes for all characters in the
        //				 alphabet for which the node does not have a transition.
        //			     This function is structured to use recursion for traversing 
        //				 the Trie.
        // Parameters:
        //		deadValue - the char value to use to indicate dead nodes
        //		alpha - an array of characters whose size should be > 0.
        //				It should contain all of the valid letters in the alphabet.
        //		startNode - the node to start using when traversing through the Trie.
        public void addDeadNode(char deadValue, char[] alpha, Node startNode) {
            
            Node currentNode = startNode;
            Vector<Character> alreadyHas = new Vector<Character>();
            
            for (int i = 0; i < alpha.length; i++) {
                if (!currentNode.containsChildValue(alpha[i])) {
                    currentNode.addChild(new Node(alpha[i], 
                    					 currentNode.getValue() + deadValue));
                }
                else {
                    alreadyHas.addElement(alpha[i]);
                }
 
            }
            
            for (int j = 0; j < alreadyHas.size(); j++) {
                addDeadNode(deadValue, alpha, currentNode.getChild(alreadyHas.get(j)));
            }
 
        }
        
        
        
        
        // Added by Kristi Yost and Jolie Nazor
        // minimize - minimizes the Trie into a Vector of Vector of Nodes.
        // 			  It follows Hopcroft's DFA Minimization algorithm, as discussed
        //			  in Wikipedia under "DFA Minimization".  The comments inside the
        //			  algorithm are taken from the pseudo code to implement it.
        // Parameters:
        // 		alpha - an array of characters whose size should be > 0.
        //				It should contain all of the valid letters in the alphabet.
        public Vector<Vector <Node>> minimize (char[] alpha) {
            Vector<Node> final_states = new Vector<Node>();
            Vector<Node> nonfinal_states = new Vector<Node>();
            
            final_states = getNodeSet(alpha, root, true);
            nonfinal_states = getNodeSet(alpha, root, false);
            
            Vector<Vector <Node>> P = new Vector<Vector <Node>>();
            Vector<Vector <Node>> W = new Vector<Vector <Node>>();
            
            P.addElement(final_states);
            P.addElement(nonfinal_states);
            W.addElement(final_states);
            
            while (!W.isEmpty()) {
                Vector<Node> setA = W.remove(0);   
                
                for(int c = 0; c < alpha.length; c++) {                	
                
                	// let X be the set of states for which a transition on c 
                	// leads to a state in A
                    Vector<Node> setX = transitionTo(alpha[c], setA, root, alpha);
                    
                    // for each set Y in P for which X ∩ Y is nonempty do
                    for(int k = 0; k < P.size(); k++) {
                    
                        Vector<Node> setY = P.get(k);
                        Vector<Node> intersect = new Vector<Node>();
                        Vector<Node> not_intersect = new Vector<Node>();
                        
                        intersect = getIntersect(setX, setY, alpha);
                        not_intersect = notIntersect(setX, setY, alpha);
                        
                        if(!intersect.isEmpty()) {
                        
                            //replace Y in P by the two sets X ∩ Y and Y \ X
                            P.remove(setY);
                            P.addElement(intersect);
                            
                            if(!not_intersect.isEmpty())
                            	P.addElement(not_intersect);
                            
                            //replace Y in W by the same two sets
                            
                            if(W.contains(setY)) {
                            
                                W.remove(setY);
                            	W.addElement(intersect);
                            	
                            	if(!not_intersect.isEmpty())
                            		W.addElement(not_intersect);
                            }
                            else {
                            
                            	if(intersect.size() <= not_intersect.size()) {
                            		W.addElement(intersect);
                            	}
                            	else if (!not_intersect.isEmpty()){
                            		W.addElement(not_intersect);
                            	}
                            }
                        } 
                    }  
                } 
            }
                        
            return P;
        }
        
        
        
        
        // Added by Kristi Yost and Jolie Nazor
        // getNodeSet - returns the set of all final states, or the set of all non-final
        //				states, based on the value passed to "type".  Uses recursion
        //			    for traversing the Trie.
        // Parameters:
        //		alpha - an array of characters whose size should be > 0.
        //				It should contain all of the valid letters in the alphabet.
        //		start - the node to use during the current iteration of the function.
        //		type  - "true" - returns the set of all final states.
        //				"false - returns the set of all non-final states.
        Vector<Node> getNodeSet(char[] alpha, Node start, Boolean type) {
            Node currentNode = start;
            Vector<Node> states = new Vector<Node>();
            Vector<Character> transitions = new Vector<Character>();
            
            for(int i = 0; i < alpha.length; i++) {
                if(currentNode.containsChildValue(alpha[i])) {
                    transitions.addElement(alpha[i]);
                }
            }
            
            for(int j = 0; j < transitions.size(); j++)
               states.addAll(getNodeSet(alpha, 
               				 currentNode.getChild(transitions.get(j)), type));
               
            if(currentNode.isWord() == type) {
                states.addElement(currentNode);
            }
            
            return states;
        }
        
        
        
        // Added by Kristi Yost and Jolie Nazor
        // printVector - prints the passed Vector and String message.  This function
        //				 was used for debugging purposes.
        // Parameters:
     	// 		mySet - a vector of Trie Nodes whose size should be > 0.
     	//		message - a String to print that labels the vector being printed.
        void printVector(Vector<Node> mySet, String message) {
        	Node temp;
        	System.out.println(message);
        	
            for(int i = 0; i < mySet.size(); i++) {
            	temp = mySet.get(i);
            	System.out.println("char: " + temp.getChar() + 
            					   "   isWord: " + temp.isWord()
            					   + "   value: " + temp.getValue());
            }
            
            System.out.println();
        }        
        
        
        
        
        // Added by Kristi Yost and Jolie Nazor
        // transitionTo - returns a Vector of Trie Nodes for which a transition on the 
        // 				  passed character value leads to a state in the Vector of Trie
        //				  Nodes in targetNode.  This function is structured to use 
        //				  recursion for traversing the Trie.
        // Parameters:
        //		value - a char value on which to search for transitions.  It should be 
        //				a char value in the alphabet.
        //		targetNode - a Vector of Trie Nodes for which to search for transitions
        //					 to these nodes.  The size of this Vector should be > 0.
        //		startNode - the node to use during the current iteration of the function.
        //		alpha - an array of characters whose size should be > 0.
        //				It should contain all of the valid letters in the alphabet.
        
        // let X be the set of states for which a transition on c leads to a state in A
        public Vector<Node> transitionTo(char value, Vector<Node> targetNode, 
        								 Node startNode, char[] alpha) {
            
            Node currentNode = startNode;
            Vector<Node> goesToA = new Vector<Node>();
            
            // Traverse Trie
            for(int j = 0; j < alpha.length - 1; j++) {
                if(currentNode.containsChildValue(alpha[j]))
                    goesToA.addAll(transitionTo(value, targetNode, 
                    			   currentNode.getChild(alpha[j]), alpha));
            }
            
            for (int i = 0; i < targetNode.size(); i++) {
                if (currentNode.containsChildValue(value)
                    && currentNode.getChild(value).equals(targetNode.get(i), alpha)) {
                    goesToA.addElement(currentNode);
                }
            }
            
            return goesToA;
        }
        
        
        
        // Added by Kristi Yost and Jolie Nazor
        // getIntersect - returns a Vector of Trie Nodes that contains all of the Trie
        //				  Nodes that are in both setX and setY.
        // Parameters:
        //		setX - a Vector of Trie Nodes whose size should be > 0.
        //		setY - a Vector of Trie Nodes whose size should be > 0.
        //		alpha - an array of characters whose size should be > 0.
        //				It should contain all of the valid letters in the alphabet.
        Vector<Node> getIntersect(Vector<Node> setX, Vector<Node> setY, char[] alpha) {
            Vector<Node> results = new Vector<Node>();
            
            for(int i = 0; i < setX.size(); i++) {
                for(int j = 0; j < setY.size(); j++) {
                    if(setX.get(i).equals(setY.get(j), alpha))
                        results.addElement(setX.get(i));
                }
            }
            
            return results;
        }
        
        
        
        
        // Added by Kristi Yost and Jolie Nazor
        // notIntersect - returns a Vector of Trie Nodes that contains all of the Trie
        //				  Nodes that are NOT in both setX and setY.
        // Parameters:
        //		setX - a Vector of Trie Nodes whose size should be > 0.
        //		setY - a Vector of Trie Nodes whose size should be > 0.
        //		alpha - an array of characters whose size should be > 0.
        //				It should contain all of the valid letters in the alphabet.
        Vector<Node> notIntersect(Vector<Node> setX, Vector<Node> setY, char[] alpha) {
            Vector<Node> results = new Vector<Node>();
            Boolean inside = false;
                        
            for(int i = 0; i < setY.size(); i++) {
                for(int j = 0; j < setX.size(); j++) {
                    if(setY.get(i).equals(setX.get(j), alpha))
                        inside = true;
                }
                if(!inside)
                    results.addElement(setY.get(i));
                inside = false;
            }
            
            return results;
        }
        
        
        
        
        // Added by Kristi Yost and Jolie Nazor
        // sizeOf - returns the size of the Trie.
        // Parameters:
        //		alpha - an array of characters whose size should be > 0.
        //				It should contain all of the valid letters in the alphabet.
        public int sizeOf(char[] alpha){
            return sizeOf(alpha, root);
        }
        
        
        
        
        // Added by Kristi Yost and Jolie Nazor
        // sizeOf - returns the size of the Trie.  This function is structured to use
        //			recursion for traversing the Trie.
        // Parameters:
        //		alpha - an array of characters whose size should be > 0.
        //				It should contain all of the valid letters in the alphabet.
        //		start - the node to use during the current iteration of the function.
        int sizeOf(char[] alpha, Node start) {
            int total = 0;
            
            for(int i = 0; i < alpha.length; i++) {
                if(start.containsChildValue(alpha[i])) {
                    total += sizeOf(alpha, start.getChild(alpha[i]));
                    total++;
                }
            }
            
            return total;
        }
        
        
        
 
 		// toString - returns the value of the Node String data member "value".
        @Override
        public String toString() {
                return root.toString();
        }
 
 
 
 		
 		// contains - returns whether or not the passed char array is stored in the Trie.
 		// Parameters:
 		//		argString - a char array whose size should be > 0.
 		//		argIsWord - "true" - looking for a valid word
 		//				 	"false" - looking for a valid substring in the Trie
        private boolean contains(char[] argString, boolean argIsWord) {
            Node node = getNode(argString);
            return (node != null && node.isWord() && argIsWord) || 
            	     (!argIsWord && node != null);
        }
 
 
 
 
 		// getNode - returns the Trie Node the Trie Node associated with the last 
 		//			 character value in the passed char array.  Returns NULL if 
 		//			 there is not a corresponding transition for one of the letters
 		//			 in the passed char array.
 		// Parameters:
 		//		argString - a char array whose length should be > 0.
        private Node getNode(char[] argString) {
                Node currentNode = root;
 
                for (int i = 0; i < argString.length && currentNode != null; i++) {
                        currentNode = currentNode.getChild(argString[i]);
 
                        if (currentNode == null) {
                                return null;
                        }
                }
 
                return currentNode;
        }
        
        
        
        
        // Written by Kristi Yost and Jolie Nazor
        //		Creates a Trie and populates it with words from a hard coded dictionary
        //		file.  Then it minimizes the Trie and serializes the minimized Trie to 
        //		file.  Lastly it tests the serialized file by opening the serialized file,
        //		storing it in a new Vector of Vectors of Trie Nodes, and printing the 
        //		contents to the console window.
        public static void main(String args[]) {
        
            File newFile;
            Scanner myFile;
            String letters;
            Trie mytree = new Trie();
            int total = 0;
            Vector<String> dict = new Vector<String>();
            char[] alphabet = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h',
                                'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p',
                                'q', 'r', 's', 't', 'u', 'v', 'w', 'x',
                                'y', 'z'};
                
            try {
                newFile = new File("dict_5.txt");
                myFile = new Scanner(newFile);
            
                while(myFile.hasNext()) {
                    letters = myFile.nextLine();                  
                    System.out.println("Word added to Trie: " + letters);
                    mytree.addWord(letters);
                    dict.addElement(letters);
                    total++;
                }
            
            } catch (FileNotFoundException e) {
                System.out.println("File Not Found.");
                System.exit(1);
            }
            
            
            System.out.println("Total words entered: " + total);
            System.out.println("Total Nodes in Trie: " + mytree.sizeOf(alphabet));
            
            /*
            System.out.println("List of words in Trie: ");
            for (String testword:dict) {
                System.out.println("Finding word: " + testword);
                if(mytree.containsWord(testword))
                    System.out.println("Success!");
                else
                    System.out.println("Not found.");
            }*/
            
            //mytree.addWord(" ");
            
            mytree.addDeadNode(' ', alphabet);
            Vector<Vector <Node>> min_tree = mytree.minimize(alphabet);
                
                
            System.out.println("\n\n\n");
            System.out.println("Total Nodes in Min Trie: " + min_tree.size());
            
            Vector<Vector <Node>> new_min = new Vector<Vector <Node>>();
                        
            try {
            	ObjectOutputStream my_out = 
            		new ObjectOutputStream(new FileOutputStream("Min_Trie.out"));
            	my_out.writeObject(min_tree);
            	my_out.close();
            
            	System.out.println("Testing Serialized Min Trie:");
            	ObjectInputStream my_in = 
            		new ObjectInputStream(new FileInputStream("Min_Trie.out"));
            	new_min = (Vector<Vector <Node>>) my_in.readObject();
            } catch (FileNotFoundException e) {
            	System.out.println("File not found, and this doesn't mean the Trie.class file.");
            } catch (IOException f) {
            	System.out.println("Must catch this IOException to make Java happy.");
            	System.out.println(f);
            } catch (ClassNotFoundException g) {
            	System.out.println("Must catch this ClassNotFoundException to make Java happy.");
            }
            
            System.out.println("Size of new_min: " + new_min.size());
            System.out.println("new_min:");
            for(int i = 0; i < new_min.size(); i++) {
            	mytree.printVector(new_min.get(i), "new_min Level: " + i);
            }

        }
}




// Made Serializable by Kristi Yost and Jolie Nazor

class Node implements Serializable {
 
        private Character ch;
        private String value;
        private Map<String, Node> children = new HashMap<String, Node>();
        private boolean isValidWord;
 
 
 
 
 		// Node constructor.
 		// Parameters:
 		//		argChar - a character value to represent the Node
 		//		argValue - a String value to be associated with argChar
        public Node(char argChar, String argValue) {
                ch = argChar;
                value = argValue;
        }
 
 
 
 
 		// addChild - adds a child Node to the Node's children data member.
 		// Parameters:
 		//		argChild - the Node to add to the calling Node's collection of children.
 		//				   It's value should NOT be NULL.
        public boolean addChild(Node argChild) {
                if (children.containsKey(Character.toString(argChild.getChar()))) {
                        return false;
                }
 
                children.put(Character.toString(argChild.getChar()), argChild);
                return true;
        }
        
        
        
 
 		// containsChildValue - returns (true/false) whether or not the calling Node
 		//						has a child associated with the passed char value.
 		// Parameters:
 		//		c - the char value to use as a key into the children to see if the 
 		//			calling Node has a child associated with this passed value.
        public boolean containsChildValue(char c) {
                return children.containsKey(Character.toString(c));
        }
 
 
 
 
 		// getValue - returns the String value of the data member "value".
        public String getValue() {
                return value.toString();
        }
 
 
 
 
 		// getChar - returns the Character value of the data member "ch".
        public char getChar() {
                return ch;
        }
 
 
 
 
 		// getChild - returns the child Node of the calling Node associated with the 
 		// 			  passed Character value.
        public Node getChild(char c) {
                return children.get(Character.toString(c));
        }
 
 
 
 
 		// isWord - returns the value of the data member "isValidWord".
        public boolean isWord() {
                return isValidWord;
        }
 
 
 
 
 		// setIsWord - sets the value of the data member "isValidWord" to the value 
 		//			   of the passed value.
 		// Parameters:
 		//		argIsWord - a boolean value representing whether or not the calling Node
 		//					is a final Node/accepting Node/last letter in a valid word.
        public void setIsWord(boolean argIsWord) {
                isValidWord = argIsWord;
 
        }
 
 
 
 
 		// toString - returns the value of the data member "value", which is a 
 		//			  String value.
        public String toString() {
                return value;
        }
        
        
        
        
        // getChildren - returns the value of the calling Node's data member "children".
        public Map<String, Node> getChildren() {
        	return children;
        }
        
        
        
        
        // Added by Kristi Yost and Jolie Nazor
        // equals - returns (true/false) whether or not the calling Node and passed Node
        //			are equal.
        // Parameters:
        //		right - Node to be compared with, which should not == NULL
        //		alpha - an array of characters whose size should be > 0.
        //				It should contain all of the valid letters in the alphabet.
        //				It is used to check the equality of the children maps.
        public Boolean equals(Node right, char[] alpha) {

  			Boolean check = true;
  			if (ch == right.ch && (isValidWord == right.isValidWord)
  				&& value.equals(right.value)) {
  				for (int i =0; i<alpha.length; i++) {
  					if (getChild(alpha[i]) != right.getChild(alpha[i])) {
  						check = false;
  					}
   
  				}
  				if (check) {
  					return true;
  				}
  				return false;
  			}
  			return false;

  		}
  		
  		
  		
  		
  		// Added by Kristi Yost and Jolie Nazor
  		// writeObject - used in the Serialization writing process of the Trie.
  		// Parameters:
  		//		output - the ObjectOutputStream object to write the Node to.
  		public void writeObject(ObjectOutputStream output) throws IOException {
  			output.defaultWriteObject();
  			output.writeChar(ch);
  			output.writeChars(value);
  			try {
  				output.writeObject(children);
  			} catch (Exception e) {
  				System.out.println("Serializing the children did not work.  :(");
  				System.out.println(e);
  			}
  			output.writeBoolean(isValidWord);
  		}
  		
  		
  		
  		
  		// Added by Kristi Yost and Jolie Nazor
  		// readObject - used in the Serialization reading process of the Trie.
  		// Parameters:
  		//		input - the ObjectInputStream object to read the Node from.
  		public void readObject(ObjectInputStream input) 
  											throws ClassNotFoundException, IOException {
  			input.defaultReadObject();
  			ch = (char) input.readObject();
  			value = (String) input.readObject();
  			try {
  				children = (HashMap<String, Node>) input.readObject();
  			} catch (Exception e) {
  				System.out.println("De-Serializing the children did not work.  :(");
  				System.out.println(e);
  			}
  			isValidWord = (Boolean) input.readObject();
  		}
 
}