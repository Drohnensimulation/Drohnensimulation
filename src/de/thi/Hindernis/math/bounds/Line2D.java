package framework.math.bounds;

import framework.math.Vector2;

public class Line2D extends Bounds {

    public final Vector2 start;
    public final Vector2 end;


    public Line2D(float x1, float y1, float x2, float y2) {
        super();
        start = new Vector2(x1, y1);
        end = new Vector2(x2, y2);
    }

    public Line2D(Vector2 start, Vector2 end) {
        super();
        this.start = start;
        this.end = end;
    }
}
