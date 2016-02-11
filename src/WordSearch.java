import java.util.ArrayList;
import java.util.HashMap;

/**
 * 
 */

/**
 * @author Shane Hare
 *
 */
public class WordSearch {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		if (args.length != 2){
			//Call usage function
			return;
		}
		
		//HashMap to Add Arrays into
		HashMap<String, Thread> map = new HashMap<String, Thread>();
	}
	
	public static void usage(){
		// TODO - Generate Usage Message
	}
	
	public static void argsErroneous(){
		// TODO - Generate Usage Message for Erroneous Arguments
	}

}

class ReadFileThread extends Thread{

	private String file;
	
	public ReadFileThread(String file){
		this.file = file;
	}
	
	public void run() {
		// TODO Auto-generated method stub
	}
	
	public void start(){
		// TODO Auto-generated method stub		
	}
}

class PrintFoundThread extends Thread{
	
	private String word;
	private ArrayList printQueue; //Use this for Queue Printing
	
	public PrintFoundThread(String word){
		
	}

	public void run() {
		// TODO Auto-generated method stub
	}
	
	public void start(){
		// TODO Auto-generated method stub		
	}	
}
