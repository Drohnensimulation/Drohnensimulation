package framework.math.bounds;

import framework.math.Vector2;

public class Circle extends Bounds {

    public final Vector2 center;
    public float radius;


    public Circle(float x, float y, float radius) {
        super();
        this.center = new Vector2(x, y);
        this.radius = radius;
    }
}
