package de.thi.wind;

import de.thi.ufo.Location;
import de.thi.ufo.UfoSim;

public class WindLayer implements Comparable<WindLayer> {

    private static final double GUST_RISE_TIME = 2;     // Time until gust reaches full speed   [s]
    private static final double MAX_CALM_TIME = 120;    // Maximum time for no gust             [s]

    private double windSpeed;                           // Speed of the steady wind             [m/s]
    private double gustSpeed;                           // Max Speed of the gusts               [m/s]
    private double timeStart;                           // time of wind layer start             [s]
    private double timeEnd;                             // time of wind layer end               [s]
    private double altitudeBottom;                      // height of wind layer bottom          [m]
    private double altitudeTop;                         // height of wind layer top             [m]
    private double windDirection;                       // current wind direction               [degree]

    private double nextGustStart = 0.0;                 // start time of the next gust          [s]
    private  double nextGustSpeed = 0.0;                // speed of the next gust               [m/s]

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
    public WindLayer(double windSpeed, double gustSpeed, double timeStart, double timeEnd, double altitudeBottom,
                     double altitudeTop, double windDirection) {
        this.windSpeed = windSpeed;
        this.gustSpeed = gustSpeed;
        this.timeStart = timeStart;
        this.timeEnd = timeEnd;
        this.altitudeBottom = altitudeBottom;
        this.altitudeTop = altitudeTop;
        this.windDirection = windDirection;
    }

    public WindLayer copy() {
        return new WindLayer(
                windSpeed,
                gustSpeed,
                timeStart,
                timeEnd,
                altitudeBottom,
                altitudeTop,
                windDirection
        );
    }

    /**
     * Applies wind speeds to drone
     * @param location Current location
     */
    public Wind.WindChange applyForces(Location location){
        double hdg = location.getHeading();
        double tas = location.getAirspeed();
        double ws = calcWindSpeed();

        if (hdg == windDirection) {
            return new Wind.WindChange(hdg, tas - ws);
        }

        // tail wind only
        if ((hdg - ws) % 180 == 0) {
            return new Wind.WindChange(hdg, tas + ws);
        }

        // Calculate angle wind angle (wa) of triangle
        double wa = (((windDirection - hdg) + 540) % 360) - 180;
        // Calculate ground speed (gs) with cosine law
        double gs = Math.sqrt(Math.pow(tas, 2) + Math.pow(ws, 2) - 2 * tas * ws * Math.cos(Math.toRadians(wa)));
        // Calculate wind correction angle (wca) using sine law
        double wca = Math.toDegrees(Math.asin(ws * Math.sin(Math.toRadians(Math.abs(wa))) / gs));
        // Set correct sign for wca
        if (wa > 0) wca *= -1;
        // Calculate resulting course
        return new Wind.WindChange(hdg + wca, gs);
    }


    /**
     * calculating wind speed with interpolation
     * @return windSpeed New wind speed in          [m/s]
     */
    private double calcWindSpeed() {
        double windSpeed = this.windSpeed;
        double time = UfoSim.getInstance().getTime();

        if (time > nextGustStart + GUST_RISE_TIME * 2) {
            calcNextGust(time);
        } else if (time >= nextGustStart) {
            windSpeed = interpolation(time, nextGustSpeed, nextGustStart, nextGustStart + 2 * GUST_RISE_TIME);
        }

        return windSpeed;
    }

    /**
     * interpolation between gust and wind
     * @param time          // current simulation time  [s]
     * @param gustSpeed     // current gust speed       [m/s]
     * @param gustEnd       // current gust end time    [m/s]
     * @param gustStart     // current gust start time  [m/s]
     * @return y
     */
    private double interpolation(double time, double gustSpeed, double gustStart, double gustEnd){
        double y = ((gustSpeed - windSpeed)/(gustEnd - gustStart)) * (time - gustStart);

        if(time > (gustStart + GUST_RISE_TIME)){
            return gustSpeed - y;
        }

        return windSpeed + y;
    }

    /**
     * calc next gust time and nex gust speed
     * @param time current time         [s]
     */
    private void calcNextGust(double time){
        nextGustStart = Math.random() * MAX_CALM_TIME + time;
        nextGustSpeed = Math.random() * gustSpeed + windSpeed;
    }

    public static double getGustRiseTime() {
        return GUST_RISE_TIME;
    }

    public static double getMaxCalmTime() {
        return MAX_CALM_TIME;
    }

    public double getWindSpeed() {
        return windSpeed;
    }

    public double getGustSpeed() {
        return gustSpeed;
    }

    public double getTimeStart() {
        return timeStart;
    }

    public double getTimeEnd() {
        return timeEnd;
    }

    public double getAltitudeBottom() {
        return altitudeBottom;
    }

    public double getAltitudeTop() {
        return altitudeTop;
    }

    public double getWindDirection() {
        return windDirection;
    }

    public double getNextGustStart() {
        return nextGustStart;
    }

    public double getNextGustSpeed() {
        return nextGustSpeed;
    }

    public void setWindSpeed(double windSpeed) {
        this.windSpeed = windSpeed;
    }

    public void setGustSpeed(double gustSpeed) {
        this.gustSpeed = gustSpeed;
    }

    public void setTimeStart(double timeStart) {
        this.timeStart = timeStart;
    }

    public void setTimeEnd(double timeEnd) {
        this.timeEnd = timeEnd;
    }

    public void setAltitudeBottom(double altitudeBottom) {
        this.altitudeBottom = altitudeBottom;
    }

    public void setAltitudeTop(double altitudeTop) {
        this.altitudeTop = altitudeTop;
    }

    public void setWindDirection(double windDirection) {
        this.windDirection = windDirection;
    }

    public void setNextGustStart(double nextGustStart) {
        this.nextGustStart = nextGustStart;
    }

    public void setNextGustSpeed(double nextGustSpeed) {
        this.nextGustSpeed = nextGustSpeed;
    }

    @Override
    public int compareTo(WindLayer layer) {
        if (getTimeStart() == 0.0 || layer.getTimeStart() == 0.0) {
            return 0;
        }
        return layer.compareTo(this);
    }
}
