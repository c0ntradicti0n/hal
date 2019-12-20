package dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

import org.neo4j.driver.Record;

import jMonkeyGod.StellarPoint;
import model.stellarObject;

public class stellarObjectNeo4jDAO implements stellarObjectDAO {
	private  Logger jlog =  Logger.getLogger("stellarObjectNeo4j");
	
	String kind;
	
	public stellarObjectNeo4jDAO(String _kind)  {
		kind = _kind;
	}


	public ArrayList<stellarObject> findByName(String name) {
		String query =  "MATCH (n) " +
	              "WHERE n.name  =~ \""+name+".*\" " +
				  "RETURN n";
		jlog.info(query);
		
		ArrayList<Record> res = gdb.query(query);

		ArrayList<stellarObject> result = new ArrayList<>();
		jlog.info(
				query.toString() + " -> [" + 
		       ((Record) res.toArray()[0]).keys().toArray()[0].toString() + 
		       "]{" +
		       ((Record) res.toArray()[0]).get(
		    		   			((Record) res.toArray()[0]).keys().toArray()[0].toString()
		    		   ).keys().toString() + 
		       "}"

		       );
		res.stream().forEach(i -> result.add(new stellarObject(
				i.get("n").get("name").asString(), 
				i.get("n").get("x_" + kind).asDouble(), 
				i.get("n").get("y_" + kind).asDouble(), 
				i.get("n").get("z_" + kind).asDouble()
				))
		);
		
		return result;
	}


	public HashMap<stellarObject, stellarObject> findByRel(String relation) {
		String query =  "MATCH (n1)-[:R {kind:'"+relation+"'}]-(n2) WHERE id(n1)>id(n2) " +
				  "RETURN n1, n2";
		jlog.info(query);
		
		ArrayList<Record> res = gdb.query(query);
		HashMap<stellarObject, stellarObject> result = new HashMap<>();

		if (res.isEmpty())  {
			jlog.warning("no relation present in DB");
			return result;
		}
		jlog.info(
				query.toString() + " -> [" + 
		       ((Record) res.toArray()[0]).keys().toArray()[0].toString() + 
		       "]{" +
		       ((Record) res.toArray()[0]).get(
		    		   			((Record) res.toArray()[0]).keys().toArray()[0].toString()
		    		   ).keys().toString() + 
		       "}"

		       );
		res.stream().forEach(i -> System.out.println(i));
		res.stream().forEach(i -> result.put(new stellarObject(
				i.get("n1").get("name").asString(), 
				i.get("n1").get("x_" + kind).asDouble(), 
				i.get("n1").get("y_" + kind).asDouble(), 
				i.get("n1").get("z_" + kind).asDouble()
				),
				new stellarObject(
				i.get("n2").get("name").asString(), 
				i.get("n2").get("x_" + kind).asDouble(), 
				i.get("n2").get("y_" + kind).asDouble(), 
				i.get("n2").get("z_" + kind).asDouble()
				))
		);
		
		return result;
	}


	public List<stellarObject> findByPosition(StellarPoint local_pos, float radius) {
		String query =  "MATCH (n) " +
	              "WHERE (n.x_"+kind+"-"+local_pos.getX() +")^2 + " +
	                    "(n.y_"+kind+"-"+local_pos.getY() +")^2 + " +
	                    "(n.z_"+kind+"-"+local_pos.getZ() +")^2 " +
	                    " < " + radius +
				  " RETURN n";
		jlog.info(query);
		
		ArrayList<Record> res = gdb.query(query);
		ArrayList<stellarObject> result = new ArrayList<>();

		if (res.isEmpty())  {
			jlog.warning("no relation present in DB");
			return result;
		}
		jlog.info(
				query.toString() + " -> [" + 
		       ((Record) res.toArray()[0]).keys().toArray()[0].toString() + 
		       "]{" +
		       ((Record) res.toArray()[0]).get(
		    		   			((Record) res.toArray()[0]).keys().toArray()[0].toString()
		    		   ).keys().toString() + 
		       "}"

		       );
		res.stream().forEach(i -> result.add(new stellarObject(
				i.get("n").get("name").asString(), 
				i.get("n").get("x_" + kind).asDouble(), 
				i.get("n").get("y_" + kind).asDouble(), 
				i.get("n").get("z_" + kind).asDouble()
				))
		);
		
		return result;
	}

	
 
}
