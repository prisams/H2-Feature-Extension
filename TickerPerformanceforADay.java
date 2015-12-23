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
 * Class to compute the performance of
 * a selected ticker on a selected date.
 * By performance we mean, the highest and
 * lowest price of the selected ticker for the
 * chosen date.
 *
 * @author Priyanka Samanta
 *
 * Date - 14-Nov-2015
 */

public class TickerPerformanceforADay {
	
	/**
	 * To get the highest and lowest price of the selected
	 * ticker for the selected date
	 * 
	 * @param ticker
	 * @param date
	 * @return
	 */
	public ArrayList<String> getTickerPerformance(String ticker,String date){

		int dateForQuery = Integer.parseInt(date.replace("-", ""));
		ResultSet results;
		ArrayList<String> result = new ArrayList<String>();
		System.out.println("Fetching the details");

		Cluster cluster = Cluster.builder().addContactPoint("localhost").
				withRetryPolicy(DefaultRetryPolicy.INSTANCE).build();
		
		//Opening a session with the database
		Session session = cluster.connect("team3");

		//Getting the range of timestamp for the entered date
		PreparedStatement selectTimestampRange = session.prepare
				("select start_timestamp,end_timestamp from "
						+ "team3.timestamp where dateasnumbers=?;");

		BoundStatement bs1 = new BoundStatement(selectTimestampRange);
		try{
			
			//firing the query to get the timestamp range for the selected date
			results=session.execute(bs1.bind(dateForQuery));

			List<Row> time = results.all();
			int starttimestamp = time.get(0).getInt("start_timestamp");
			int endtimestamp = time.get(0).getInt("end_timestamp");


			System.out.println("Time: " + starttimestamp + " : " + endtimestamp);

			//Getting the low and high price for the ticker for the particular date
			PreparedStatement selectRange = session.prepare
					("select low_price,high_price,timestamp from team3.dailystockdata "
							+ "where timestamp >=? "
							+ "and timestamp<=? and ticker=? ALLOW FILTERING;");

			BoundStatement bs2 = new BoundStatement(selectRange);
			ResultSet result2;
			result2=session.execute(bs2.bind(starttimestamp,endtimestamp,ticker));
			
			//declaring the data structure to store the result
			ArrayList<Double> lowprice = new ArrayList<Double>();
			ArrayList<Double> highprice = new ArrayList<Double>();
			ArrayList<Integer> timestamp = new ArrayList<Integer>();

			for(Row row: result2){
				lowprice.add(row.getDouble("low_price"));
				highprice.add(row.getDouble("high_price"));
				timestamp.add(row.getInt("timestamp"));
			}

			//find the lowest and highest price for the day
			double finalLow = Double.MAX_VALUE,finalHigh=Double.MIN_VALUE;
			int indexLow=0,indexHigh=0;

			for(int i=0;i<lowprice.size();i++){
				if(finalLow>lowprice.get(i)){
					finalLow=lowprice.get(i);
					indexLow=i;
				}
				if(finalHigh<highprice.get(i)){
					finalHigh=highprice.get(i);
					indexHigh=i;
				}
			}
			//computing timestamp in terms of hh:mm:ss
			int difference_highPrice=(timestamp.get(indexHigh)-starttimestamp);
			int difference_lowPrice=(timestamp.get(indexLow)-starttimestamp);

			int hour_high=0, hour_low=0;
			if ((difference_highPrice)>3600){
				hour_high = (difference_highPrice)/3600;
				difference_highPrice = (difference_highPrice)%3600;
			}
			int min_high =(difference_highPrice)/60;
			int sec_high=(difference_highPrice)%60;


			if ((difference_lowPrice)>3600){
				hour_low = (difference_lowPrice)/3600;
				difference_lowPrice = (difference_lowPrice)%3600;
			}
			int min_low =(difference_lowPrice)/60;
			int sec_low=(difference_lowPrice)%60;


			System.out.println("For the date: " + date);
			System.out.println("Low Price : " + finalLow + " at " + hour_low + "h " + min_low + "m " + sec_low + "s ");
			System.out.println("High Price: " + finalHigh + " at " + hour_high + "h " + min_high + "m " + sec_high + "s ");
			String min= "Low Price: " + finalLow + "$" + " at " + hour_low + "h " + min_low + "m " + sec_low + "s ";
			String max= "High Price: " + finalHigh + "$" + " at " + hour_high + "h " + min_high + "m " + sec_high + "s ";
			result.add(min);result.add(max);
		}
		catch(Exception e){
			System.out.println("No data present in the database for your query");
		}
		session.close();
		
		//returning the result
		return result;
	}
}

