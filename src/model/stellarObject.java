package model;

import org.neo4j.driver.Value;

import jMonkeyGod.Point3D;

public class stellarObject {
	public String name;
	public Point3D coords;
	public int cl_pca,	cl_tsne, cl_k2,	cl_kn;

	public stellarObject(String name, double x, double y, double z, 
			int cl_pca,	int cl_tsne, int cl_k2,	int cl_kn) {
        this.name =  name;
        coords =  new Point3D(x,y,z);
	}

	public stellarObject(Value value, String kind) {
		name    = value.get("name").asString();
        coords  =  new Point3D(
        		value.get("x_" + kind).asDouble(),
        		value.get("y_" + kind).asDouble(),
        		value.get("z_" + kind).asDouble());
        cl_pca  = value.get("cl_pca").asInt();
        cl_tsne = value.get("cl_tsne").asInt();
        cl_k2   = value.get("cl_k2").asInt();
        cl_kn   = value.get("cl_kn").asInt();
	}



	public stellarObject(CSVPoint p, String kind) {
		name    = p.getName();
        coords  =  p.getPointKind(kind);
        cl_pca  = p.getCl_pca();
        cl_tsne =p.getCl_tsne();
        cl_k2   = p.getCl_k2();
        cl_kn   =p.getCl_kn();
	}

	@Override
	public String toString() {
		return "stellarObject [name=" + name + ", coords=" + coords + "]";
	}
}
