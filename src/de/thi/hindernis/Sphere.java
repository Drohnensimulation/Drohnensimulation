package de.thi.hindernis;

import de.thi.hindernis.math.Vector3;

public class Sphere extends Bounds {

    public final Vector3 center;
    public float radius;


    public Sphere(float x, float y, float z, float radius) {
        super();
        this.center = new Vector3(x, y, z);
        this.radius = radius;
    }
    
    public Sphere(Vector3 center, float radius) {
    	super();
    	this.center = center;
    	this.radius = radius;
    }
}
