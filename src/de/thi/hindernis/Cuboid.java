package de.thi.hindernis;

import de.thi.hindernis.math.Vector3;

public class Cuboid extends Bounds {

    public final Vector3 center;
    public final Vector3 dimensions;
    public final Vector3 direction;

    
    public Cuboid(Vector3 center, Vector3 dimensions, Vector3 direction) {
    	super();
    	this.center = center;
    	this.dimensions = dimensions;
    	this.direction = direction;
    }
    
    public Cuboid(double x1, double x2, double y1, double y2, double z1, double z2) {
    	super();
    	this.center = new Vector3(x1 + (x2 - x1) / 2,
    							  y1 + (y2 - y1) / 2,
    							  z1 + (z2 - z1) / 2);
    	this.dimensions = new Vector3(x2 - x1, y2 - y1, z2 - z1);
    	this.direction = new Vector3(0, 1, 0);
    }
    
    public Cuboid(Vector3 center, double width, double length, double height) {
    	super();
    	this.center = center;
    	this.dimensions = new Vector3(width, length, height);
    	this.direction = new Vector3(0, 1, 0);
    }
}