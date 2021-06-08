package de.thi.dronesim.persistence.entity;

/**
 * Object that holds all configurations to a Wind-Layer.
 *
 * @author Daniel Stolle
 * @author Lausch, Christopher
 * @author Hupp, Laurence
 * @author Wittschen, Marvin
 */
public class WindConfig {

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
        return windSpeed == that.windSpeed
                && gustSpeed == that.gustSpeed
                && timeStart == that.timeStart
                && timeEnd == that.timeEnd
                && altitudeBottom == that.altitudeBottom
                && altitudeTop == that.altitudeTop
                && windDirection == that.windDirection;
    }

    @Override
    public int hashCode() {
        int hashCode = Double.hashCode(windSpeed);
        hashCode *= 31 + Double.hashCode(gustSpeed);
        hashCode *= 31 + Double.hashCode(timeStart);
        hashCode *= 31 + Double.hashCode(timeEnd);
        hashCode *= 31 + Double.hashCode(altitudeBottom);
        hashCode *= 31 + Double.hashCode(altitudeTop);
        hashCode *= 31 + Double.hashCode(windDirection);
        return hashCode;
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
