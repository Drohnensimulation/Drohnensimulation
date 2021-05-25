package de.thi.dronesim.drone;

public class Vector3 {

    public double x, y, z;


    public Vector3() {
        this.x = 0;
        this.y = 0;
        this.z = 0;
    }

    public Vector3(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vector3(Vector3 other) {
        this.x = other.x;
        this.y = other.y;
        this.z = other.z;
    }

    public Vector3 cpy() {
        return new Vector3(x, y, z);
    }

    public Vector3 set(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
        return this;
    }


    public Vector3 set(Vector3 other) {
        this.x = other.x;
        this.y = other.y;
        this.z = other.z;
        return this;
    }

    public Vector3 add(double x, double y, double z) {
        this.x += x;
        this.y += y;
        this.z += z;
        return this;
    }


    public Vector3 add(Vector3 other) {
        this.x += other.x;
        this.y += other.y;
        this.z += other.z;
        return this;
    }

    public Vector3 sub(double x, double y, double z) {
        this.x -= x;
        this.y -= y;
        this.z -= z;
        return this;
    }

    public Vector3 sub(Vector3 other) {
        this.x -= other.x;
        this.y -= other.y;
        this.z -= other.z;
        return this;
    }

    public Vector3 mul(double scalar) {
        this.x *= scalar;
        this.y *= scalar;
        this.z *= scalar;
        return this;
    }

    public double len() {
        return Math.sqrt(x * x + y * y + z * z);
    }

    public Vector3 nor() {
        double len = len();
        if (len != 0) {
            this.x /= len;
            this.y /= len;
            this.z /= len;
        }
        return this;
    }

    public double dist(double x, double y, double z) {
        double distX = this.x - x;
        double distY = this.y - y;
        double distZ = this.z - z;
        return Math.sqrt(distX * distX + distY * distY + distZ * distZ);
    }

    public double dist(Vector3 other) {
        double distX = this.x - other.x;
        double distY = this.y - other.y;
        double distZ = this.y - other.z;
        return Math.sqrt(distX * distX + distY * distY + distZ * distZ);
    }

    public double distSquared(double x, double y, double z) {
        double distX = this.x - x;
        double distY = this.y - y;
        double distZ = this.z - z;
        return distX * distX + distY * distY + distZ * distZ;
    }

    public double distSquared(Vector3 other) {
        double distX = this.x - other.x;
        double distY = this.y - other.y;
        double distZ = this.z - other.z;
        return distX * distX + distY * distY + distZ * distZ;
    }
    
    public double scalarProduct(Vector3 other) {
		return this.x * other.x + 
			   this.y * other.y + 
			   this.z * other.z;
	}

}
