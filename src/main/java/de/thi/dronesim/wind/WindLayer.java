package de.thi.dronesim.wind;

import javax.vecmath.Vector3d;

public class WindLayer {

    protected static final double GUST_RISE_TIME = 2;     // Time until gust reaches full speed   [s]
    protected static final double MAX_CALM_TIME = 120;    // Maximum time for no gust             [s]

    private final double windSpeed;                           // Speed of the steady wind             [m/s]
    private final double gustSpeed;                           // Max Speed of the gusts               [m/s]
    private final double windDirection;                       // current wind direction               [degree]
    private double timeStart;                           // time of wind layer start             [s]
    private double timeEnd;                             // time of wind layer end               [s]
    private double altitudeBottom;                      // height of wind layer bottom          [m]
    private double altitudeTop;                         // height of wind layer top             [m]

    private double nextGustStart = 0.0;                 // start time of the next gust          [s]
    private double nextGustSpeed = 0.0;                // speed of the next gust               [m/s]

    /**
     * Constructor for wind layer
     * @param windSpeed Speed of the steady wind                    [m/s]
     * @param gustSpeed Speed of the gusts                          [m/s]
     * @param timeStart time of wind layer start                    [s]
     * @param timeEnd time of wind layer end                        [s]
     * @param altitudeBottom  height of wind layer bottom           [m]
     * @param altitudeTop height of wind layer top                  [m]
     * @param windDirection current wind direction                  [degree]
     */
    protected WindLayer(double windSpeed, double gustSpeed, double timeStart, double timeEnd, double altitudeBottom,
                     double altitudeTop, double windDirection) {
        this.windSpeed = windSpeed;
        this.gustSpeed = gustSpeed;
        this.timeStart = timeStart;
        this.timeEnd = timeEnd;
        this.altitudeBottom = altitudeBottom;
        this.altitudeTop = altitudeTop;
        this.windDirection = windDirection;
    }

    /**
     * Checks if two layers overlap with each other
     * @param other Layer to test on
     * @return True of both layers overlap, otherwise false
     */
    public boolean overlapsWith(WindLayer other) {
        return timeStart < other.timeStart + (other.timeEnd - other.timeStart)
                && timeStart + (timeEnd - timeStart) > other.timeStart
                && altitudeBottom < other.altitudeBottom + (other.altitudeTop - other.altitudeBottom)
                && altitudeBottom + (altitudeTop - altitudeBottom) > other.altitudeBottom;
    }

    /**
     *
     * @return True if all values are within their limits
     */
    public boolean isValid() {
        return windDirection >= 0 && windDirection < 360
                && altitudeBottom >= -1 * Wind.WIND_LAYER_INTERPOLATION_ALTITUDE_RANGE * 2
                && altitudeTop > 0
                && timeStart >= -1 * Wind.WIND_LAYER_INTERPOLATION_TIME_RANGE * 2
                && timeEnd > 0
                && windSpeed >= 0
                && gustSpeed >= windSpeed
                && altitudeBottom < altitudeTop
                && timeStart < timeEnd;
    }

    /**
     * Creates a vector out of the wind speed.
     * x and z are the speeds in each direction the length equals the speed
     * @param time The time at which the speeds occur
     * @return A vector with the speed
     */
    public Vector3d getSpeedVector(double time) {
        double ws = calcWindSpeed(time);
        double wd = (windDirection + 180) % 360;
        return Wind.createSpeedVector(wd, ws);
    }

    /**
     * Normalizes all values to next lowest multiple of double interpolation range.
     * If the start is zero, the start is also decreased by double interpolation range to prevent interpolation there.
     */
    public void normalize() {
        double altDistance = Wind.WIND_LAYER_INTERPOLATION_ALTITUDE_RANGE * 2;
        double timeDistance = Wind.WIND_LAYER_INTERPOLATION_ALTITUDE_RANGE * 2;
        // Convert to next multiple of distance
        altitudeBottom = Math.ceil(altitudeBottom / altDistance) * altDistance;
        altitudeTop = Math.ceil(altitudeTop / altDistance) * altDistance;
        timeStart =  Math.ceil(timeStart / timeDistance) * timeDistance;
        timeEnd = Math.ceil(timeEnd / timeDistance) * timeDistance;
        // Set start to earlier point to prevent interpolation for these cases
        if (timeStart == 0) timeStart = -1 * timeDistance;
        if (altitudeBottom == 0) altitudeBottom = -1 * altDistance;
    }

    /**
     * Converts a layer to a wind speed vector.
     * <ul>
     *     <li>If the layer is null, a zero vector is returned</li>
     * </ul>
     * @param windLayer Layer to be converted
     * @param time The time at which the wind occurs
     * @return A vector with the wind speed or a null vector
     */
    protected static Vector3d convertSpeedOrZero(WindLayer windLayer, double time) {
        return windLayer != null ? windLayer.getSpeedVector(time) : new Vector3d(0, 0, 0);
    }

    /**
     * Calculates wind speed with interpolation
     * @return New wind speed in m/s
     */
    private double calcWindSpeed(double time) {
        double windSpeed = this.windSpeed;
        // Check if new gust needs to be calculated
        if (gustSpeed > windSpeed) {
            if (time > nextGustStart + GUST_RISE_TIME * 2) {
                calcNextGust(time);
            } else if (time >= nextGustStart && gustSpeed > windSpeed) {
                // Apply gust
                windSpeed = gustInterpolation(time, nextGustSpeed, nextGustStart, nextGustStart + 2 * GUST_RISE_TIME);
            }
        }
        return windSpeed;
    }

    /**
     * Interpolation between gust and wind
     * @param time Time for which the gust should be calculated in s
     * @param gustSpeed Maximum speed of the gust in m/s
     * @param gustStart Time at which the gust starts in s
     * @param gustEnd Time at which the gust ends in s
     * @return The resulting wind speed
     */
    private double gustInterpolation(double time, double gustSpeed, double gustStart, double gustEnd) {
        final double riseTime = (gustEnd - gustStart) / 2;
        double y;
        if (time <= (gustStart + riseTime)) {
            y = ((gustSpeed - windSpeed)/(gustEnd - gustStart) * 2) * (time - gustStart);
        } else {
           y = -1 * ((gustSpeed - windSpeed)/(gustEnd - gustStart) * 2) * (time - gustStart) + (gustSpeed - windSpeed)
                   * riseTime;
        }
        return windSpeed + y;
    }

    /**
     * calc next gust time and nex gust speed
     * @param time current time         [s]
     */
    private void calcNextGust(double time) {
        nextGustStart = Math.random() * MAX_CALM_TIME + time;
        nextGustSpeed = Math.random() * (gustSpeed - windSpeed) + windSpeed;
    }


    protected double getWindSpeed() {
        return windSpeed;
    }

    protected double getGustSpeed() {
        return gustSpeed;
    }

    protected double getTimeStart() {
        return timeStart;
    }

    protected double getTimeEnd() {
        return timeEnd;
    }

    protected double getAltitudeBottom() {
        return altitudeBottom;
    }

    protected double getAltitudeTop() {
        return altitudeTop;
    }

    protected double getWindDirection() {
        return windDirection;
    }

    protected double getNextGustStart() {
        return nextGustStart;
    }

    protected double getNextGustSpeed() {
        return nextGustSpeed;
    }

    protected void setNextGustStart(double nextGustStart) {
        this.nextGustStart = nextGustStart;
    }

    protected void setNextGustSpeed(double nextGustSpeed) {
        this.nextGustSpeed = nextGustSpeed;
    }

}
