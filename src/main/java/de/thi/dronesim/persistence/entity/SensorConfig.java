package de.thi.dronesim.persistence.entity;

/**
 * Object that holds all Configurations to a Sensor.
 *
 * @author Daniel Stolle
 */
public class SensorConfig {

    private String className;
    private int sensorId;

    private double measurementAccuracy;
    private double directionX;
    private double directionY;
    private double directionZ;
    private double posX;
    private double posY;
    private double posZ;

    // DistanceSensor
    private double range;
    private double sensorAngle;
    private double sensorRadius;

    // WindSensor
    private double zeroDegreeDirectionX;
    private double zeroDegreeDirectionY;
    private double zeroDegreeDirectionZ;
    private double nintyDegreeDirectionX;
    private double nintyDegreeDirectionY;
    private double nintyDegreeDirectionZ;

    // UltrasonicSensor
    private float rangeIncreaseVelocity;
    private int callTimerForSensorValues;

    // /////////////////////////////////////////////////////////////////////////////
    // Object Methods
    // /////////////////////////////////////////////////////////////////////////////

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SensorConfig config = (SensorConfig) o;

        if (sensorId != config.sensorId) return false;
        if (Double.compare(config.measurementAccuracy, measurementAccuracy) != 0) return false;
        if (Double.compare(config.directionX, directionX) != 0) return false;
        if (Double.compare(config.directionY, directionY) != 0) return false;
        if (Double.compare(config.directionZ, directionZ) != 0) return false;
        if (Double.compare(config.posX, posX) != 0) return false;
        if (Double.compare(config.posY, posY) != 0) return false;
        if (Double.compare(config.posZ, posZ) != 0) return false;
        if (Double.compare(config.range, range) != 0) return false;
        if (Double.compare(config.sensorAngle, sensorAngle) != 0) return false;
        if (Double.compare(config.sensorRadius, sensorRadius) != 0) return false;
        if (Double.compare(config.zeroDegreeDirectionX, zeroDegreeDirectionX) != 0) return false;
        if (Double.compare(config.zeroDegreeDirectionY, zeroDegreeDirectionY) != 0) return false;
        if (Double.compare(config.zeroDegreeDirectionZ, zeroDegreeDirectionZ) != 0) return false;
        if (Double.compare(config.nintyDegreeDirectionX, nintyDegreeDirectionX) != 0) return false;
        if (Double.compare(config.nintyDegreeDirectionY, nintyDegreeDirectionY) != 0) return false;
        if (Double.compare(config.nintyDegreeDirectionZ, nintyDegreeDirectionZ) != 0) return false;
        if (Float.compare(config.rangeIncreaseVelocity, rangeIncreaseVelocity) != 0) return false;
        if (callTimerForSensorValues != config.callTimerForSensorValues) return false;
        return className != null ? className.equals(config.className) : config.className == null;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = className != null ? className.hashCode() : 0;
        result = 31 * result + sensorId;
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
        temp = Double.doubleToLongBits(range);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(sensorAngle);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(sensorRadius);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(zeroDegreeDirectionX);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(zeroDegreeDirectionY);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(zeroDegreeDirectionZ);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(nintyDegreeDirectionX);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(nintyDegreeDirectionY);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(nintyDegreeDirectionZ);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + (rangeIncreaseVelocity != +0.0f ? Float.floatToIntBits(rangeIncreaseVelocity) : 0);
        result = 31 * result + callTimerForSensorValues;
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

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public double getZeroDegreeDirectionX() {
        return zeroDegreeDirectionX;
    }

    public void setZeroDegreeDirectionX(double zeroDegreeDirectionX) {
        this.zeroDegreeDirectionX = zeroDegreeDirectionX;
    }

    public double getZeroDegreeDirectionY() {
        return zeroDegreeDirectionY;
    }

    public void setZeroDegreeDirectionY(double zeroDegreeDirectionY) {
        this.zeroDegreeDirectionY = zeroDegreeDirectionY;
    }

    public double getZeroDegreeDirectionZ() {
        return zeroDegreeDirectionZ;
    }

    public void setZeroDegreeDirectionZ(double zeroDegreeDirectionZ) {
        this.zeroDegreeDirectionZ = zeroDegreeDirectionZ;
    }

    public double getNintyDegreeDirectionX() {
        return nintyDegreeDirectionX;
    }

    public void setNintyDegreeDirectionX(double nintyDegreeDirectionX) {
        this.nintyDegreeDirectionX = nintyDegreeDirectionX;
    }

    public double getNintyDegreeDirectionY() {
        return nintyDegreeDirectionY;
    }

    public void setNintyDegreeDirectionY(double nintyDegreeDirectionY) {
        this.nintyDegreeDirectionY = nintyDegreeDirectionY;
    }

    public double getNintyDegreeDirectionZ() {
        return nintyDegreeDirectionZ;
    }

    public void setNintyDegreeDirectionZ(double nintyDegreeDirectionZ) {
        this.nintyDegreeDirectionZ = nintyDegreeDirectionZ;
    }

    public int getSensorId() {
    	return sensorId;
    }
    
    public void setSensorId(int id) {
    	this.sensorId = id;
    }

    public float getRangeIncreaseVelocity() {
        return rangeIncreaseVelocity;
    }

    public void setRangeIncreaseVelocity(float rangeIncreaseVelocity) {
        this.rangeIncreaseVelocity = rangeIncreaseVelocity;
    }

    public int getCallTimerForSensorValues() {
        return callTimerForSensorValues;
    }

    public void setCallTimerForSensorValues(int callTimerForSensorValues) {
        this.callTimerForSensorValues = callTimerForSensorValues;
    }
}
