import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * This class builds an internet model from json.
 * 
 * @author Lee
 */
public class Internet {
	
	String root;
	Map<String, String[]> sites;
	
	/**
	 * Retrieves the set of links for a given address.
	 * 
	 * @param addr string node address to retrieve the list for
	 * @return 
	 */
	public String[] getLinks(String addr) {
		return sites.get(addr);
	}
	
	/**
	 * Constructs an internet object from the provided json file
	 * 
	 * @param fileName String path to a valid json file
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws ParseException
	 */
	public Internet(String fileName) 
			throws FileNotFoundException, IOException, ParseException {
		sites = new HashMap<>();
		
		//Parse the json
		JSONParser parser = new JSONParser();
		Object parsedObject = parser.parse(new FileReader(fileName));
		JSONObject rootObj = (JSONObject)parsedObject;
		buildHelper(rootObj);
	}
	
	/**
	 * This method takes a JSONObject and populates the root and sites map.
	 * 
	 * @param obj JSONObject internet
	 */
	private void buildHelper(JSONObject obj) {
		
		JSONArray pagesArray = (JSONArray)obj.get("pages");
		for (Object o : pagesArray) {
			JSONObject siteObj = (JSONObject)o;
			String addr = siteObj.get("address").toString();
			if (root == null) {
				root = addr;
			}
			JSONArray linkArr = (JSONArray)siteObj.get("links");
			String[] links = new String[linkArr.size()];
			for (int i=0; i<linkArr.size(); i++) {
				links[i] = linkArr.get(i).toString();
			}
			
			sites.put(addr, links);
		}
	}
	
	/**
	 * Provides a printable version of the internet for debugging.
	 */
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Root: ").append(root).append('\n');
		for (String k : sites.keySet()) {
			sb.append("Page: ").append(k).append(" -> ");
			for (String l : sites.get(k)) {
				sb.append(l).append(" ");
			}
			sb.append('\n');
		}
		return sb.toString();
	}
}
