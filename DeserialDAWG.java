/* Deserialize DAWG class
 * Jolie Nazor and Kristi Yost
 * December 9, 2013
 * Dr. Ravikumar
 * CS 454 - Theory of Computation
 * Semester Project: Scrabble
 *
 * Description:  This class opens a serialized DAWG class to create a DAWG object.
 *				 The DAWG object can be returned by this class.
 */







import java.util.*;
import java.lang.*;
import java.io.*;




public class DeserialDAWG {

	DAWG playerDAWG;

	// DeserializeDAWG constructor - deserialize a DAWG serialized file.
	public DeserialDAWG() {
	
		playerDAWG = new DAWG(new Vector<Vector <Node>>());
	
	
		try {
		
            ObjectInputStream my_in = new ObjectInputStream(new FileInputStream("Dict_DAWG.out"));
            playerDAWG = (DAWG) my_in.readObject();
            
        } catch (FileNotFoundException e) {
           	System.out.println("File not found, and this doesn't mean the Trie.class file.");
        } catch (IOException f) {
           	System.out.println("Must catch this IOException to make Java happy.");
           	System.out.println(f);
        } catch (ClassNotFoundException g) {
           	System.out.println("Must catch this ClassNotFoundException to make Java happy.");
        }
	
	}
	
	
	
	
	
	// getDAWG - returns the DAWG data member object "playerDAWG".
	public DAWG getDAWG() {
		return playerDAWG;
	}
	
	
	
	
	// used for testing
	public static void main(String[] args) {
	
		DeserialDAWG myTest = new DeserialDAWG();
		
		myTest.getDAWG().printDAWG_Nodes();
	}


}
