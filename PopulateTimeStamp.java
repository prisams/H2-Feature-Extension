import java.io.BufferedReader;
import java.io.FileReader;
import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.policies.DefaultRetryPolicy;

/**
 * Class to populate the timestamp 
 * table in the database with the 
 * new timestamp entries obtained while downloading
 * data from the website
 *
 * @author Priyanka Samanta
 *
 * Date - 14-Nov-2015
 */

public class PopulateTimeStamp {

	//To insert data in the timestamp table in the database
	public void dumpDataInDatabase(){
		Cluster cluster = Cluster.builder().addContactPoint("localhost").
				withRetryPolicy(DefaultRetryPolicy.INSTANCE).build();
	
		//Starting a session with the database
		Session session = cluster.connect("team3");

		//Prepared statement to insert data in the timestamp table
		PreparedStatement insert_timestamp = session.prepare(
				"INSERT INTO team3.TIMESTAMP (dateasnumbers,start_timestamp,end_timestamp)"
						+ "VALUES(?,?,?);");

		BoundStatement bs2 = new BoundStatement(insert_timestamp);

		//reading data from file and populating the database table
		try{
			BufferedReader input = new BufferedReader(new FileReader("ticker.txt"));
			String lineRead;
			while((lineRead=input.readLine())!=null){

				//looking for the range timestamp
				if(lineRead.contains("range:")){
					String line1=lineRead.replace("range:", "");
					String[] timestamp=line1.split(",");

					//populating timestamp table
					session.execute(bs2.bind(Integer.parseInt(timestamp[0]),
							Integer.parseInt(timestamp[1]),
							Integer.parseInt(timestamp[2])));
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