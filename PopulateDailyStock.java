import java.io.BufferedReader;
import java.io.FileReader;
import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.policies.DefaultRetryPolicy;

/**
 * Class to get populate the daily stock table
 * in the database with the data stored in the
 * file from the HTTP get request
 *
 * @author Priyanka Samanta
 *
 * Date: 12-Nov-2015
 */

public class PopulateDailyStock {

	/**
	 * To dump the data from the file in the database
	 * @param ticker
	 */
	public void dumpDataInDatabase(String ticker){
		Cluster cluster = Cluster.builder().addContactPoint("localhost").
				withRetryPolicy(DefaultRetryPolicy.INSTANCE).build();
		
		//Starting a session with the database
		Session session = cluster.connect("team3");

		//Prepared Statement to insert data in the DailyStockData Table
		PreparedStatement insert_dailyStock = session.prepare
				("INSERT INTO team3.DAILYSTOCKDATA"
						+ "(ticker,timestamp,closing_price,high_price,"
						+ "low_price,opening_price,volume)"
						+ "VALUES(?,?,?,?,?,?,?);");

		BoundStatement bs1 = new BoundStatement(insert_dailyStock);

		//reading data from file and populating the database table
		try{
			BufferedReader input = new BufferedReader(new FileReader("ticker.txt"));
			String lineRead;
			while((lineRead=input.readLine())!=null){
				String[] dailyStock=lineRead.split(",");
				if((dailyStock.length==6)
						&&(!lineRead.contains("values"))){

					//populating dailystockdata table
					session.execute(bs1.bind(ticker,
							Integer.parseInt(dailyStock[0]),
							Double.parseDouble(dailyStock[1]),
							Double.parseDouble(dailyStock[2]),
							Double.parseDouble(dailyStock[3]),
							Double.parseDouble(dailyStock[4]),
							Double.parseDouble(dailyStock[5])));
				}
			}
			input.close();
		}
		catch(Exception e){
		}
		cluster.close();
		session.close();
	}
}