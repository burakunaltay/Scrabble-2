/* DAWG Class and DAWG_Node Class
 * Jolie Nazor and Kristi Yost
 * December 9, 2013
 * Dr. Ravikumar
 * CS 454 - Theory of Computation
 * Semester Project: Scrabble
 * 
 * DAWG Class:
 * Takes in a vector containing vectors of Nodes.
 * Converts the vector to a DAWG and serializes it.
 *
 * DAWG_Node Class:
 * Each sub-vector taken in from the DAWG class is converted to a DAWG_Node.
 */

import java.util.*;
import java.io.*;
import java.lang.*;



public class DAWG implements Serializable {
	DAWG_Node root;                  // DAWG_Node that is the root of the DAWG
	Vector<DAWG_Node> dawg_nodes;    // vector of all the DAWG_Nodes in the DAWG
	
  // Overloaded Constructor - makes a new DAWG_Node for every sub vector
  // and adds it in dawg_nodes
  // Parameters:
  //      arg - a vector containing a vector of Nodes
	DAWG (Vector<Vector<Node>> arg) {

		dawg_nodes = new Vector<DAWG_Node>();
		for(int i = 0; i < arg.size(); i++)
		    dawg_nodes.addElement(new DAWG_Node(arg.get(i)));
	}


  // addNode - goes through each sub-vector and each DAWG_Node in dawg_nodes
  //           to figure how to connect each DAWG_Node in the DAWG
  // Parameters:
  //      arg - a vector containing a vector of Nodes
  public void addNode(Vector<Vector<Node>> arg) {

			boolean start = true;
      for(int i = 0; i < dawg_nodes.size(); i++) {

         DAWG_Node currentDAWG = dawg_nodes.get(i);
         Vector<Node> currentNodes = arg.get(i);
         
         if(currentNodes.get(0).getChar() == '\0')
         		root = currentDAWG;
         
         for(int k = 0; k < currentNodes.size(); k++) {
              Set<String> keys = currentNodes.get(k).getChildren().keySet();
              
              for(int m = 0; m < dawg_nodes.size(); m++) {
                  for(int n = 0; n < dawg_nodes.get(m).getCharSize(); n++) {
                  
                  	Character my_char = new Character(dawg_nodes.get(m).getAChar(n));
                  	String my_string = my_char.toString();
                  
                    if(keys.contains(my_string) && 
                      dawg_nodes.get(m).getValues().get(n)
                        .equals(currentNodes.get(k).getChild(my_char).getValue())){
                    
                    	for(int q = 0; q < dawg_nodes.get(m).getCharacts().size(); q++) {
                    
                    		if(currentDAWG.getChild(dawg_nodes.get(m).getAChar(q)) == null)
                        		currentDAWG.addChild(dawg_nodes.get(m), my_char);
                      }
                    }
                  }
              }
         }
	    }
	}
	
	
	// print - prints each DAWG_Node in dawg_nodes
	public void print() {
		for(int k = 0; k < dawg_nodes.size(); k++)
			if(dawg_nodes.get(k).getValues().size() < 100)
				System.out.println(dawg_nodes.get(k).toString());
	}
	
	
	// printDAWG_Nodes -  prints each DAWG_Node and its children
	public void printDAWG_Nodes() {
		
		for(int k = 0; k < dawg_nodes.size(); k++) {
			DAWG_Node currentNode = dawg_nodes.get(k);
			
			if(currentNode.getValues().size() < 100) {
				System.out.println(currentNode.toString());
				System.out.println("Children:");
				Collection<DAWG_Node> kids = currentNode.getChildren().values();
				for(DAWG_Node one : kids)
					System.out.println(one.toString());
				System.out.println();
			}
		}
	}
	
	
	// conaingsWord - sees if a word can be found in the DAWG
  // Parameters:
  //    argWord - the word to search for in the DAWG
	public boolean containsWord(String argWord) {
		return contains(argWord.toCharArray(), true);
	}
	
	

