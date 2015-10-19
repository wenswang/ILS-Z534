import java.util.Scanner;

import org.apache.lucene.analysis.core.KeywordAnalyzer;
import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.analysis.core.StopAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;


public class indexComparison {

	public static void main(String[] args) throws Exception {
		
		// TODO Auto-generated method stub	
		System.out.println("Enter 1 for Keyword:");
		System.out.println("Enter 2 for Simple:");
		System.out.println("Enter 3 for Stop:");
		System.out.println("Enter 4 for Standard:");

		// User is asked which analyzer to use.
		Scanner kbd =new Scanner(System.in);
		int number = kbd.nextInt();
		switch(number){
		
			case 1:generateIndex genKey = new generateIndex(new KeywordAnalyzer());
			genKey.readFiles(); 
			System.out.println("KEYWORD ANALYZER:");
			genKey.extractData("/Users/wensiwang/Desktop/Z534/Assignment/Assignment1/result");
			break;
			
			case 2:generateIndex genSimple = new generateIndex(new SimpleAnalyzer());
			genSimple.readFiles(); 
			System.out.println("SIMPLE ANALYZER:");
			genSimple.extractData("/Users/wensiwang/Desktop/Z534/Assignment/Assignment1/result");
			break;
			
			case 3:generateIndex genStop = new generateIndex(new StopAnalyzer());
			genStop.readFiles(); 
			System.out.println("STOP ANALYZER:");
			genStop.extractData("/Users/wensiwang/Desktop/Z534/Assignment/Assignment1/result");
			break;
			
			case 4:generateIndex genStand = new generateIndex(new StandardAnalyzer());
			genStand.readFiles(); 
			System.out.println("STANDARD ANALYZER:");
			genStand.extractData("/Users/wensiwang/Desktop/Z534/Assignment/Assignment1/result");
			break;
			}
		}
	}