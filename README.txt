This program runs on Java 1.7.0_25.  There’s probably a good possibility that it will work on Java 1.6.#.  The main Java library that it uses is Vector.

The files Min_Trie.out and Dict_DAWG.out are serialized files, not executable files.

All that should be needed to run this program is to enter the command:

java Player

at the command line, while be in the directory NazorYost_Scrabble.


If an error arises about serialization, then try the command:

java DAWG

This should take about 5 minutes to complete, and all information being stored in each DAWG Node will be displayed on screen (This is due to leaving in debug statements, and taking them out at this time may mess up the serialization process with the JVM. ).  Then try running the first command again.


If an error arises about serialization again, then try the command:

java Trie

This should be ran before going to bed.  When you wake up in the morning, a nice minimized Trie will be displayed on screen and serialized (Again, debug statements have been left in due to possible problems with the serialization process and JVM.).  Then run:

java DAWG

wait about 5 minutes, followed by:

java Player


Enjoy watching the two computer opponents play Scrabble!  :D  You will need to type “yes” before the next turn is taken by the players.  Typing “no” will end the game.

 ~ Jolie Nazor and Kristi Yost


