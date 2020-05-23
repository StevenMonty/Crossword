/**
 * A java implementation of a De la Briandais trie
 * 
 * @author StevenMontalbano - smm285@pitt.edu
 */

/*			TODO 
 * 
 * Delete Method - Extra Credit
 *
 */

public class DLB implements DictInterface {

	private Node root;
	private int size;
	private final int NOT_FOUND = 0;
	private final int PREFIX = 1;
	private final int WORD = 2;
	private final int WORD_AND_PREFIX = 3;	
	private static final boolean DEBUG_MODE = false;	// Prints debug print statements while true	

	public DLB() {
		root = new Node('*');	// Root level nodes will be siblings of this node with a arbitrary sentinel char
		size = 0;
	}

	@Override
	public boolean add(String s) {

		//		if (searchPrefix(new StringBuilder(s)) > 0)	// Check if word already exists in the trie
		//			return false;

		s = s.toLowerCase();	// All strings are stored in lower case form; this is to make all char comparisons easier
		int i = 0;
		char c = s.charAt(i);
		Node cur = root;

		while(i < s.length()) {
			c = s.charAt(i);

			if (cur.data == c) {	// letter found
				if (cur.child == null)	// if child is null, then create a new child for the next char
					try {
						cur.child = new Node(s.charAt(i+1));	// handles the last char
					} catch (Exception e) {
						cur.isWord = true;
						return true;
					}

				cur = cur.child;	// move down the trie
				i++;
			} else {	// search siblings for the current char
				if (cur.sibling == null)
					cur.sibling = new Node(c);

				cur = cur.sibling;
			}	
		}

		cur.isWord = true;
		size++;

		return true;	// Word successfully added
	}

	public boolean delete(String s) {	// TODO Extra Credit

		StringBuilder target = new StringBuilder(s.toLowerCase());

		if(searchPrefix(target) == NOT_FOUND) 
			return false;		// Should this return true since the trie wasnt actually altered? 

		// Deleting just a prefix will compromise the structure of the other words that are children of the prefix s
		if(searchPrefix(target) == PREFIX) 
			return false;	

		if(searchPrefix(target) == WORD) {
			// Delete the whole word
		}

		if(searchPrefix(target) == WORD_AND_PREFIX) {
			// Set the isWord boolean to be false at the last node of the target word
		}

		return true;
	}

	@Override
	public int searchPrefix(StringBuilder s) {
		return searchPrefix(s, 0, s.length()-1);
	}


	//TODO comments and fix single line brackets
	@Override
	public int searchPrefix(StringBuilder s, int start, int end) {	

		Node cur = root;
		String target = s.toString().substring(start, end+1).toLowerCase();		
		int i = 0;
		char c = target.charAt(i);
		int returnVal = 0;

		debugPrint("Attempting to find " + target);

		while(i < target.length()) {
			c = target.charAt(i);
			debugPrint("cur.data = " + cur.data); debugPrint("cur.isWord = " + cur.isWord);

			if (cur.data == c) {
				if (i == target.length() - 1) {
					if (cur.isWord) 
						returnVal += 2;

					if (cur.child != null) 
						returnVal += 1;

					return returnVal;
				}

				if (cur.child != null) {
					cur = cur.child;
					i++;
				} else 
					return NOT_FOUND;
			} else {
				
				if (cur.sibling == null) 
					return NOT_FOUND;
				else 
					cur = cur.sibling;
			}
		}

		if(!cur.isWord && cur.child != null) 
			return PREFIX;
		else if(cur.isWord && cur.child == null) 
			return WORD;
		else if(cur.isWord && cur.child != null) 
			return WORD_AND_PREFIX;
		else 
			return NOT_FOUND;	
	}

	@Override
	public String toString() {	//TODO

		if(size == 0) {
			return new String("DLB Trie is currently empty");
		}

		return new String();
	}

	public static void debugPrint(char c) {
		debugPrint(String.valueOf(c));
	}

	public static void debugPrint(String s) {
		if(DEBUG_MODE) System.out.println(s);		
	}

	/**
	 *  Static inner class to represent the nodes of the DLB trie.
	 *  Getters and Setters are not necessary since Node is a static inner class
	 *  the DLB has full access to a Node's private data members
	 */
	private static class Node {

		private char data;		// The char that this node represents
		private Node child;		// The node that is the child of this node, lives below this node on the trie
		private Node sibling;	// The node that is the sibling of this node, lives adjacent to this node in the trie
		private boolean isWord;	// Instead of using a sentinel char to represent the end of a word, a boolean flag is used at 
		// the last char of a word

		public Node(char data) {
			this.data = data;
		}

		@Override
		public String toString() {
			return new String(String.valueOf(data));
		}

	}	// End Node class

}	// End BLD class
