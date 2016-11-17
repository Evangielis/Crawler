import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import java.util.Vector;
import java.util.concurrent.RecursiveAction;

import org.json.simple.*;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


public class WebCrawler {

	private List<String> visited;
	private List<String> skipped;
	private List<String> errors;
	{
		visited = new Vector<>();
		skipped = new Vector<>();
		errors = new Vector<>();
	}
	
	public void crawl(Internet inet) {
	}
	
	class ForkCrawl extends RecursiveAction {

		WebCrawler crawler;
		Internet inet;
		Stack<String> links;
		
		public ForkCrawl(WebCrawler crawler, Internet inet, 
				Stack<String> links) {
			this.crawler = crawler;
			this.inet = inet;
			this.links = links;
		}
		
		public ForkCrawl(WebCrawler crawler, Internet inet, String link) {
			this.crawler = crawler;
			this.inet = inet;
			this.links = null;
			followLink(link);
		}
		
		public void followLink(String link) {
			//Logic for crawling here
		}
		
		@Override
		protected void compute() {
			// TODO Auto-generated method stub
			String link = links.pop();
			
			if (links.isEmpty()) {
				followLink(link);
			} else {
				invokeAll(new ForkCrawl(crawler, inet, link),
						new ForkCrawl(crawler, inet, links));
			}
		}
		
	}
	
	public static void main(String[] args) {
		String file1 = "C:\\Users\\Lee\\workspace\\Crawler\\src\\test\\internet1";
		try {
			Internet i = new Internet(file1);
			System.out.println(i);
		} catch (IOException | ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
