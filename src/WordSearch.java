import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.Scanner;

/**
 * @author Shane Hare
 * @Project1-1 WordSearch
 * 
 * Description:
 * 	   This java program is designed to create two sets of threads and allow
 *     one set of threads to Read Files and the other set of threads to be
 *     able to Print the words found that we determine without stopping
 *     the Read Threads. We accomplish this by using Group1 and Group2 threads.
 *     
 */
public class WordSearch {

	private static String DELIMITER = ","; 
	
	/*
	 * Class ReadThread
	 * Description:
	 *     This class is designed to read the contents of a file and send a 
	 *     print message to the PrintThread Class Asynchronously
	 */
	private static class ReadThread extends Thread{

		private String file;						//File to Read From
		private HashMap<String, PrintThread> map;	//Words Linked to PrintThreads
		private HashSet<String> dictionary;			//Keep track of visited Words
		
		/*
		 * Constructor
		 */
		public ReadThread(String file, HashMap<String, PrintThread> map){
			this.file 		= file;
			this.map 		= map;
			this.dictionary = new HashSet<String>();
		}
		
		/*
		 * function run()
		 * Description:
		 *     Read through the given file and send the print message
		 *     to the designated PrintThread
		 */
		public void run() {
			
			Scanner scan = null;
			try{
				File f = new File(this.file);
				scan = new Scanner(f);
				
				//Loop through the Contents of the File
				while(scan.hasNextLine()){
					String line = scan.nextLine();
					
					String[] arr = line.split("[^a-zA-z]+");
					
					
					for(String found : arr){
						String word = found.toLowerCase();
						
						//If the word has not been found, print it
						if(dictionary.add(word)){				
							//System.out.println(word + " " + this.file);
							if (map.containsKey(word)){
								PrintThread th = map.get(word);
								th.addQueue(this.file);
							}
						}
					}				
				}
			}
			catch(FileNotFoundException ex){
				System.err.println(ex.getMessage());
			}
			catch(Exception ex){
				System.err.println(ex.getMessage());
			}
		}	
	}	
	
	/*
	 * Class PrintThread
	 * Description:
	 *     This class is designed to take print a queue of Strings 
	 *     where a word was found in what file.   
	 */
	private static class PrintThread extends Thread{
		private volatile boolean running;
		private String word;
		private LinkedList<String> queue; //Use this for Queue Printing
		
		/*
		 * Constructor
		 */
		public PrintThread(String word){
			this.word 		= word.toLowerCase(); // <-- always make sure the word is lowerCase
			this.queue 		= new LinkedList<String>();
			this.running 	= true;
		}
		
		/*
		 * function run()
		 * Description:
		 *     Loop until the thread is terminated to see if you need
		 *     to print the queue.
		 */
		public void run() {
			//Keep looping forever until terminated!
			while(running || !queue.isEmpty()){
				print();
			}
		}
		
		/*
		 * Function print()
		 * Description:
		 *     Print the queue and remove it.
		 */
		public synchronized void print(){
			if (!queue.isEmpty()){
				System.out.println(this.word + " " + queue.getFirst());
				queue.removeFirst();			
			}
		}
		
		/*
		 * function addQueue
		 * Description:
		 * 		Take in the file
		 * @param file - Name of the file where the word came from
		 */
		public synchronized void addQueue(String file){
			queue.addLast(file);	
		}

		/*
		 * function terminate()
		 * Description:
		 *      Allows the thread to terminate gracefully if it 
		 *      still has items in the queue to print
		 */
		public void terminate(){
			this.running = false;
		}
	}	
	
	/*
	 * function usage()
	 * Description:
	 *     Used to print out a usage message to the user on Std.err
	 */
	public static void usage(){
		System.err.println("Usage: java WordSearch <files> <words>");
		System.err.println("<files> - list of one or more text file names, separated by commas, with no whitespace.");
		System.err.println("<words> - list of one or more target words, separated by commas, with no whitespace");
		System.exit(1);
	}
	
	/*
	 * function checkArgsFiles
	 * Description:
	 *     Check the parameters for the files provided by the user.
	 *     If there is an error with finding a file, print usage
	 *     
	 */
	public static void checkArgsFiles(String fileLine){
		boolean cont = true;
		
		String[] arr = fileLine.split(DELIMITER);
		
		for(String file : arr){
			File f = new File(file);
			cont = cont && f.exists() && !f.isDirectory();
			
			//If File doesn't exist, print error message
			if (!f.exists()) { System.err.println(file + " Does not exist."); }
			
			//If file is a directory print error message
			else if (f.isDirectory()) { System.err.println(file + " is not a file, it is a directory"); }
		}
		
		//If Errors were found in the files, exit
		if(!cont) usage();
	}
	
	/*
	 * function checkArgsWords
	 * Description:
	 *     Check the parameters for the words provided by the user.
	 *     If there is an error with a word not being only characters
	 *     from the alphabet (English), print the error.
	 *     
	 */	
	public static void checkArgsWords(String wordLine){
		boolean cont = true;
		
		String[] arr = wordLine.split(DELIMITER);
		
		for(String word : arr){
			//Regular Expression for a Word
			if (!word.matches("[a-zA-Z]+")) { 
				System.err.println(word + " is not a word."); 
				cont = false;
			}
		}
		
		//If Errors were found in the files, exit
		if(!cont) usage();
	}	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		if (args.length != 2){ usage(); }
		
		//Check the Arguments to see if you can continue
		checkArgsFiles(args[0]);
		checkArgsWords(args[1]);
		
		String[] fSplit = args[0].split(DELIMITER);
		String[] wSplit = args[1].split(DELIMITER);
		
		HashMap<String, PrintThread> Group2 = new HashMap<String, PrintThread>();
		LinkedList<ReadThread> Group1 		= new LinkedList<ReadThread>();
		
		//Create a PrintThreads & start them
		for(String word : wSplit){
			String lower = word.toLowerCase();
			PrintThread temp = new PrintThread(lower);
			temp.start();
			Group2.put(lower,temp);
		}
		
		//Create ReadThreads
		for(String file : fSplit){
			String lower = file.toLowerCase();
			ReadThread temp = new ReadThread(lower,Group2);
			temp.start();
			Group1.add(temp);
		}
	
		//Join the Threads
		for(ReadThread th : Group1){
			try {
				th.join();
			} catch (InterruptedException e) {
				System.err.println(e.getMessage());
			}			
		}
		
		//Exit the PrintThreads Gracefully
		for(Entry<String, PrintThread> entry : Group2.entrySet()){
			PrintThread th = entry.getValue();
			th.terminate();
		}
	}
}