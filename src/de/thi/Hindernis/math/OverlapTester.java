package framework.math;

import framework.math.bounds.Circle;
import framework.math.bounds.Line2D;
import framework.math.bounds.Rectangle;
import framework.math.bounds.Sphere;

public class OverlapTester {

    public static boolean overlapCircles(Circle c1, Circle c2) {
        float distance = c1.center.distSquared(c2.center);
        float radiusSum = c1.radius + c2.radius;
        return distance <= radiusSum * radiusSum;
    }

    public static boolean overlapRectangles(Rectangle r1, Rectangle r2) {
        return r1.lowerLeft.x < r2.lowerLeft.x + r2.width &&
                r1.lowerLeft.x + r1.width > r2.lowerLeft.x &&
                r1.lowerLeft.y < r2.lowerLeft.y + r2.height &&
                r1.lowerLeft.y + r1.height > r2.lowerLeft.y;
    }

    public static boolean overlapCircleRectangle(Circle c, Rectangle r) {
        float closestX = c.center.x;
        float closestY = c.center.y;

        if (c.center.x < r.lowerLeft.x) {
            closestX = r.lowerLeft.x;
        } else if(c.center.x > r.lowerLeft.x + r.width) {
            closestX = r.lowerLeft.x + r.width;
        }


        if (c.center.y < r.lowerLeft.y) {
            closestY = r.lowerLeft.y;
        } else if(c.center.y > r.lowerLeft.y + r.height) {
            closestY = r.lowerLeft.y + r.height;
        }

        return c.center.distSquared(closestX, closestY) < c.radius * c.radius;
    }

    public static boolean overlapSpheres(Sphere s1, Sphere s2) {
        float distance = s1.center.distSquared(s2.center);
        float radiusSum = s1.radius + s2.radius;
        return distance <= radiusSum * radiusSum;
    }

    public static boolean overlapLines2D(Line2D s1, Line2D s2) {

        float a1 = s1.end.y - s1.start.y;
        float b1 = s1.start.x - s1.end.x;
        float c1 = a1 * s1.start.x + b1 * s1.start.y;

        float a2 = s2.end.y - s2.start.y;
        float b2 = s2.start.x - s2.end.x;
        float c2 = a2 * s2.start.x + b2 * s2.start.y;

        float det = a1 * b2 - a2 * b1;

        if (det == 0) return false;

        else {
            float x = (c1 * b2 - b1 * c2) / det;
            float y = (a1 * c2 - c1 * a2) / det;

            return    (x >= Math.min(s1.start.x, s1.end.x) && x <= Math.max(s1.start.x, s1.end.x)
                    && x >= Math.min(s2.start.x, s2.end.x) && x <= Math.max(s2.start.x, s2.end.x)
                    && y >= Math.min(s1.start.y, s1.end.y) && y <= Math.max(s1.start.y, s1.end.y)
                    && y >= Math.min(s2.start.y, s2.end.y) && y <= Math.max(s2.start.y, s2.end.y));
        }
    }
    public static boolean overlapLines2D(Vector2 s1Start, Vector2 s1End,
                                         Vector2 s2Start, Vector2 s2End) {

        float a1 = s1End.y - s1Start.y;
        float b1 = s1Start.x - s1End.x;
        float c1 = a1 * s1Start.x + b1 * s1Start.y;

        float a2 = s2End.y - s2Start.y;
        float b2 = s2Start.x - s2End.x;
        float c2 = a2 * s2Start.x + b2 * s2Start.y;

        float det = a1 * b2 - a2 * b1;

        if (det == 0) return false;

        else {
            float x = (c1 * b2 - b1 * c2) / det;
            float y = (a1 * c2 - c1 * a2) / det;

            return    (x >= Math.min(s1Start.x, s1End.x) && x <= Math.max(s1Start.x, s1End.x)
                    && x >= Math.min(s2Start.x, s2End.x) && x <= Math.max(s2Start.x, s2End.x)
                    && y >= Math.min(s1Start.y, s1End.y) && y <= Math.max(s1Start.y, s1End.y)
                    && y >= Math.min(s2Start.y, s2End.y) && y <= Math.max(s2Start.y, s2End.y));
        }
    }
    public static boolean overlapLines2D(float x1, float y1, float x2, float y2,
                                                float x3, float y3, float x4, float y4) {
        float a1 = y2 - y1;
        float b1 = x1 - x2;
        float c1 = a1 * x1 + b1 * y1;

        float a2 = y4 - y3;
        float b2 = x3 - x4;
        float c2 = a2 * x3 + b2 * y3;

        float det = a1 * b2 - a2 * b1;

        if (det == 0) return false;

        else {
            float x = (c1 * b2 - b1 * c2) / det;
            float y = (a1 * c2 - c1 * a2) / det;

            return    (x >= Math.min(x1, x2) && x <= Math.max(x1, x2)
                    && x >= Math.min(x3, x4) && x <= Math.max(x3, x4)
                    && y >= Math.min(y1, y2) && y <= Math.max(y1, y2)
                    && y >= Math.min(y3, y4) && y <= Math.max(y3, y4));
        }
    }

