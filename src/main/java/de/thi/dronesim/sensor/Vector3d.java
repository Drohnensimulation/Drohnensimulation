package de.thi.dronesim.sensor;

public class Vector3d {
	private final double x;
	private final double y;
	private final double z;
	
	public Vector3d(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public double getX() {
		return this.x;
	}
	
	public double getY() {
		return this.y;
	}
	
	public double getZ() {
		return this.z;
	}
	
	public Vector3d copy() {
		return new Vector3d(this.getX(), this.getY(), this.getZ());
	}
	
	@Override
	public String toString() {
		return "(" + this.x + ", " + this.y + ", " + this.z + ")";
	}
}
