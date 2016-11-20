import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class CrawlerTest {
	
	String localPath; 
	String internetFile;
	String expectedFile;
	String absolutePath;
	
	@SuppressWarnings("resource")
	protected String expectedOutput() throws FileNotFoundException {
		File file = new File(absolutePath.concat(localPath.concat(expectedFile)));
		try (
				Scanner scn = new Scanner(file).useDelimiter("\\Z");
				) {
			String output = scn.next();
			scn.close();
			return output;
		}
	}
}
