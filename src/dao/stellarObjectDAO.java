package dao;

import java.util.ArrayList;
import java.util.logging.Logger;

import db.DBConnect;
import model.stellarObject;

public interface stellarObjectDAO {

	DBConnect gdb = new DBConnect();
	public ArrayList<stellarObject> findByName(String name);

}
