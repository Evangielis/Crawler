import static org.junit.Assert.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

import org.json.simple.parser.ParseException;
import org.junit.Test;

public class CrawlerTest1 {
	
	@SuppressWarnings("resource")
	private String expectedOutput() throws FileNotFoundException {
		File file = new File("C:\\Users\\Lee\\workspace\\Crawler\\src\\test\\output1");
		try (
				Scanner scn = new Scanner(file).useDelimiter("\\Z");
				) {
			String output = scn.next();
			scn.close();
			return output;
		}
	}
	
	@Test
	public void test() {
		String file = "C:\\Users\\Lee\\workspace\\Crawler\\src\\test\\internet1.json";
		try {
			WebCrawler wc = new WebCrawler(file);
			wc.crawl();
			String result = wc.getReport();
			//String test = expectedOutput();
			System.out.println(result);
			//assertEquals(result, test);			
		} catch (IOException | ParseException e) {
			fail(e.getMessage());
		}
	}

}
