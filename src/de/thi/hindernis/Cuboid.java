package de.thi.hindernis;

import de.thi.hindernis.math.Vector3;

public class Cuboid extends Bounds {

    public final Vector3 leftDownBack;
    public final Vector3 rightTopFront;


    public Cuboid(Vector3 leftDownBack, Vector3 rightTopFront) {
        super();
        this.leftDownBack = leftDownBack;
        this.rightTopFront = rightTopFront;
    }
    
    public Cuboid(float x1, float x2, float y1, float y2, float z1, float z2) {
    	super();
    	this.leftDownBack = new Vector3(x1, y1, z1);
    	this.rightTopFront = new Vector3(x2, y2, z2);
    }
    
    public Cuboid(Vector3 center, float width, float length, float height) {
    	super();
    	this.leftDownBack = new Vector3(center.x - width / 2,
    									center.y - length / 2,
    									center.z - height / 2);
    	this.rightTopFront = new Vector3(center.x + width / 2,
										 center.y + length / 2,
										 center.z + height / 2);
    }
}