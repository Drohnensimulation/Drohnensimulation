package de.thi.ufo;

import de.thi.hindernis.math.Vector3;

public class Location {

    private final Vector3 position;     // Vector of current position               [m]
    private final Vector3 movement;     // Vector of travel direction               [m/s]

    private double track = 0;           // True movement direction                  [deg]
    private double hdg = 0;             // Direction in which the A/C faces         [deg]
    private double tas = 0;             // Speed relative to the wind               [m/s]
    private double gs = 0;              // Speed over ground                        [m/s]
    private double vs = 0;              // Speed along y-axis                       [m/s]
    private double pitch = 0;           // Pitch angle of the A/C                   [deg]

    private double deltaHdg = 0;        // Requested, not applied change of hdg     [deg]
    private double deltaTas = 0;        // Requested, not applied change of tas     [m/s]
    private double deltaVs = 0;         // Requested, not applied change of vas     [m/s]

    public Location(double x, double y, double z) {
        this.position = new Vector3(x, y, z);
        this.movement = new Vector3(0, 0, 0);
    }

    /**
     * Updates the position and movement vector based on the track, ground speed and vertical speed.
     * @param updateRate Updates per second.
     */
    public void updatePosition(int updateRate) {
        movement.x = Math.cos(Math.toRadians((track + -90) % 360)) * gs;
        movement.z = vs;
        movement.y = Math.cos(Math.toRadians(track)) * gs;
        // Apply updateRate to movement
        movement.mul(1.0/updateRate);

        // Calculate position based on movement
        position.add(movement);
    }

