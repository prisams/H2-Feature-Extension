import java.util.ArrayList;
import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.Statement;
import com.datastax.driver.core.policies.DefaultRetryPolicy;
import com.datastax.driver.core.querybuilder.QueryBuilder;

/**
 * Class to retrieve the different tickers,
 * sectors and tickers per sector available
 *
 * @author Priyanka Samanta
 *
 * Date - 12-Nov-2015
 */
public class TickerTableMethods {

	/**Method to fetch all the available tickers
	 *
	 */
	public ArrayList<String> getListofTickers(){
		ResultSet results;
		//System.out.println("Fetching the tickers");

		Cluster cluster = Cluster.builder().addContactPoint("localhost").
				withRetryPolicy(DefaultRetryPolicy.INSTANCE).build();

		//Starting a session with the database
		Session session = cluster.connect("team3");

		//Get the list of all available tickers to populate the GUI
		Statement selection = QueryBuilder.select().column("ticker").from("stock");
		results=session.execute(selection);
		ArrayList<String> tickerList= new ArrayList<String>();
		for(Row row: results){
			tickerList.add(row.getString("ticker"));
			//System.out.printf("%s \n",row.getString("ticker"));
		}
		session.close();

		//return the list of tickers
		return tickerList;
	}

	/**Method to fetch all the available sectors in stock
	 *
	 */
	public ArrayList<String> getListofSectors(){
		ResultSet results;
		Cluster cluster = Cluster.builder().addContactPoint("localhost").
				withRetryPolicy(DefaultRetryPolicy.INSTANCE).build();

		//Starting a session with the database
		Session session = cluster.connect("team3");
		Statement selection = QueryBuilder.select().all().from("team3","sectors");

		//Firing query to retrieve all the available sectors
		results=session.execute(selection);
		ArrayList<String> sectors = new ArrayList<String>();
		for(Row row: results){
			sectors.add(row.getString("sector"));
		}
		session.close();

		//return the list of sectors to populate the GUI
		return sectors;
	}

	/**Method to fetch all the tickers based on sectors
	 *
	 */
	public ArrayList<String> getListofTickersOfSector(String sector){
		ResultSet results;
		Cluster cluster = Cluster.builder().addContactPoint("localhost").
				withRetryPolicy(DefaultRetryPolicy.INSTANCE).build();

		//Starting a session with the database
		Session session = cluster.connect("team3");

		//Building query to retrieve the ticker list based on sectors
		PreparedStatement select_statement = session.prepare(
				"SELECT TICKER,DESCRIPTION from STOCK where sector = ? ALLOW FILTERING;");

		BoundStatement bs = new BoundStatement(select_statement);

		//Firing the query to retrieve the tickers for the selected sector
		results=session.execute(bs.bind(sector));

		ArrayList<String> tickerList = new ArrayList<String>();
		for(Row row: results){
			String tabValue=row.getString("ticker") + 
					"\t" + ":" +"\t" + row.getString("description");
			tickerList.add(tabValue);
		}
		session.close();

		//return the ticker list based on sector to populate the GUI
		return tickerList;

	}
}