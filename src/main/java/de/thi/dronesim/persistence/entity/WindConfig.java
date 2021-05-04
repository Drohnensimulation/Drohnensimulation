package de.thi.dronesim.persistence.entity;

/**
 * Object that holds all configurations to a Wind-Layer.
 *
 * @author Daniel Stolle
 */
public class WindConfig {

    // TODO: add attributes, overwrite equals and hashcode

    private int dummy;

    private double windSpeed;                           // Speed of the steady wind             [m/s]
    private double gustSpeed;                           // Max Speed of the gusts               [m/s]
    private double timeStart;                           // time of wind layer start             [s]
    private double timeEnd;                             // time of wind layer end               [s]
    private double altitudeBottom;                      // height of wind layer bottom          [m]
    private double altitudeTop;                         // height of wind layer top             [m]
    private double windDirection;                       // current wind direction

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

        WindConfig that = (WindConfig) o;

        return dummy == that.dummy;
    }

    @Override
    public int hashCode() {
        return dummy;
    }


    public double getWindDirection() {
        return windDirection;
    }

    public void setWindDirection(double windDirection) {
        this.windDirection = windDirection;
    }

    public double getAltitudeTop() {
        return altitudeTop;
    }

    public void setAltitudeTop(double altitudeTop) {
        this.altitudeTop = altitudeTop;
    }

    public double getAltitudeBottom() {
        return altitudeBottom;
    }

    public void setAltitudeBottom(double altitudeBottom) {
        this.altitudeBottom = altitudeBottom;
    }

    public double getTimeEnd() {
        return timeEnd;
    }

    public void setTimeEnd(double timeEnd) {
        this.timeEnd = timeEnd;
    }

    public double getTimeStart() {
        return timeStart;
    }

    public void setTimeStart(double timeStart) {
        this.timeStart = timeStart;
    }

    public double getGustSpeed() {
        return gustSpeed;
    }

    public void setGustSpeed(double gustSpeed) {
        this.gustSpeed = gustSpeed;
    }

    public double getWindSpeed() {
        return windSpeed;
    }

    public void setWindSpeed(double windSpeed) {
        this.windSpeed = windSpeed;
    }
}
