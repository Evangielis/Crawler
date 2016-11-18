import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

import org.json.simple.parser.ParseException;


public class WebCrawler {

	private Set<String> visited;
	private Set<String> successes;
	private Set<String> skipped;
	private Set<String> errors;
	{
		visited = new HashSet<>();
		successes = new HashSet<>();
		skipped = new HashSet<>();
		errors = new HashSet<>();
	}
	
	public Internet inet;
	public WebCrawler(String filePath) throws FileNotFoundException, IOException, ParseException {
		inet = new Internet(filePath);
	}
	
	public void addVisited(String link) {
		synchronized(visited) {
			visited.add(link);
		}
	}
	
	public boolean isVisited(String link) {
		boolean result;
		synchronized(visited) {
			result = visited.contains(link);
		}
		return result;
	}
	
	public void addSuccess(String link) {
		synchronized(successes) {
			successes.add(link);
		}
	}
	
	public void addSkipped(String link) {
		synchronized(skipped) {
			skipped.add(link);
		}
	}
	
	public void addError(String link) {
		synchronized(errors) {
			errors.add(link);
		}
	}
	
	public String[] visitLink(String link) {
		return inet.getLinks(link);
	}
	
	class ForkCrawl extends RecursiveAction {

		WebCrawler crawler;
		String addr;
		
		public ForkCrawl(WebCrawler crawler, String addr) {
			this.crawler = crawler;
			this.addr = addr;
		}
		
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
		
		@Override
		protected void compute() {
			
			String[] links = visitLink(addr);
			//logVisit(links);
			
			if (links == null) {
				crawler.addError(addr);
				return;
			} else {
				crawler.addSuccess(addr);
			}
				
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
				invokeAll(tasks);
			}
		}
	}
	
	public void crawl() {
		ForkCrawl fc = new ForkCrawl(this, inet.root);
		this.addVisited(inet.root);
		ForkJoinPool pool = new ForkJoinPool();
		pool.invoke(fc);
	}
	
	public String getReport() {
		StringBuilder sb = new StringBuilder();
		sb.append("Success:").append('\n');
		sb.append(formatCollection(successes)).append('\n').append('\n');
		sb.append("Skipped:").append('\n');
		sb.append(formatCollection(skipped)).append('\n').append('\n');
		sb.append("Error:").append('\n');
		sb.append(formatCollection(errors)).append('\n').append('\n');
		return sb.toString();
	}
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
		String file = "C:\\Users\\Lee\\workspace\\Crawler\\src\\test\\internet2.json";
		try {
			WebCrawler wc = new WebCrawler(file);
			wc.crawl();
			System.out.println(wc.getReport());
		} catch (IOException | ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
