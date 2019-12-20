package jMonkeyGod;

import com.jme3.math.Vector3f;

import util.Config;

public class StellarPoint{
	private float uX, uY, uZ;
	private double x,y,z;

	public StellarPoint(double _x, double _y, double _z) {
		x = _x;
		y = _y;
		z = _z;
		setuX(this.getX());
		setuY(this.getY());
		setuZ(this.getZ()); 
	}
	public StellarPoint(Point3D pNorm) {
        x = pNorm.getX();
        y = pNorm.getY();
        z = pNorm.getZ();

		setuX(this.getX());
		setuY(this.getY());
		setuZ(this.getZ());   
	}
	
	public StellarPoint(Vector3f rPos) {
		uX = rPos.x;
		uY = rPos.y;
		uZ = rPos.z;
		setx(uX);
		sety(uY);
		setz(uZ);
	}
	
	
	public float getuX() {
		return uX;
	}
	private void setuX(double x) {
		this.uX = (float) (x * Config.UNIVERSEZOOM) /* -Config.UNIVERSEZOOM*/;
	}
	public float getuY() {
		return uY;
	}
	private void setuY(double y) {
		this.uY =  (float) (y * Config.UNIVERSEZOOM) /* -Config.UNIVERSEZOOM*/;
	}
	public float getuZ() {
		return uZ;
	}
	private void setuZ(double z) {
		this.uZ = (float) (z * Config.UNIVERSEZOOM) /* -Config.UNIVERSEZOOM*/;
	}
	
	public Vector3f getVector()  {
		return new Vector3f(
        		getuX(), 
        		getuY(), 
        		getuZ()
        		);
	}
	public double getX() {
		return x;
	}
	public void setX(float x) {
		this.x = x;
	}
	private void setx(double uX) {
		x = (float) (uX /* -Config.UNIVERSEZOOM*/) / Config.UNIVERSEZOOM;
	}
	public double getY() {
		return y;
	}
	public void sety(double uY) {
		y = (float) (uY/* -Config.UNIVERSEZOOM*/) / Config.UNIVERSEZOOM;
	}
	public double getZ() {
		return z;
	}
	public void setz(double uZ) {
		z = (float) (uZ /* -Config.UNIVERSEZOOM*/) / Config.UNIVERSEZOOM;
	}
	@Override
	public String toString() {
		return "StellarPoint [uX=" + uX + ", uY=" + uY + ", uZ=" + uZ + ", x=" + x + ", y=" + y + ", z=" + z + "]";
	}
}
