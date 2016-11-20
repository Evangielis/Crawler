import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

import org.json.simple.parser.ParseException;

/**
 * Main part of the project creates and crawls an
 * Internet object from a json file. 
 * 
 * @author Lee Painton
 */
public class WebCrawler {

	//All of these sets track addresses
	private final Set<String> visited;
	private final Set<String> successes;
	private final Set<String> skipped;
	private final Set<String> errors;
	{
		visited = new HashSet<>();
		successes = new HashSet<>();
		skipped = new HashSet<>();
		errors = new HashSet<>();
	}
	
	//The Internet object to crawl
	private final Internet inet;
	
	/**
	 * Passing the constructor a valid path to a json file
	 * causes it to build an Internet object and prep for crawling. 
	 * 
	 * @param filePath String valid path to json file
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws ParseException
	 */
	public WebCrawler(String filePath) throws FileNotFoundException, IOException, ParseException {
		inet = new Internet(filePath);
	}
	
	//The methods below provide mutators and accessors as needed.
	//Note that all accessors and mutators are synchronized on
	//the their underlying collections to provide thread-safety.
	private void addVisited(String link) {
		synchronized(visited) {
			visited.add(link);
		}
	}
	private boolean isVisited(String link) {
		boolean result;
		synchronized(visited) {
			result = visited.contains(link);
		}
		return result;
	}
	private void addSuccess(String link) {
		synchronized(successes) {
			successes.add(link);
		}
	}
	private void addSkipped(String link) {
		synchronized(skipped) {
			skipped.add(link);
		}
	}
	private void addError(String link) {
		synchronized(errors) {
			errors.add(link);
		}
	}
	
	/**
	 * Wraps the Internet object's getLink method.
	 * 
	 * @param link String address of link to get
	 * @return array of Strings
	 */
	public String[] visitLink(String link) {
		return inet.getLinks(link);
	}
	
	/**
	 * Inner class which uses Java's Fork/Join framework to 
	 * perform the crawling functionality both concurrently
	 * and recursively. 
	 * 
	 * @author Lee
	 */
	class ForkCrawl extends RecursiveAction {

		WebCrawler crawler;
		String addr;
		
		/**
		 * Constructs a new ForkCrawl task.
		 * 
		 * @param crawler the active WebCrawler
		 * @param addr the String address being crawled
		 */
		public ForkCrawl(WebCrawler crawler, String addr) {
			this.crawler = crawler;
			this.addr = addr;
		}
		
		//Debugging method
		private void logVisit(String[] links) {
			StringBuilder sb = new StringBuilder();
			sb.append(addr).append(" -> ");
			if (links != null) {
				for (String s : links) {
					sb.append(s).append(' ');
				}
			} else {
				sb.append("error");
			}
			sb.append('\n');
			System.out.println(sb.toString());
		}
		
		/**
		 * Main computational task invoked by the Fork/Join framework.
		 * Creates new ForkCrawl objects for each link being crawled.
		 */
		@Override
		protected void compute() {
			//Invariant: addr is not null
			//Base case: if there are no new links to follow
				//or no site for addr 
			
			//Get the list of links at this address
			String[] links = visitLink(addr);
			
			//Branch and terminate if sites doesn't exist.
			if (links == null) {
				crawler.addError(addr);
				return;
			} else {
				crawler.addSuccess(addr);
			}
				
			//Main branch builds a new task for each non-visited address
			if (links.length > 0) {
				Set<ForkCrawl> tasks = new HashSet<ForkCrawl>();
				for (String link : links) {
					
					if (crawler.isVisited(link)) {
						crawler.addSkipped(link);
					} else {
						crawler.addVisited(link);
						tasks.add(new ForkCrawl(crawler, link));
					}
				}
				//Start new tasks
				invokeAll(tasks);
			}
		}
	}
	
	/**
	 * Starts the WebCrawler.
	 */
	public void crawl() {
		ForkCrawl fc = new ForkCrawl(this, inet.root);
		this.addVisited(inet.root);
		ForkJoinPool pool = new ForkJoinPool();
		pool.invoke(fc);
	}
	
	/**
	 * Gets a pretty-printed report of the WebCrawler results.
	 * 
	 * @return String
	 */
	public String getReport() {
		StringBuilder sb = new StringBuilder();
		sb.append("Success:").append('\n');
		sb.append(formatCollection(successes)).append('\n').append('\n');
		sb.append("Skipped:").append('\n');
		sb.append(formatCollection(skipped)).append('\n').append('\n');
		sb.append("Error:").append('\n');
		sb.append(formatCollection(errors));
		return sb.toString();
	}
	//Helper method
	private String formatCollection(Collection<String> col) {
		StringBuilder sb = new StringBuilder();
		String[] sortedCol = col.toArray(new String[col.size()]);
		Arrays.sort(sortedCol, (a, b) -> {return a.compareTo(b);});
		sb.append('[');
		String sep = "";
		for (String s : sortedCol) {
			sb.append(sep);
			sb.append('"').append(s).append('"');
			sep = ", ";
		}
		sb.append(']');
		return sb.toString();
	}
	
	public static void main(String[] args) {
		if (args.length < 1){
			System.out.println("Need to provide file path to a valid json file.");
			return;
		}
		
		String path = args[0];
		WebCrawler crawler;
		try {
			crawler = new WebCrawler(path);
			crawler.crawl();
			System.out.println(crawler.getReport());
		} catch (IOException | ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
