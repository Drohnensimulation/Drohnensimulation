package de.thi.hindernis;

import de.thi.hindernis.math.Vector3;

public class OverlapTester {
	
	public static Vector3 leftDownBack = new Vector3(0, 0, 0);
	public static Vector3 rightTopFront = new Vector3(0, 0, 0);

    public static boolean overlapSpheres(Sphere s1, Sphere s2) {
        double distance = s1.center.distSquared(s2.center);
        double radiusSum = s1.radius + s2.radius;
        return distance <= radiusSum * radiusSum;
    }
    
    public static boolean overlapCuboid(Cuboid c1, Cuboid c2) {
    	leftDownBack = getLeftDownBack(c1);
    	rightTopFront = getRightTopFront(c1);
        return  leftDownBack.x  < c2.center.x + c2.dimensions.x / 2 &&
                rightTopFront.x > c2.center.x - c2.dimensions.x / 2 &&
                leftDownBack.y  < c2.center.y + c2.dimensions.y / 2 &&
                rightTopFront.y > c2.center.y - c2.dimensions.y / 2 &&
                leftDownBack.z  < c2.center.z + c2.dimensions.z / 2 &&
                rightTopFront.z > c2.center.z - c2.dimensions.z / 2;
    }

    public static boolean overlapSphereCuboid(Sphere s, Cuboid c) {
        double closestX = s.center.x;
        double closestY = s.center.y;
        double closestZ = s.center.z;
    	leftDownBack = getLeftDownBack(c);
    	rightTopFront = getRightTopFront(c);

        if (s.center.x < leftDownBack.x) {
            closestX = leftDownBack.x;
        } else if(s.center.x > rightTopFront.x) {
            closestX = rightTopFront.x;
        }

        if (s.center.y < leftDownBack.y) {
            closestY = leftDownBack.y;
        } else if(s.center.y > rightTopFront.y) {
            closestY = rightTopFront.y;
        }

        if (s.center.z < leftDownBack.z) {
            closestZ = leftDownBack.z;
        } else if(s.center.z > rightTopFront.z) {
            closestZ = rightTopFront.z;
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
    	leftDownBack = getLeftDownBack(c);
    	rightTopFront = getRightTopFront(c);
        return  leftDownBack.x <= p.x && rightTopFront.x >= p.x &&
                leftDownBack.y <= p.y && rightTopFront.y >= p.y &&
                leftDownBack.z <= p.z && rightTopFront.z >= p.z;
    }
    
    public static boolean pointInCuboid(Cuboid c, float x, float y, float z) {
    	leftDownBack = getLeftDownBack(c);
    	rightTopFront = getRightTopFront(c);
        return  leftDownBack.x <= x && rightTopFront.x >= x &&
                leftDownBack.y <= y && rightTopFront.y >= y &&
                leftDownBack.z <= z && rightTopFront.z >= z;
    }
    
    public static Vector3 getLeftDownBack(Cuboid c) {
    	return new Vector3(c.center.x - c.dimensions.x / 2,
    					   c.center.y - c.dimensions.y / 2,
    					   c.center.z - c.dimensions.z / 2);
    }
    
    public static Vector3 getRightTopFront(Cuboid c) {
    	return new Vector3(c.center.x + c.dimensions.x / 2,
    					   c.center.y + c.dimensions.y / 2,
    					   c.center.z + c.dimensions.z / 2);
    }
}
