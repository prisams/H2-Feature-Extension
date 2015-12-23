import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.policies.DefaultRetryPolicy;

/**
 * Class to compute and return the ticker
 * that has performed best in the selected sector, 
 * in terms of the maximum volume traded.
 *  
 * @author Priyanka Samanta
 * @date 15 Nov 2015
 *
 */
public class BestTickerPerformanceInASector {

	public String bestTickerInSector(String sector, String date){
		//Converting date to the integer format
		int dateForQuery = Integer.parseInt(date.replace("-", ""));

		//Declaring result-set to store the value
		ResultSet results;

		//Declaring result to return the value to the UI
		String result;
		System.out.println("Fetching the details");

		//Opening the connection with the database
		Cluster cluster = Cluster.builder().addContactPoint("localhost").
				withRetryPolicy(DefaultRetryPolicy.INSTANCE).build();
		
		//Connecting with the keyspace
		Session session = cluster.connect("team3");

		//Getting the tickers for the entered sector
		PreparedStatement selectTickerForSector = session.prepare
				("select ticker,description from team3.stock"
						+ " where sector=? ALLOW FILTERING;");

		BoundStatement bs1 = new BoundStatement(selectTickerForSector);

		//Firing the query and storing the result
		results=session.execute(bs1.bind(sector));
		ArrayList<String> tickerNDesc = new ArrayList<String>();

		for (Row row: results){
			tickerNDesc.add(row.getString("ticker"));
			tickerNDesc.add(row.getString("description"));
		}

		//For logging
		for (int i=0;i<tickerNDesc.size()/2;i++){
			System.out.println(tickerNDesc.get(2*i) + ":" + tickerNDesc.get(2*i+1));
		}

		//Getting the timeStamp for the entered day
		PreparedStatement selectTimestampRange = session.prepare
				("select start_timestamp,end_timestamp from team3.timestamp where dateasnumbers=?;");

		BoundStatement bs2 = new BoundStatement(selectTimestampRange);
		results=session.execute(bs2.bind(dateForQuery));

		List<Row> time = results.all();

		//Storing the timestamp
		int starttimestamp = time.get(0).getInt("start_timestamp");
		int endtimestamp = time.get(0).getInt("end_timestamp");

		//Logging timestamp
		System.out.println("Time: " + starttimestamp + " : " + endtimestamp);

		//Fetching the volume of every ticker traded within
		//the obtained timestamp for the selected sector
		HashMap<String,Double> maxVolumeForEachTicker =
				new HashMap<String,Double>();
		for(int i=0;i<tickerNDesc.size()/2;i++){
			String tickerForQuery=tickerNDesc.get(2*i);

			//Creating the prepared statement
			PreparedStatement selectVolume = session.prepare
					("select volume from team3.dailystockdata "
							+ "where ticker=? and timestamp >=? "
							+ "and timestamp<=? ALLOW FILTERING;");

			BoundStatement bs3 = new BoundStatement(selectVolume);
			
			//Firing the query
			results=session.execute(bs3.bind(tickerForQuery,starttimestamp,endtimestamp));
			ArrayList<Double> volume= new ArrayList<Double>();

			double maxVolume = Double.MIN_VALUE;
			for(Row row: results){
				volume.add(row.getDouble("volume"));
				if(maxVolume<row.getDouble("volume")){
					maxVolume=row.getDouble("volume");
				}
			}
			maxVolumeForEachTicker.put(tickerForQuery, maxVolume);
		}
		System.out.println("HashMap size: " + maxVolumeForEachTicker.size());


		//Compute the best ticker in the sector based 
		//on the max volume
		double maxValue= Double.MIN_VALUE;
		String bestTicker=null;
		for (HashMap.Entry<String,Double> entry : maxVolumeForEachTicker.entrySet()) {

			if(maxValue<entry.getValue()){
				maxValue=entry.getValue();
				bestTicker=entry.getKey();
			}
		}

		//Closing the session
		session.close();

		//Logging the result
		System.out.println(bestTicker + " performed the best with the max  volume traded as: " + maxValue);

		//returning the best ticker to the UI
		result= bestTicker;
		return result;
	}
}