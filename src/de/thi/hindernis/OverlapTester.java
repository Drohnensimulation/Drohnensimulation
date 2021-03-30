package de.thi.hindernis;

import de.thi.hindernis.math.Vector3;

public class OverlapTester {

    public static boolean overlapSpheres(Sphere s1, Sphere s2) {
        float distance = s1.center.distSquared(s2.center);
        float radiusSum = s1.radius + s2.radius;
        return distance <= radiusSum * radiusSum;
    }
    
    public static boolean overlapCuboid(Cuboid c1, Cuboid c2) {
        return  c1.leftDownBack.x  < c2.rightTopFront.x &&
                c1.rightTopFront.x > c2.leftDownBack.x  &&
                c1.leftDownBack.y  < c2.rightTopFront.y &&
                c1.rightTopFront.y > c2.leftDownBack.y  &&
                c1.leftDownBack.z  < c2.rightTopFront.z &&
                c1.rightTopFront.z > c2.leftDownBack.z;
    }

    public static boolean overlapSphereCuboid(Sphere s, Cuboid c) {
        float closestX = s.center.x;
        float closestY = s.center.y;
        float closestZ = s.center.z;

        if (s.center.x < c.leftDownBack.x) {
            closestX = c.leftDownBack.x;
        } else if(s.center.x > c.rightTopFront.x) {
            closestX = c.rightTopFront.x;
        }

        if (s.center.y < c.leftDownBack.y) {
            closestY = c.leftDownBack.y;
        } else if(s.center.y > c.rightTopFront.y) {
            closestY = c.rightTopFront.y;
        }

        if (s.center.z < c.leftDownBack.z) {
            closestZ = c.leftDownBack.z;
        } else if(s.center.z > c.rightTopFront.z) {
            closestZ = c.rightTopFront.z;
        }

        return s.center.distSquared(closestX, closestY, closestZ) < s.radius * s.radius;
    }

    public static boolean pointInSphere(Sphere s, Vector3 p) {
        return s.center.distSquared(p) <= s.radius * s.radius;
    }
    
    public static boolean pointInSphere(Sphere s, float x, float y, float z) {
        return s.center.distSquared(x, y, z) <= s.radius * s.radius;
    }

    public static boolean pointInCuboid(Cuboid c, Vector3 p) {
        return  c.leftDownBack.x <= p.x && c.rightTopFront.x >= p.x &&
                c.leftDownBack.y <= p.y && c.rightTopFront.y >= p.y &&
                c.leftDownBack.z <= p.z && c.rightTopFront.z >= p.z;
    }
    
    public static boolean pointInCuboid(Cuboid c, float x, float y, float z) {
        return  c.leftDownBack.x <= x && c.rightTopFront.x >= x &&
                c.leftDownBack.y <= y && c.rightTopFront.y >= y &&
                c.leftDownBack.z <= z && c.rightTopFront.z >= z;
    }
}
