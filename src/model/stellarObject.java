package model;

import jMonkeyGod.Point3D;

public class stellarObject {
	public String name;
	public Point3D coords;

	public stellarObject(String name, double x, double y, double z) {
        this.name =  name;
        coords =  new Point3D(x,y,z);
	}

	@Override
	public String toString() {
		return "stellarObject [name=" + name + ", coords=" + coords + "]";
	}

}
