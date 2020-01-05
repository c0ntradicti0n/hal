package dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

import org.neo4j.driver.Value;

import db.DBConnect;
import jMonkeyGod.StellarPoint;
import model.stellarObject;

public interface stellarObjectDAO {

	DBConnect gdb = new DBConnect();
	public ArrayList<stellarObject> findByName(String name);
	HashMap<stellarObject, stellarObject> findByRel(String relation);
	List<stellarObject> findByPosition(StellarPoint local_pos, float radius);
	void tryAdd(ArrayList<stellarObject> result, Value value, String kind2);
	void setLimit(int _limit);

}
