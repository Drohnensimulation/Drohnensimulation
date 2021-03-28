package framework.math.bounds;

import framework.math.Vector2;

public class Rectangle extends Bounds {

    public final Vector2 lowerLeft;
    public float width, height;


    public Rectangle(float startX, float startY, float width, float height) {
        super();
        this.lowerLeft = new Vector2(startX, startY);
        this.width = width;
        this.height = height;
    }
}
