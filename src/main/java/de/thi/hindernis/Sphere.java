package de.thi.hindernis;

import de.thi.hindernis.math.Vector3;

public class Sphere extends Bounds {

    public final Vector3 center;
    public double radius;


    public Sphere(double x, double y, double z, double radius) {
        super();
        this.center = new Vector3(x, y, z);
        this.radius = radius;
    }
    
    public Sphere(Vector3 center, double radius) {
    	super();
    	this.center = center;
    	this.radius = radius;
    }
}
