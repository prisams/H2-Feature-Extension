import java.util.List;
import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.policies.DefaultRetryPolicy;

/**
 * Class to update the description 
 * of the ticker
 *
 * @author Priyanka Samanta
 *
 * Date - 18-Nov-2015
 */

public class UpdateTicker {
	
	/**
	 * Update the description of the ticker
	 * @param ticker
	 * @param description
	 * @return
	 */
	public String updateTicker(String ticker, String description){
		ResultSet results;
		System.out.println("Updating the details");

		Cluster cluster = Cluster.builder().addContactPoint("localhost").
				withRetryPolicy(DefaultRetryPolicy.INSTANCE).build();
		
		//Starting a session with the database
		Session session = cluster.connect("team3");


		//Updating the data
		PreparedStatement getSector = session.prepare
				("SELECT sector from team3.stock where ticker=?");
		BoundStatement bs = new BoundStatement(getSector);
		
		//Firing the query to get the sector corresponding to the ticker
		results=session.execute(bs.bind(ticker));

		String sector = results.all().get(0).getString("sector");
		System.out.println("Sector: " + sector);

		//Update the ticker based on the sector and ticker which 
		//acts a partition key
		PreparedStatement updateTickerDescription = session.prepare
				("UPDATE stock set description=? where ticker=? and sector = ?;");

		BoundStatement bs1 = new BoundStatement(updateTickerDescription);
		results=session.execute(bs1.bind(description,ticker,sector));

		System.out.println("Retrieving the updated value");
		PreparedStatement retrieveUpdatedValue = session.prepare
				("select ticker,description from stock where ticker=?");

		//Fire the query to update the ticker
		BoundStatement bs2 = new BoundStatement(retrieveUpdatedValue);
		results=session.execute(bs2.bind(ticker));

		List<Row> row = results.all();
		String updateResult = row.get(0).getString("description");
		System.out.println("Ticker description upddated to : " + row.get(0).getString("ticker")
				+ " Desc: " + row.get(0).getString("description"));
		
		//Return the updated ticker description
		return updateResult;
	}
}

