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
    private float startIncreaseTime;
    
    // RotationSensor
    private int spinsPerSecond;
    private float startRotationTime;

    // /////////////////////////////////////////////////////////////////////////////
    // Object Methods
    // /////////////////////////////////////////////////////////////////////////////

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SensorConfig config = (SensorConfig) o;

        if (sensorId != config.sensorId) return false;
        if (Float.compare(config.measurementAccuracy, measurementAccuracy) != 0) return false;
        if (Float.compare(config.directionX, directionX) != 0) return false;
        if (Float.compare(config.directionY, directionY) != 0) return false;
        if (Float.compare(config.directionZ, directionZ) != 0) return false;
        if (Float.compare(config.posX, posX) != 0) return false;
        if (Float.compare(config.posY, posY) != 0) return false;
        if (Float.compare(config.posZ, posZ) != 0) return false;
        if (Float.compare(config.range, range) != 0) return false;
        if (Float.compare(config.sensorAngle, sensorAngle) != 0) return false;
        if (Float.compare(config.sensorRadius, sensorRadius) != 0) return false;
        if (Float.compare(config.zeroDegreeDirectionX, zeroDegreeDirectionX) != 0) return false;
        if (Float.compare(config.zeroDegreeDirectionY, zeroDegreeDirectionY) != 0) return false;
        if (Float.compare(config.zeroDegreeDirectionZ, zeroDegreeDirectionZ) != 0) return false;
        if (Float.compare(config.nintyDegreeDirectionX, nintyDegreeDirectionX) != 0) return false;
        if (Float.compare(config.nintyDegreeDirectionY, nintyDegreeDirectionY) != 0) return false;
        if (Float.compare(config.nintyDegreeDirectionZ, nintyDegreeDirectionZ) != 0) return false;
        if (Float.compare(config.rangeIncreaseVelocity, rangeIncreaseVelocity) != 0) return false;
        if (Float.compare(config.startIncreaseTime, startIncreaseTime) != 0) return false;
        if (spinsPerSecond != config.spinsPerSecond) return false;
        if (Float.compare(config.startRotationTime, startRotationTime) != 0) return false;
        if (className != null ? !className.equals(config.className) : config.className != null) return false;
        if (sensorForm != null ? !sensorForm.equals(config.sensorForm) : config.sensorForm != null) return false;
        return calcType != null ? calcType.equals(config.calcType) : config.calcType == null;
    }

    @Override
    public int hashCode() {
        int result = className != null ? className.hashCode() : 0;
        result = 31 * result + sensorId;
        result = 31 * result + (measurementAccuracy != +0.0f ? Float.floatToIntBits(measurementAccuracy) : 0);
        result = 31 * result + (directionX != +0.0f ? Float.floatToIntBits(directionX) : 0);
        result = 31 * result + (directionY != +0.0f ? Float.floatToIntBits(directionY) : 0);
        result = 31 * result + (directionZ != +0.0f ? Float.floatToIntBits(directionZ) : 0);
        result = 31 * result + (posX != +0.0f ? Float.floatToIntBits(posX) : 0);
        result = 31 * result + (posY != +0.0f ? Float.floatToIntBits(posY) : 0);
        result = 31 * result + (posZ != +0.0f ? Float.floatToIntBits(posZ) : 0);
        result = 31 * result + (range != +0.0f ? Float.floatToIntBits(range) : 0);
        result = 31 * result + (sensorAngle != +0.0f ? Float.floatToIntBits(sensorAngle) : 0);
        result = 31 * result + (sensorRadius != +0.0f ? Float.floatToIntBits(sensorRadius) : 0);
        result = 31 * result + (sensorForm != null ? sensorForm.hashCode() : 0);
        result = 31 * result + (calcType != null ? calcType.hashCode() : 0);
        result = 31 * result + (zeroDegreeDirectionX != +0.0f ? Float.floatToIntBits(zeroDegreeDirectionX) : 0);
        result = 31 * result + (zeroDegreeDirectionY != +0.0f ? Float.floatToIntBits(zeroDegreeDirectionY) : 0);
        result = 31 * result + (zeroDegreeDirectionZ != +0.0f ? Float.floatToIntBits(zeroDegreeDirectionZ) : 0);
        result = 31 * result + (nintyDegreeDirectionX != +0.0f ? Float.floatToIntBits(nintyDegreeDirectionX) : 0);
        result = 31 * result + (nintyDegreeDirectionY != +0.0f ? Float.floatToIntBits(nintyDegreeDirectionY) : 0);
        result = 31 * result + (nintyDegreeDirectionZ != +0.0f ? Float.floatToIntBits(nintyDegreeDirectionZ) : 0);
        result = 31 * result + (rangeIncreaseVelocity != +0.0f ? Float.floatToIntBits(rangeIncreaseVelocity) : 0);
        result = 31 * result + (startIncreaseTime != +0.0f ? Float.floatToIntBits(startIncreaseTime) : 0);
        result = 31 * result + spinsPerSecond;
        result = 31 * result + (startRotationTime != +0.0f ? Float.floatToIntBits(startRotationTime) : 0);
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

    public float getStartIncreaseTime() {
        return startIncreaseTime;
    }

    public void setStartIncreaseTime(float startIncreaseTime) {
        this.startIncreaseTime = startIncreaseTime;
    }
    
    public int getSpinsPerSecond() {
        return spinsPerSecond;
    }

    public void setSpinsPerSecond(int spinsPerSecond) {
        this.spinsPerSecond = spinsPerSecond;
    }
    
    public float getStartRotationTime() {
        return startRotationTime;
    }

    public void setStartRotationTime(float startRotationTime) {
        this.startRotationTime = startRotationTime;
    }
}
