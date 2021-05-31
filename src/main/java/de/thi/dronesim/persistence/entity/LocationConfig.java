package de.thi.dronesim.persistence.entity;

/**
 * Object that holds all Configurations for the initial Location.
 *
 * @author Daniel Stolle
 */
public class LocationConfig {

    private float x;
    private float y;
    private float z;

    // /////////////////////////////////////////////////////////////////////////////
    // Object Methods
    // /////////////////////////////////////////////////////////////////////////////

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LocationConfig that = (LocationConfig) o;

        if (Float.compare(that.x, x) != 0) return false;
        if (Float.compare(that.y, y) != 0) return false;
        return Float.compare(that.z, z) == 0;
    }

    @Override
    public int hashCode() {
        int result = (x != +0.0f ? Float.floatToIntBits(x) : 0);
        result = 31 * result + (y != +0.0f ? Float.floatToIntBits(y) : 0);
        result = 31 * result + (z != +0.0f ? Float.floatToIntBits(z) : 0);
        return result;
    }

    // /////////////////////////////////////////////////////////////////////////////
    // Getter/Setter
    // /////////////////////////////////////////////////////////////////////////////

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getZ() {
        return z;
    }

    public void setZ(float z) {
        this.z = z;
    }
}
