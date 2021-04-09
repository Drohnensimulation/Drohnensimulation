package de.thi.ufo;

import de.thi.hindernis.math.Vector3;

public class Location {

    private final Vector3 position;
    private final Vector3 movement;

    private double track = 0;                   // current track                [degree]
    private double hdg = 0;                     // current heading              [degree]
    private double tas = 0;                     // current true airspeed        [m/s]
    private double gs = 0;                      // current ground speed         [m/s]
    private double vs = 0;                      // current vertical speed       [m/s]
    private double i = 0;                       // current pitch inclination    [degree]

    private double deltaHdg = 0;
    private double deltaTas = 0;
    private double deltaVs = 0;

    public Location(double x, double y, double z) {
        this.position = new Vector3(x, y, z);
        this.movement = new Vector3(0, 0, 0);
    }

    /**
     * Updates the position and movement vector based on the track, ground speed and bertical speed.
     * @param updateRate Updates per second.
     */
    public void updatePosition(int updateRate) {
        movement.x = Math.cos(Math.toRadians((track + -90) % 360)) * gs;
        movement.y = vs;
        movement.z = Math.cos(Math.toRadians(track)) * gs;
        // Calculate position based on movement
        position.add(movement);
        position.mul(1.0/updateRate);
    }

    /**
     * Calculates airspeed, vertical speed and heading according to its corresponding delta.
     * Update rates are as follows:
     *  tas:    1
     *  vs:     1
     *  gs:     1
     *  TODO calculate acceleration based on update rate
     */
    public void updateDelta(int updateRate) {
        DeltaUpdate speedUpdate;
        // True Air Speed
        if (deltaTas != 0) {
            speedUpdate = updateSpeed(tas, deltaTas, 0, UfoSim.VMAX, UfoSim.ACCELERATION / updateRate);
            tas = speedUpdate.value;
            deltaTas = speedUpdate.delta;
            // Set airspeed as ground speed
            gs = tas;
        }

        // Vertical Speed
        if (deltaVs != 0) {
            speedUpdate = updateSpeed(vs, deltaVs, -UfoSim.VMAX, UfoSim.VMAX, UfoSim.ACCELERATION / updateRate);
            vs = speedUpdate.value;
            deltaVs = speedUpdate.delta;
        }

        // Heading
        if (deltaHdg != 0) {
            double hgdChange = 1 * Math.signum(deltaHdg);
            if (Math.abs(deltaHdg) < 1) {
                hgdChange = deltaHdg;
            }
            hdg += hgdChange;
            deltaHdg -= hgdChange;
            // Set boundaries to 0 <= hdg < 360
            if (hdg < 0) hdg += 360;
            hdg %= 360;
            // Set track as hdg.
            track = hdg;
        }
    }

    /**
     * Updates a speed and its corresponding delta.
     * @param speed Current speed in m/s
     * @param delta Requested change in m/s
     * @param min Minimum speed in m/s
     * @param max Maximum speed in m/s
     * @param acceleration Increment of speed im m/s^2
     * @return The updated speed and delta.
     */
    private DeltaUpdate updateSpeed(double speed, double delta, double min, double max, double acceleration) {
        // update v
        if (delta != 0) {
            acceleration *= Math.signum(delta);  // Set direction of acceleration
            if (delta / acceleration > 1) {
                speed += acceleration;
                delta -= acceleration;
            } else {
                speed += delta;
                delta = 0;
            }
        }
        speed = Math.max(min, Math.min(speed, max));
        return new DeltaUpdate(speed, delta);
    }

    /**
     * Sets all values to zero.
     */
    public void reset() {
        position.set(0, 0, 0);
        movement.set(0, 0, 0);
        hdg = 0;
        track = 0;
        tas = 0;
        gs = 0;
        vs = 0;
        i = 0;
        deltaHdg = 0;
        deltaVs = 0;
        deltaTas = 0;
    }

    /* =================================================================================================================
     * Getter and setter
     * ============================================================================================================== */

    public double getX() {
        return position.x;
    }

