package de.thi.dronesim.persistence.entity;

/**
 * Object that holds all Configurations to a Sensor.
 *
 * @author Daniel Stolle
 */
public class SensorConfig {

    private String className;
    private int sensorId;

    private float measurementAccuracy;
    private float directionX;
    private float directionY;
    private float directionZ;
    private float posX;
    private float posY;
    private float posZ;

    // DistanceSensor
    private float range;
    private float sensorAngle;
    private float sensorRadius;
    private String sensorForm;
    private String calcType;

    // WindSensor
    private float zeroDegreeDirectionX;
    private float zeroDegreeDirectionY;
    private float zeroDegreeDirectionZ;
    private float nintyDegreeDirectionX;
    private float nintyDegreeDirectionY;
    private float nintyDegreeDirectionZ;

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
        if (!config.calcType.equals(calcType)) return false;
        if (!config.sensorForm.equals(sensorForm)) return false;
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
        temp = Long.parseLong(Integer.toString(sensorForm.length()));
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Long.parseLong(Integer.toString(calcType.length()));
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }


    // /////////////////////////////////////////////////////////////////////////////
    // Getter/Setter
    // /////////////////////////////////////////////////////////////////////////////

    public float getRange() {
        return range;
    }

    public void setRange(float range) {
        this.range = range;
    }

    public float getSensorAngle() {
        return sensorAngle;
    }

    public void setSensorAngle(float sensorAngle) {
        this.sensorAngle = sensorAngle;
    }

    public float getSensorRadius() {
        return sensorRadius;
    }

    public void setSensorRadius(float sensorRadius) {
        this.sensorRadius = sensorRadius;
    }

    public float getMeasurementAccuracy() {
        return measurementAccuracy;
    }

    public void setMeasurementAccuracy(float measurementAccuracy) {
        this.measurementAccuracy = measurementAccuracy;
    }

    public float getDirectionX() {
        return directionX;
    }

    public void setDirectionX(float directionX) {
        this.directionX = directionX;
    }

    public float getDirectionY() {
        return directionY;
    }

    public void setDirectionY(float directionY) {
        this.directionY = directionY;
    }

    public float getDirectionZ() {
        return directionZ;
    }

    public void setDirectionZ(float directionZ) {
        this.directionZ = directionZ;
    }

    public float getPosX() {
        return posX;
    }

    public void setPosX(float posX) {
        this.posX = posX;
    }

    public float getPosY() {
        return posY;
    }

    public void setPosY(float posY) {
        this.posY = posY;
    }

    public float getPosZ() {
        return posZ;
    }

    public void setPosZ(float posZ) {
        this.posZ = posZ;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getSensorForm() {
        return sensorForm;
    }

    public void setSensorForm(String sensorForm) {
        this.sensorForm = sensorForm;
    }

    public String getCalcType() {
        return calcType;
    }

    public void setCalcType(String calcType) {
        this.calcType = calcType;
    }

    public float getZeroDegreeDirectionX() {
        return zeroDegreeDirectionX;
    }

    public void setZeroDegreeDirectionX(float zeroDegreeDirectionX) {
        this.zeroDegreeDirectionX = zeroDegreeDirectionX;
    }

    public float getZeroDegreeDirectionY() {
        return zeroDegreeDirectionY;
    }

    public void setZeroDegreeDirectionY(float zeroDegreeDirectionY) {
        this.zeroDegreeDirectionY = zeroDegreeDirectionY;
    }

    public float getZeroDegreeDirectionZ() {
        return zeroDegreeDirectionZ;
    }

    public void setZeroDegreeDirectionZ(float zeroDegreeDirectionZ) {
        this.zeroDegreeDirectionZ = zeroDegreeDirectionZ;
    }

    public float getNintyDegreeDirectionX() {
        return nintyDegreeDirectionX;
    }

    public void setNintyDegreeDirectionX(float nintyDegreeDirectionX) {
        this.nintyDegreeDirectionX = nintyDegreeDirectionX;
    }

    public float getNintyDegreeDirectionY() {
        return nintyDegreeDirectionY;
    }

    public void setNintyDegreeDirectionY(float nintyDegreeDirectionY) {
        this.nintyDegreeDirectionY = nintyDegreeDirectionY;
    }

    public float getNintyDegreeDirectionZ() {
        return nintyDegreeDirectionZ;
    }

    public void setNintyDegreeDirectionZ(float nintyDegreeDirectionZ) {
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