  // getRoot - returns the DAWG_Node that is the root of the DAWG
	public DAWG_Node getRoot() {
		return root;
	}
	
	
	// contains - looks for a string in the DAWG and sees if it's a valid word
  // Parameters:
  //    argString - a character array holding the string to look for
  //    argIsWord - whether or not the string being looked for is valid
	private boolean contains(char[] argString, boolean argIsWord) {
	
		DAWG_Node node = getDAWGNode(argString);
		return (node != null && node.isWord() && argIsWord) 
            || (!argIsWord && node != null);
	}
	
	
	// getDAWGNode - returns the DAWG_Node where a certain string can be found
  //               or null if the string can't be found
  // Parameters:
  //    argString - character array holding the string to be searched for
	private DAWG_Node getDAWGNode(char[] argString) {
	
		DAWG_Node currentNode = root;
		String tempWord = new String();
		
		for(int i = 0; i < argString.length && currentNode != null; i++) {
		
			tempWord = tempWord + new Character(argString[i]);
			currentNode = currentNode.getChild(argString[i]);
			
			if(currentNode == null || !currentNode.getValues().contains(tempWord)) {
				return null;
			}
		}		
		return currentNode;
	}
	

	// writeObject - serializes a DAWG object
	public void writeObject(ObjectOutputStream output) throws IOException {
    	output.defaultWriteObject();
    	try {
  			output.writeObject(root);
  		} catch (Exception e) {
  			System.out.println("Serializing the root did not work.");
  			System.out.println(e);
  		}
    	try {
  			output.writeObject(dawg_nodes);
  		} catch (Exception e) {
  			System.out.println("Serializing the dawg_nodes did not work.");
  			System.out.println(e);
  		}
    }
    
    
    
    // readObject - de-serializes a DAWG object
    public void readObject(ObjectInputStream input) throws ClassNotFoundException, IOException {
    	input.defaultReadObject();
    	try {
  			root = (DAWG_Node) input.readObject();
  		} catch (Exception e) {
  			System.out.println("De-Serializing the root did not work.");
  			System.out.println(e);
  		}
  		try {
  			dawg_nodes = (Vector<DAWG_Node>) input.readObject();
  		} catch (Exception e) {
  			System.out.println("De-Serializing the dawg_nodes did not work.");
  			System.out.println(e);
  		}
    }
	


  // main - opens serialized Min_Trie and serializes 
  //        the DAWG after it's been created
	public static void main(String args[]) {
		Trie mytree = new Trie();
		Vector<Vector <Node>> new_min = new Vector<Vector <Node>>();
		
		try {            
            System.out.println("Opening Serialized Min Trie:");
            ObjectInputStream my_in = new ObjectInputStream(new FileInputStream("Min_Trie.out"));
            new_min = (Vector<Vector <Node>>) my_in.readObject();
        } catch (FileNotFoundException e) {
           	System.out.println("File not found, and this doesn't mean the Trie.class file.");
        } catch (IOException f) {
          	System.out.println("Must catch this IOException to make Java happy.");
           	System.out.println(f);
        } catch (ClassNotFoundException g) {
           	System.out.println("Must catch this ClassNotFoundException to make Java happy.");
        }
        
        int largest = new_min.get(0).size();
        int index = -1;
        for(int z = 1; z < new_min.size(); z++) {
        	if(largest < new_min.get(z).size()) {
        		largest = new_min.get(z).size();
        		index = z;
        	}
        }
        		
        new_min.remove(index);
        DAWG my_dawg = new DAWG(new_min);
        my_dawg.addNode(new_min);        
        DAWG new_dawg = new DAWG(new Vector<Vector <Node>>());

        try {
            	ObjectOutputStream my_out = new ObjectOutputStream(new FileOutputStream("Dict_DAWG.out"));
            	my_out.writeObject(my_dawg);
            	my_out.close();
            
            	System.out.println("Testing Serialized DAWG:");
            	ObjectInputStream my_in = new ObjectInputStream(new FileInputStream("Dict_DAWG.out"));
            	new_dawg = (DAWG) my_in.readObject();
            } catch (FileNotFoundException e) {
            	System.out.println("File not found, and this doesn't mean the Trie.class file.");
            } catch (IOException f) {
            	System.out.println("Must catch this IOException to make Java happy.");
            	System.out.println(f);
            } catch (ClassNotFoundException g) {
            	System.out.println("Must catch this ClassNotFoundException to make Java happy.");
            }
            
        System.out.println("Deserialized DAWG:");
        new_dawg.printDAWG_Nodes();
	}
}


/* DAWG_Node Class
*  characts - vector of characters that transition to the DAWG_Node
*             from all of its parent DAWG_Nodes
*  values - vector of strings that lead to the DAWG_Node
*  isValidWord - true if the DAWG_Node has valid words, other false
*  children - map that contains all of the child DAWG_Nodes
*/
class DAWG_Node implements Serializable {

