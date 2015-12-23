import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.policies.DefaultRetryPolicy;

/**
 * Class to delete stock data for a 
 * selected ticker from the database
 *  
 * @author Priyanka Samanta
 * @date 15 Nov 2015
 *
 */
public class DeleteData {
	public void deleteData(String ticker){
		
		//Staring the connection with the database.
		Cluster cluster = Cluster.builder().addContactPoint("localhost").
				withRetryPolicy(DefaultRetryPolicy.INSTANCE).build();
		
		//Connecting with the keyspace
		Session session = cluster.connect("team3");

		//Creating prepared statement to delete all data 
		//for the entered ticker
		PreparedStatement deleteData = session.prepare
				("delete from team3.dailystockdata where ticker=?;");

		BoundStatement bs1 = new BoundStatement(deleteData);

		//Firing the query
		session.execute(bs1.bind(ticker));

	}
}