    public void setX(double x) {
        this.position.x = x;
    }

    public double getY() {
        return position.y;
    }

    public void setY(double y) {
        this.position.y = y;
    }

    public double getZ() {
        return position.z;
    }

    public void setZ(double z) {
        this.position.z = z;
    }

    /**
     *
     * @return The current position
     */
    public Vector3 getPosition() {
        return position;
    }

    /**
     *
     * @return The current direction of travel in m/s
     */
    public Vector3 getMovement() {
        return movement;
    }

    /**
     *
     * @return The actual travel direction in deg,
     */
    public double getTrack() {
        return track;
    }

    /**
     * Sets the track (true travel direction).
     * NOTE:    This method does not change the heading and the track will be overwritten once {@link #updateDelta(int)}
     *          is called.
     *          Consider using {@link #setHeading(double)} To make this change affective.
     * @param track Track direction in deg
     */
    public void setTrack(double track) {
        this.track = track;
    }

    /**
     *
     * @return Heading in deg
     */
    public double getHeading() {
        return hdg;
    }

    /**
     * Sets the heading.
     * @param hdg Heading in deg.
     */
    public void setHeading(double hdg) {
        this.hdg = hdg;
    }

    /**
     *
     * @return The true airspeed in m/s
     */
    public double getAirspeed() {
        return tas;
    }

    /**
     * Sets the airspeed
     * @param tas True airspeed in m/s. (0 <= tas)
     */
    public void setAirspeed(double tas) {
        this.tas = tas;
    }

    /**
     *
     * @return The projected speed over ground in m/s
     */
    public double getGroundSpeed() {
        return gs;
    }

    /**
     * Sets the speed over ground.
     * NOTE:    This method does not change the airspeed and the ground speed will be overwritten once
     *          {@link #updateDelta(int)} is called.
     *          Consider using {@link #setAirspeed(double)} To make this change affective.
     * @param gs Ground speed in m/s
     */
    public void setGroundSpeed(double gs) {
        this.gs = gs;
    }

    /**
     *
     * @return The vertical speed in m/s
     */
    public double getVerticalSpeed() {
        return vs;
    }

    /**
     * Sets the vertical speed
     * @param vs Vertical speed in m/s
     */
    public void setVerticalSpeed(double vs) {
        this.vs = vs;
    }

    /**
     *
     * @return The inclination of the x-axis in deg
     */
    public double getInclination() {
        return i;
    }

    /**
     * Sets the inclination of the x-axis
     * @param i Inclination in deg. (-90 <= i <= 90)
     */
    public void setInclination(double i) {
        this.i = i;
    }

    /* =================================================================================================================
     * Delta
     * ============================================================================================================== */

    /**
     *
     * @return Requested, not applied change of heading in deg
     */
    public double getDeltaHeading() {
        return deltaHdg;
    }

    /**
     * Requests a change of heading. The change is summed up with all previous request changes.
     * A change will take affect over time.
     * @param delta Change in deg
     */
    public void requestDeltaHeading(double delta) {
        this.deltaHdg += delta;
    }

    /**
     *
     * @return The requested but not applied change of airspeed in m/s
     */
    public double getDeltaAirspeed() {
        return deltaTas;
    }

    /**
     * Requests a change of airspeed. The change is summed up with all previous request changes.
     * @param delta Change in m/s
     */
    public void requestDeltaAirspeed(double delta) {
        this.deltaTas += delta;
    }

    /**
     *
     * @return The requested but not applied change of vertical speed in m/s
     */
    public double getDeltaVerticalSpeed() {
        return deltaVs;
    }

    /**
     * Requests a change of vertical speed. The change is summed up with all previous request changes.
     * @param delta Change in m/s
     */
    public void requestDeltaVerticalSpeed(double delta) {
        this.deltaVs += delta;
    }


    /**
     * Helper class for return values with associated delta
     */
    private static class DeltaUpdate {

        private final double delta;
        private final double value;

        public DeltaUpdate(double value, double delta) {
            this.value = value;
            this.delta = delta;
        }
    }

}
