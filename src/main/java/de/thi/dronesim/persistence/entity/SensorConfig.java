package de.thi.dronesim.persistence.entity;

import com.jme3.math.Vector3f;

/**
 * Object that holds all Configurations to a Sensor.
 */
public class SensorConfig {

    private double range;
    private double sensorAngle;
    private double sensorRadius;
    private double measurementAccuracy;
    private double directionX;
    private double directionY;
    private double directionZ;
    private double posX;
    private double posY;
    private double posZ;

    // /////////////////////////////////////////////////////////////////////////////
    // Object Methods
    // /////////////////////////////////////////////////////////////////////////////

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        SensorConfig that = (SensorConfig) o;

        if (Double.compare(that.range, range) != 0) {
            return false;
        }
        if (Double.compare(that.sensorAngle, sensorAngle) != 0) {
            return false;
        }
        if (Double.compare(that.sensorRadius, sensorRadius) != 0) {
            return false;
        }
        if (Double.compare(that.measurementAccuracy, measurementAccuracy) != 0) {
            return false;
        }
        if (Double.compare(that.directionX, directionX) != 0) {
            return false;
        }
        if (Double.compare(that.directionY, directionY) != 0) {
            return false;
        }
        if (Double.compare(that.directionZ, directionZ) != 0) {
            return false;
        }
        if (Double.compare(that.posX, posX) != 0) {
            return false;
        }
        if (Double.compare(that.posY, posY) != 0) {
            return false;
        }
        return Double.compare(that.posZ, posZ) == 0;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        temp = Double.doubleToLongBits(range);
        result = (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(sensorAngle);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(sensorRadius);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(measurementAccuracy);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(directionX);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(directionY);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(directionZ);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(posX);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(posY);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(posZ);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    // /////////////////////////////////////////////////////////////////////////////
    // Getter/Setter
    // /////////////////////////////////////////////////////////////////////////////

    public double getRange() {
        return range;
    }

    public void setRange(double range) {
        this.range = range;
    }

    public double getSensorAngle() {
        return sensorAngle;
    }

    public void setSensorAngle(double sensorAngle) {
        this.sensorAngle = sensorAngle;
    }

    public double getSensorRadius() {
        return sensorRadius;
    }

    public void setSensorRadius(double sensorRadius) {
        this.sensorRadius = sensorRadius;
    }

    public double getMeasurementAccuracy() {
        return measurementAccuracy;
    }

    public void setMeasurementAccuracy(double measurementAccuracy) {
        this.measurementAccuracy = measurementAccuracy;
    }

    public double getDirectionX() {
        return directionX;
    }

    public void setDirectionX(double directionX) {
        this.directionX = directionX;
    }

    public double getDirectionY() {
        return directionY;
    }

    public void setDirectionY(double directionY) {
        this.directionY = directionY;
    }

    public double getDirectionZ() {
        return directionZ;
    }

    public void setDirectionZ(double directionZ) {
        this.directionZ = directionZ;
    }

    public double getPosX() {
        return posX;
    }

    public void setPosX(double posX) {
        this.posX = posX;
    }

    public double getPosY() {
        return posY;
    }

    public void setPosY(double posY) {
        this.posY = posY;
    }

    public double getPosZ() {
        return posZ;
    }

    public void setPosZ(double posZ) {
        this.posZ = posZ;
    }
}
