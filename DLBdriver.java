import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class DLBdriver {

	public static void main(String[] args) throws FileNotFoundException {

		DictInterface DLB = new DLB();
		
	
	
		// Add an arbitrary amount of words to the DLB
		// Call searchPrefix to verify that they are properly added
		// Call delete to remove them
		// Call searchPrefix on the deleted string to verify the method works
		
//		
		DLB.add("test");
		DLB.add("testing");
		DLB.add("apple");
		DLB.add("boy");
		DLB.add("girl");
		DLB.add("botch");
		DLB.add("botched");


//		
//		System.out.println(DLB);
//		
//		System.out.println(DLB.searchPrefix(tmp));
//		

//		
//		System.out.println(DLB.searchPrefix(tmp));
//		
//		tmp = new StringBuilder("apple");
//		System.out.println(DLB.searchPrefix(tmp));
		
//		Scanner dictionary = new Scanner(new FileInputStream(args[0]));
////		
//		String line;
//		while (dictionary.hasNext()) {		//	Populate the MyDictionary from the dictionary file contents
//			line = dictionary.nextLine();	
////			System.out.println(line);
//
//			DLB.add(line);
//		}
//		dictionary.close();
		
		
//		System.out.println(DLB);
//		StringBuilder tmp = new StringBuilder("zurich");
//		System.out.println(DLB.searchPrefix(tmp));	// 0
//		tmp = new StringBuilder("t");
//		System.out.println(DLB.searchPrefix(tmp));	// 1
//		tmp = new StringBuilder("apple");
//		System.out.println(DLB.searchPrefix(tmp));	// 2
//		tmp = new StringBuilder("testing");
//		System.out.println(DLB.searchPrefix(tmp));	// 2
//		tmp = new StringBuilder("test");
//		System.out.println(DLB.searchPrefix(tmp));	// 3
//		tmp = new StringBuilder("boy");
//		System.out.println(DLB.searchPrefix(tmp));	// 3
//		tmp = new StringBuilder("botch");
//		System.out.println(DLB.searchPrefix(tmp));	// 3

	
	}
	
	


}
