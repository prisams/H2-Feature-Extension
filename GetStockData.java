import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Class to get the data from website(yahoo finance)
 * at runtime and store in a file.
 * We would use this file to insert records the database
 *
 * @author Priyanka Samanta
 *
 * Date - 02-Nov-2015
 */

public class GetStockData {

	//Using the Mozilla browser to hit the browser.
	private static final String BROWSER = "Mozilla/5.0";
	static String ticker;
	
	/**
	 * to get the data from the website using httpRequest
	 * @param ticker
	 * @param noOfDays
	 * @throws Exception
	 */
	public void getStreamingData(String ticker, int noOfDays) 
			throws Exception {
		GetStockData weblink = new GetStockData();
		System.out.println("Sending request .......");
		
		//sending the request
		weblink.sendRequest(ticker,noOfDays);
		PopulateTimeStamp t = new PopulateTimeStamp();
		
		//Populating the timestamp table for 
		//the newly obtained records
		t.dumpDataInDatabase();
		
		//Populating the Stock table with the received data from the website
		PopulateDailyStock p = new PopulateDailyStock();
		p.dumpDataInDatabase(ticker);

	}

	/**
	 * HTTP  send request
	 * @param ticker
	 * @param days
	 * @throws Exception
	 */
	public void sendRequest(String ticker,int days) throws Exception {
		
		//Constructing the URL to be hit
		String url = "http://chartapi.finance.yahoo.com/instrument/1.0/" + ticker +
				"/chartdata;type=quote;range="+ days + "d/csv";

		URL object = new URL(url);
		HttpURLConnection connection = (HttpURLConnection) object.openConnection();

		// optional default is GET
		connection.setRequestMethod("GET");

		//add request header
		connection.setRequestProperty("Agent", BROWSER);

		BufferedReader in = new BufferedReader(
				new InputStreamReader(connection.getInputStream()));
		String line;
		//Writing the data received from the website in a text file
		PrintWriter writer = new PrintWriter("ticker.txt", "UTF-8");
		while ((line = in.readLine()) != null) {
			System.out.println(line);
			writer.println(line);
		}
		in.close();
		writer.close();
	}
}