    /**
     * Calculates airspeed, vertical speed and heading according to its corresponding delta.
     * Update rates are as follows:
     *  tas:    1
     *  vs:     1
     *  gs:     1
     * @param updateRate Amount of updates per second
     */
    public void updateDelta(int updateRate) {
        // Acceleration
        double zAcceleration = UfoSim.ACCELERATION / updateRate;
        double xyAcceleration = UfoSim.ACCELERATION / updateRate;
        // Max speed
        double yVMax = UfoSim.VMAX;
        double xzVMax = UfoSim.VMAX;

        DeltaUpdate speedUpdate;
        // True Air Speed
        if (deltaTas != 0) {
            speedUpdate = updateSpeed(tas, deltaTas, 0, xzVMax, xyAcceleration);
            tas = speedUpdate.value;
            deltaTas = speedUpdate.delta;
            // Set airspeed as ground speed
            gs = tas;
        }

        // Vertical Speed
        if (deltaVs != 0) {
            speedUpdate = updateSpeed(vs, deltaVs, -UfoSim.VMAX, yVMax, zAcceleration);
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
    public synchronized void reset() {
        position.set(0, 0, 0);
        movement.set(0, 0, 0);
        hdg = 0;
        track = 0;
        tas = 0;
        gs = 0;
        vs = 0;
        pitch = 0;
        deltaHdg = 0;
        deltaVs = 0;
        deltaTas = 0;
    }

    /* =================================================================================================================
     * Getter and setter
     * ============================================================================================================== */

    /**
     *
     * @return Position on x-axis in m
     */
    public synchronized double getX() {
        return position.x;
    }

    /**
     * Sets the position on x-axis.
     * @param x position in m
     */
    public synchronized void setX(double x) {
        this.position.x = x;
    }

    /**
     *
     * @return Position on y-axis in m
     */
    public synchronized double getY() {
        return position.y;
    }

    /**
     * Sets the position on y-axis.
     * @param y position in m
     */
    public synchronized void setY(double y) {
        this.position.y = y;
    }

    /**
     *
     * @return Position on z-axis in m
     */
    public synchronized double getZ() {
        return position.z;
    }

    /**
     * Sets the position on z-axis.
     * @param z position in m
     */
    public synchronized void setZ(double z) {
        this.position.z = z;
    }

    /**
     *
     * @return The current direction of travel in m/s
     */
    public synchronized Vector3 getMovement() {
        return movement;
    }

    /**
     *
     * @return The actual travel direction in deg
     */
    public synchronized double getTrack() {
        return track;
    }

    /**
     * Sets the track (true travel direction).
     * <p>
     *  NOTE:    This method does not change the heading and the track will be overwritten once {@link #updateDelta(int)}
     *           is called.<br>
     *           Consider using {@link #setHeading(double)} To make an affective change.
     * </p>
     *
     * @param track Track direction in deg
     */
    public synchronized void setTrack(double track) {
        this.track = track;
    }

    /**
     *
     * @return Heading in deg
     */
    public synchronized double getHeading() {
        return hdg;
    }

    /**
     * Sets the heading and sets the corresponding delta to zero..
     * @param hdg Heading in deg.
     */
    public synchronized void setHeading(double hdg) {
        this.hdg = hdg;
        this.deltaHdg = 0;
    }

    /**
     *
     * @return The true airspeed in m/s
     */
    public synchronized double getAirspeed() {
        return tas;
    }

    /**
     * Sets the airspeed and sets the corresponding delta to zero.
     * @param tas True airspeed in m/s. (0 <= tas)
     */
    public synchronized void setAirspeed(double tas) {
        this.tas = tas;
        this.deltaTas = 0;
    }

    /**
     *
     * @return The projected speed over ground in m/s
     */
    public synchronized double getGroundSpeed() {
        return gs;
    }

    /**
     * Sets the speed over ground.
     * <p>
     * NOTE:    This method does not change the airspeed and the ground speed will be overwritten once
     *          {@link #updateDelta(int)} is called.<br>
     *          Consider using {@link #setAirspeed(double)} To make an affective change.
     * </p>
     * @param gs Ground speed in m/s
     */
    public synchronized void setGroundSpeed(double gs) {
        this.gs = gs;
    }

    /**
     *
     * @return The vertical speed in m/s
     */
    public synchronized double getVerticalSpeed() {
        return vs;
    }

    /**
     * Sets the vertical speed and sets the corresponding delta to zero.
     * @param vs Vertical speed in m/s
     */
    public synchronized void setVerticalSpeed(double vs) {
        this.vs = vs;
        this.deltaVs = 0;
    }

    /**
     *
     * @return The inclination of the x-axis in deg
     */
    public synchronized double getPitch() {
        return pitch;
    }

    /**
     * Sets the pitch.
     * @param pitch Pitch in deg
     */
    public synchronized void setPitch(double pitch) {
        this.pitch = pitch;
    }

    /* -----------------------------------------------------------------------------------------------------------------
     * Deltas
     * -------------------------------------------------------------------------------------------------------------- */

    /**
     *
     * @return Requested, not applied change of heading in deg
     */
    public synchronized double getDeltaHeading() {
        return deltaHdg;
    }

    /**
     * Requests a change of heading. The change is summed up with all previous request changes.
     * A change will take affect over time.
     * @param delta Change in deg
     */
    public synchronized void requestDeltaHeading(double delta) {
        this.deltaHdg += delta;
    }

    /**
     *
     * @return The requested but not applied change of airspeed in m/s
     */
    public synchronized double getDeltaAirspeed() {
        return deltaTas;
    }

    /**
     * Requests a change of airspeed. The change is summed up with all previous request changes.
     * @param delta Change in m/s
     */
    public synchronized void requestDeltaAirspeed(double delta) {
        this.deltaTas += delta;
    }

    /**
     *
     * @return The requested but not applied change of vertical speed in m/s
     */
    public synchronized double getDeltaVerticalSpeed() {
        return deltaVs;
    }

    /**
     * Requests a change of vertical speed. The change is summed up with all previous request changes.
     * @param delta Change in m/s
     */
    public synchronized void requestDeltaVerticalSpeed(double delta) {
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