  	Vector<Character> characts = new Vector<Character>();
  	Vector<String> values = new Vector<String>();
  	boolean isValidWord;
  	Map<Character, DAWG_Node> children = new HashMap<Character, DAWG_Node>();


    // Overloaded Constructor - converts a vector of Nodes to a DAWG_Node
    // Parameters:
    //    arg - a vector of Nodes to be converted to a DAWG_Node
  	DAWG_Node(Vector<Node> arg) {
  		for(int i = 0; i < arg.size(); i++) {
  			characts.addElement(arg.get(i).getChar());
  			values.addElement(arg.get(i).getValue());
  		}
  		
  		if(arg.size() > 0)
  			isValidWord = arg.get(arg.size() - 1).isWord();
  		else
  			isValidWord = false;
  	}
  	

    // addChild - adds a child to the DAWG_Node
    // Parameters:
    //    argChild - the DAWG_Node to add as a child
    //    argChar - the character that will transition to the new child DAWG_Node
  	public boolean addChild(DAWG_Node argChild, char argChar) {	    
  	    
  	    if(children.containsKey(argChar)) {
  	    	return false;
  	    }
  	    
  	    children.put(argChar, argChild);
  	    return true;
  	}
  	

    // isInCharacts - sees if a character is in the characts vector
    // Parameters:
    //    c - the character to look for in the vector
  	public boolean isInCharacts(char c) {
  	    return characts.contains(c);   
  	}
  	

    // getAChar - returns the character in characts at the index passed to it
    // Parameters:
    //    index - index of the character to return in the characts vector 
  	public char getAChar(int index) {
  	    return characts.get(index);
  	}
  	

    // getCharSize - returns the size of the characters vector
  	public int getCharSize() {
  	    return characts.size();
  	}


    // isInValues - sees if a string is in the values vector
    // Parameters:
    //    arg - the string to look for in the values vector
    public boolean isInValues(String arg) {
        return values.contains(arg);
    }


    // getChild - returns the child DAWG_Node arg transitions to
    // Parameters:
    //      arg - character that transitions to the child DAWG_Node
    public DAWG_Node getChild(Character arg) {
    	return children.get(arg);
    }
    

    // getValues - returns the values vector
    public Vector<String> getValues() {
    	return values;
    }
    

    // getCharacts - returns the characts vector
    public Vector<Character> getCharacts() {
    	return characts;
    }
    

    // isWord - returns isValidWord
    public boolean isWord() {
    	return isValidWord;
    }
    

    // toString - returns of the characters in characts
    //            and of the strings in values as a string
    public String toString() {
    	String temp = new String();
    	
    	temp += "Characters:  ";
    	
    	for(int i = 0; i < characts.size(); i++)
    		temp += characts.get(i) + " ";
    		
    	temp += "\nStrings:  ";
    	
    	for(int j = 0; j < values.size(); j++)
    		temp += values.get(j) + " ";
    		
    	temp += "\n";
    	
    	return temp;
    }


    // getChildren - returns children
    public Map<Character, DAWG_Node> getChildren() {
    	return children;
    }
    

    // writeObject - serializes the DAWG_Node
    public void writeObject(ObjectOutputStream output) throws IOException {
    	output.defaultWriteObject();
    	try {
  			output.writeObject(characts);
  		} catch (Exception e) {
  			System.out.println("Serializing the characts did not work.");
  			System.out.println(e);
  		}
    	try {
  			output.writeObject(values);
  		} catch (Exception e) {
  			System.out.println("Serializing the values did not work.");
  			System.out.println(e);
  		}
    	output.writeBoolean(isValidWord);
    	try {
  			output.writeObject(children);
  		} catch (Exception e) {
  			System.out.println("Serializing the children did not work.");
  			System.out.println(e);
  		}
    }
    

    // readObject - de-serializes the DAWG_Node
    public void readObject(ObjectInputStream input) throws ClassNotFoundException, IOException {
    	input.defaultReadObject();
    	try {
  			characts = (Vector<Character>) input.readObject();
  		} catch (Exception e) {
  			System.out.println("De-Serializing the characts did not work.");
  			System.out.println(e);
  		}
  		try {
  			values = (Vector<String>) input.readObject();
  		} catch (Exception e) {
  			System.out.println("De-Serializing the values did not work.");
  			System.out.println(e);
  		}
  		isValidWord = (Boolean) input.readObject();
  		try {
  			children = (HashMap<Character, DAWG_Node>) input.readObject();
  		} catch (Exception e) {
  			System.out.println("De-Serializing the children did not work.");
  			System.out.println(e);
  		}
    }

}