    public static boolean overlapCircleLine2D(Circle c, Line2D s){

        float A1 = s.end.y - s.start.y;
        float B1 = s.start.x - s.end.x;
        float C1 = A1 * s.start.x + B1 * s.start.y;
        float C2 = -B1 * c.center.x + A1 * c.center.y;

        float det = A1 * A1 - (-B1 * B1);

        if(det == 0) return false;
        else
            return OverlapTester.pointInCircle(c,
                    (A1 * C1 -   B1 * C2 ) / det,
                    (A1 * C2 - (-B1 * C1)) / det);
    }
    public static boolean overlapCircleLine2D(Circle c, Vector2 start, Vector2 end) {

        float A1 = end.y - start.y;
        float B1 = start.x - end.x;
        float C1 = A1 * start.x + B1 * start.y;
        float C2 = -B1 * c.center.x + A1 * c.center.y;

        float det = A1 * A1 - (-B1 * B1);

        if(det == 0) return false;
        else
            return OverlapTester.pointInCircle(c,
                    (A1 * C1 -   B1 * C2 ) / det,
                    (A1 * C2 - (-B1 * C1)) / det);
    }
    public static boolean overlapCircleLine2D(Circle c, float lx1, float ly1, float lx2, float ly2){

        float A1 = ly2 - ly1;
        float B1 = lx1 - lx2;
        float C1 = (ly2 - ly1) * lx1 + (lx1 - lx2) * ly1;
        float C2 = -B1 * c.center.x + A1 * c.center.y;

        float det = A1 * A1 - (-B1 * B1);

        if(det == 0) return false;
        else
            return OverlapTester.pointInCircle(c,
                    (A1 * C1 -   B1 * C2 ) / det,
                    (A1 * C2 - (-B1 * C1)) / det);
    }

    public static boolean pointInCircle(Circle c, Vector2 p) {
        return c.center.distSquared(p) <= c.radius * c.radius;
    }
    public static boolean pointInCircle(Circle c, float x, float y) {
        return c.center.distSquared(x, y) <= c.radius * c.radius;
    }

    public static boolean pointInRectangle(Rectangle r, Vector2 p) {
        return  r.lowerLeft.x <= p.x && r.lowerLeft.x + r.width >= p.x &&
                r.lowerLeft.y <= p.y && r.lowerLeft.y + r.height >= p.y;
    }
    public static boolean pointInRectangle(Rectangle r, float x, float y) {
        return  r.lowerLeft.x <= x && r.lowerLeft.x + r.width >= x &&
                r.lowerLeft.y <= y && r.lowerLeft.y + r.height >= y;
    }

    public static boolean pointInSphere(Sphere s, Vector3 p) {
        return s.center.distSquared(p) <= s.radius * s.radius;
    }
    public static boolean pointInSphere(Sphere s, float x, float y, float z) {
        return s.center.distSquared(x, y, z) <= s.radius * s.radius;
    }
}
