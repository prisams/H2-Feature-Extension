import java.util.ArrayList;
import java.util.List;
import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.policies.DefaultRetryPolicy;

/**
 * Class to compute the maximum volume
 * traded for the selected ticker on the
 * selected date.
 *
 * @author Priyanka Samanta
 *
 * Date - 16-Nov-2015
 */

public class TickerMaxVolumeForADay {

	/**
	 * To retrive the maximum volume traded 
	 * for the selected ticker across 
	 * all the hours of the seleced date
	 * 
	 * @param ticker
	 * @param date
	 * @return
	 */
	public String getTickerMaxVolume(String ticker, String date){

		int dateForQuery = Integer.parseInt(date.replace("-", ""));
		String result = null;
		ResultSet results;
		System.out.println("Fetching the details");

		Cluster cluster = Cluster.builder().addContactPoint("localhost").
				withRetryPolicy(DefaultRetryPolicy.INSTANCE).build();

		//Starting a session with the database
		Session session = cluster.connect("team3");

		//Getting the range of timestamp for the entered date
		PreparedStatement selectTimestampRange = session.prepare
				("select start_timestamp,end_timestamp from "
						+ "team3.timestamp where dateasnumbers=?;");

		BoundStatement bs1 = new BoundStatement(selectTimestampRange);
		try{
			//firing the query to get the range of timestamp for the selected date
			results=session.execute(bs1.bind(dateForQuery));

			//Storing the timestamp result
			List<Row> time = results.all();
			int starttimestamp = time.get(0).getInt("start_timestamp");
			int endtimestamp = time.get(0).getInt("end_timestamp");

			System.out.println("Time: " + starttimestamp + " : " + endtimestamp);

			//Getting all the possible volumes traded for the ticker over the 
			//obtained range of timestamp
			PreparedStatement selectRange = session.prepare
					("select volume,timestamp from team3.dailystockdata "
							+ "where ticker=? and timestamp >=? "
							+ "and timestamp<=? ALLOW FILTERING;");

			BoundStatement bs2 = new BoundStatement(selectRange);
			ResultSet result2;
			result2=session.execute(bs2.bind(ticker,starttimestamp,endtimestamp));

			System.out.println("Result size: " + result2);
			ArrayList<Double> volume= new ArrayList<Double>();
			ArrayList<Integer> timestamp = new ArrayList<Integer>();

			//Obtaining the maximum volume traded for the ticker
			//over the day along with the proper time
			double maxVolume = Double.MIN_VALUE;
			int timeStamp=0;
			for(Row row: result2){
				volume.add(row.getDouble("volume"));
				timestamp.add(row.getInt("timestamp"));
				if(maxVolume<row.getDouble("volume")){
					maxVolume=row.getDouble("volume");
					timeStamp=row.getInt("timestamp");
				}
			}
			
			//Conversion of the timestamp in to hh:mm:ss
			int difference_maxVolume=(timeStamp-starttimestamp);

			int hour_maxVolume=0;
			if ((difference_maxVolume)>3600){
				hour_maxVolume = (difference_maxVolume)/3600;
				difference_maxVolume = (difference_maxVolume)%3600;
			}
			int min_maxVolume =(difference_maxVolume)/60;
			int sec_maxVolume=(difference_maxVolume)%60;

			System.out.println("Max Volume traded for ticker: " + ticker
					+ " on " + date + " : " + maxVolume + " at "
					+ hour_maxVolume + "h " + min_maxVolume + "m " + sec_maxVolume + "s ");

			result=maxVolume + " at "
					+ hour_maxVolume + "h " + min_maxVolume + "m " + sec_maxVolume + "s ";
		}
		catch(Exception e){
		}
		session.close();
		
		//returning the result
		return result;
	}
}

