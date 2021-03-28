package framework.math.bounds;

import framework.math.Vector3;

public class Sphere extends Bounds {

    public final Vector3 center;
    public float radius;


    public Sphere(float x, float y, float z, float radius) {
        super();
        this.center = new Vector3(x, y, z);
        this.radius = radius;
    }
}
