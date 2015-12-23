import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.policies.DefaultRetryPolicy;

/**
 * Class to populate the static tables 
 * ticker and sector in the database
 *
 * @author Priyanka Samanta
 *
 * Date - 15-Nov-2015
 */
public class PopulateS_PIndex {
	/**
	 * Populate the ticker and sector table with the data in the Ticker.txt file
	 * @return
	 */
	public void populateIndexTable(){

		//Code to insert index data in the stock static table
		System.out.print("Enter the data file: ");
		Scanner scan = new Scanner(System.in);
		String fileName = scan.nextLine();
		scan.close();
		System.out.println("Populating stock table.....");
		Cluster cluster = Cluster.builder().addContactPoint("localhost").
				withRetryPolicy(DefaultRetryPolicy.INSTANCE).build();
		Session session = cluster.connect("team3");

		//Prepared statement to insert data in the stock table

		PreparedStatement insert_statement = session.prepare(
				"INSERT INTO STOCK" + "(ticker,description,sector)"
						+ "VALUES(?,?,?);");

		//Prepared statement to insert data in the sector table
		PreparedStatement insert_statement_sector = session.prepare(
				"INSERT INTO SECTORS" + "(sector)"
						+ "VALUES(?);");

		BoundStatement bs1 = new BoundStatement(insert_statement);
		BoundStatement bs2 = new BoundStatement(insert_statement_sector);


		//reading data from file
		try{
			BufferedReader in = new BufferedReader(new FileReader(fileName));
			String line;
			while((line=in.readLine())!=null){
				String[] record = line.split(",");
				String ticker=record[0];
				String desc=record[1];
				String sector=record[2];
				session.execute(bs1.bind(ticker,desc,sector));
				session.execute(bs2.bind(sector));

			}
			in.close();
		}
		catch(IOException e){
			System.out.println("Incorrect file");
		}
		System.out.println("Data inserted successfully!");
		cluster.close();
		session.close();
	}
}
