import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * @author Shane Hare
 *
 */
public class WordSearch {

	private static String DELIMITER = ","; 
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		/*if (args.length != 2){
			//Call usage function
			return;
		}*/
		
		args = new String[2];
		args[0] = "file1.txt,file2.txt";
		args[1] = "word,HEAVENS,God,void";
		
		String[] fSplit = args[0].split(DELIMITER);
		String[] wSplit = args[1].split(DELIMITER);
		
		HashMap<String, PrintThread> Group2 = new HashMap<String, PrintThread>();
		LinkedList<ReadThread> Group1 = new LinkedList<ReadThread>();
		
		//Create a PrintThreads
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
				// TODO Auto-generated catch block
				e.printStackTrace();
			}			
		}
		
		//Exit the Threads
		for(Entry<String, PrintThread> entry : Group2.entrySet()){
			PrintThread th = entry.getValue();
			th.terminate();
		}
	}
	
	public static void usage(){
		// TODO - Generate Usage Message
	}
	
	public static void argsErroneous(){
		// TODO - Generate Usage Message for Erroneous Arguments
	}

}

class ReadThread extends Thread{

	private String file;
	private HashMap<String, PrintThread> map;
	private HashSet<String> dictionary;
	
	public ReadThread(String file, HashMap<String, PrintThread> map){
		this.file = file;
		this.map = map;
		this.dictionary = new HashSet<String>();
	}
	
	public void run() {
		
		Scanner scan = null;
		try{
			File f = new File(this.file);
			scan = new Scanner(f);
			
			while(scan.hasNextLine()){
				String line = scan.nextLine();
				
				Scanner words = new Scanner(line);
				
				while(words.hasNext()){
					String word = words.next().toLowerCase();
					
					//If the word has not been found, print it
					if(dictionary.add(word)){				
						//System.out.println(word + " " + this.file);
						if (map.containsKey(word)){
							PrintThread th = map.get(word);
							th.addQueue(this.file);
						}
					}
				}
				
				words.close();
				
			}
		}
		catch(FileNotFoundException ex){
			System.out.println(ex.getMessage());
		}
		catch(IOException ex){
			System.out.println(ex.getMessage());
		}
		catch(Exception ex){
			System.out.println(ex.getMessage());
		}
	}	
}

class PrintThread extends Thread{
	private volatile boolean running;
	private String word;
	private LinkedList<String> queue; //Use this for Queue Printing
	
	public PrintThread(String word){
		this.word = word.toLowerCase(); // <-- always make sure the word is lowerCase
		this.queue = new LinkedList<String>();
		this.running = true;
	}

	public void run() {
		
		//Keep looping forever!
		while(running || !queue.isEmpty()){
			//If the queue is NOT empty - print
			if(!queue.isEmpty()){
				printMe();
				queue.removeFirst();
			}
		}
	}
	
	public void addQueue(String file){
		queue.addLast(file);	
	}

	public void printMe(){
		System.out.println(this.word + " " + queue.getFirst());
	}

	public void terminate(){
		this.running = false;
	}
}