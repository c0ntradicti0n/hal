package jMonkeyGod;

public class Point3D {
	private double x, y, z;

	public Point3D(double _x, double _y, double _z) {
        x = _x;
        y = _y;
        z = _z;
	}

	@Override
	public String toString() {
		return "Point3D [x=" + x + ", y=" + y + ", z=" + z + "]";
	}

	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}

	public double getZ() {
		return z;
	}

	public void setZ(double z) {
		this.z = z;
	}

}
