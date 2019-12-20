package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.neo4j.driver.Record;
import org.neo4j.driver.Session;
import org.neo4j.driver.StatementResult;



/**
 * create csv with https://github.com/c0ntradicti0n/allennlp_vs_ampligraph/blob/master/wiki_ampligraph.py
 * load this with 
 * 
 * 
 *LOAD CSV WITH HEADERS FROM 'file:////home/stefan/eclipse-workspace/hal/knowledge_graph_3d_choords.csv' AS line
   CREATE (:Node { 
	name: line.name, id:line.id, 
	x_pca:toFloat(line.x_pca), y_pca:toFloat(line.y_pca), z_pca:toFloat(line.z_pca), 
	x_tsne:toFloat(line.x_tsne), y_tsne:toFloat(line.y_tsne), z_tsne:toFloat(line.z_tsne), 
	x_k2:toFloat(line.x_k2), y_k2:toFloat(line.y_k2), z_k2:toFloat(line.z_k2)})
 * 
 * @param cypher
 * @return
 */


public class DBConnect {
	
	private static Logger jlog =  Logger.getLogger("Neo4j Connect");

	private static String user =  "neo4j";
	private static String password = "neo4j";
	private static String uri = "bolt://localhost:7687";

	private final static Driver driver = GraphDatabase.driver( uri, AuthTokens.basic( user, password ) );


	public ArrayList<Record> query(String cypher) {
		try (Session session = driver.session())
        {
            // Auto-commit transactions are a quick and easy way to wrap a read.
            StatementResult result = session.run(cypher);
            // Each Cypher execution returns a stream of records.
           
          ArrayList<Record> foundThings = new ArrayList<>();
            while (result.hasNext())
          {
                Record record = result.next();
                
                
                foundThings.add(record);
                // Values can be extracted from a record by index or name.
                
            }
        return foundThings;
        }
		
	}
	
}
