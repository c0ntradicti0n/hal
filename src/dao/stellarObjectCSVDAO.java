package dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.neo4j.driver.Record;
import org.neo4j.driver.Value;
import org.neo4j.driver.exceptions.value.NotMultiValued;

import jMonkeyGod.Point3D;
import jMonkeyGod.StellarPoint;
import model.CSVPoint;
import model.ClusterCenter;
import model.stellarObject;
import util.CsvReader;

public class stellarObjectCSVDAO implements stellarObjectDAO {
	private  Logger jlog =  Logger.getLogger("stellarObjectNeo4j");
	
	String kind;

	private List<CSVPoint> map;


	static int limit;
	public static String limitStr;
	
	
	public stellarObjectCSVDAO(String _kind, int _limit, String path)  {
		kind = _kind;
        setLimit(_limit);
		map = CsvReader.readCSVPoints(path);
	}

    @Override
	public ArrayList<stellarObject> findByName(String name) {
		ArrayList<stellarObject> result = (ArrayList<stellarObject>) map.stream().
					filter(p -> p.getName() == name).
					map(p -> new stellarObject(p,  kind)).
					collect(Collectors.toList());
		return result;
	}

    @Override
	public HashMap<stellarObject, stellarObject> findByRel(String relation) {
		HashMap<stellarObject, stellarObject> result = new HashMap<>();
		return result;
	}

    @Override
	public List<stellarObject> findByPosition(StellarPoint local_pos, float radius) {
		System.out.println(local_pos);
		System.out.println(radius);

		ArrayList<stellarObject> result = (ArrayList<stellarObject>) map.stream().
				filter(p -> {
					Point3D p2 =  p.getPointKind(kind);
					return (
							Math.pow((p2.getX() - local_pos.getX()),2) +
							Math.pow((p2.getY() - local_pos.getY()),2) +
							Math.pow((p2.getZ() - local_pos.getZ()),2) 
							) < radius;
				}).
				map(p -> new stellarObject(p,  kind)).
				collect(Collectors.toList());
		System.out.println(result);

		return result;
	}

    @Override
	public void tryAdd(ArrayList<stellarObject> result, Value value, String kind2)  {
		try  {

			result.add(new stellarObject(value, kind));	
		}
		catch(NotMultiValued e)  {
			System.out.println ("Result not convertable to point " + value + " " + kind);
		}
	}

    @Override
	public void setLimit(int _limit) {
		limit = _limit;
		limitStr = " LIMIT "+limit;
	}
	
	
	
 
}
