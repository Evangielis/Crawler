import static org.junit.Assert.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

import org.json.simple.parser.ParseException;
import org.junit.Assume;
import org.junit.Test;

public class CrawlerTest2 {
	
	final String localPath; 
	final String internetFile;
	final String expectedFile;
	final String absolutePath;
	{
		Path currentRelativePath = Paths.get("");
		absolutePath = currentRelativePath.toAbsolutePath().toString();
		localPath = "\\src\\test\\data\\";
		internetFile = "internet2.json";
		expectedFile = "expected2";
	}
	
	@SuppressWarnings("resource")
	private String expectedOutput() throws FileNotFoundException {
		File file = new File(absolutePath.concat(localPath.concat(expectedFile)));
		try (
				Scanner scn = new Scanner(file).useDelimiter("\\Z");
				) {
			String output = scn.next();
			scn.close();
			return output;
		}
	}
	
	@Test
	public void testInternet1() {
		String filePath = absolutePath.concat(localPath.concat(internetFile));
		try {
			WebCrawler wc = new WebCrawler(filePath);
			wc.crawl();
			String result = wc.getReport();
			String test = expectedOutput();
			System.out.println("---------------------------------------------");
			System.out.println("-=RESULTS FROM " + internetFile + "=-");
			System.out.println(result);
			System.out.println("-=EXPECTED OUTPUT FOR " + internetFile + "=-");
			System.out.println(test);
			System.out.println("---------------------------------------------");
		} catch (IOException | ParseException e) {
			fail(e.getMessage());
		}
	}

}
