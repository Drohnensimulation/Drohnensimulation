package de.thi.dronesim.wind;

import de.thi.dronesim.ISimulationChild;
import de.thi.dronesim.Simulation;
import de.thi.dronesim.drone.Location;
import de.thi.dronesim.persistence.entity.WindConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.vecmath.Vector3d;
import java.util.*;

/**
 * @author Lausch, Christopher
 * @author Hupp, Laurence
 * @author Wittschen, Marvin
 */
public class Wind implements ISimulationChild {

    protected static final double WIND_LAYER_INTERPOLATION_ALTITUDE_RANGE = 5;        // range in m
    protected static final double WIND_LAYER_INTERPOLATION_TIME_RANGE = 5;            // range in s

    private final Logger logger = LogManager.getLogger();

    private Simulation simulation;                          // Main simulation
    private final List<WindLayer> windLayers;               // list of wind layers
    private int latestLayerId = 0;                          // Id of latest WindLayer to take into consideration based on time

    /**
     * Default constructor called by {@link Simulation}
     */
    public Wind() {
        windLayers = new ArrayList<>();
    }

    /**
     * Tester construction
     * @param layers List of WindLayers to be applies
     */
    protected Wind(List<WindLayer> layers) {
        this.windLayers = layers;
    }

    @Override
    public void initialize(Simulation simulation) {
        this.simulation = simulation;
        loadConfig();
        process();
        // Register update handler
        simulation.registerUpdateListener(event -> applyWind(event.getDrone().getLocation(), event.getTime()), 850);
    }

    @Override
    public void onSimulationStop() {
        latestLayerId = 0;
    }

    @Override
    public Simulation getSimulation() {
        return simulation;
    }

    /**
     * Calculates the wind direction and speed at the current time of a given location.
     * @param location Location of requested wind
     * @return Wind speed in m/s and direction in deg
     */
    public static Wind.CurrentWind getWindAt(Location location) {
        double tas = location.getAirspeed();
        double gs = location.getGroundSpeed();

        // No wind at all
        if (location.getGroundSpeed() == location.getAirspeed())
            return new CurrentWind(location.getHeading(), 0);

        // Nose or tailwind
        if (location.getHeading() == location.getTrack()) {
            double ws = location.getAirspeed() - location.getGroundSpeed();
            return new Wind.CurrentWind(
                    ws > 0 ? location.getHeading() : (location.getHeading() + 180) % 360,
                    Math.abs(ws));
        }
        // Wind correction angle
        double wca = 180 - Math.abs(Math.abs(location.getHeading() - location.getTrack()) - 180);
        if (Math.abs((location.getHeading() + wca) % 360 - location.getTrack()) > 0.001)  wca *= -1;
        // Calculate wind speed
        double ws = Math.sqrt(gs * gs + tas * tas - 2 * gs * tas * Math.cos(Math.toRadians(Math.abs(wca))));
        // Calculate wind angle
        double wa = Math.toDegrees(Math.asin(Math.sin(Math.toRadians(Math.abs(wca))) / ws * gs));
        // Use vertex angle for obtuse triangle
        if (Math.abs(wca) < 90 && gs > tas) wa = 180 - wa;
        // Calculate wind direction
        double wd = (location.getHeading() - wa *  Math.signum(wca) + 360) % 360;

        return new CurrentWind(wd, ws);
    }

    /**
     * Processes the WindLayers to be used in the simulation environment
     */
    private void process() {
        sortWindLayer();
        normalize();
    }

    /**
     * Load wind layers from config
     */
    private void loadConfig() {
        List<WindConfig> windConfigList = simulation.getConfig().getWindConfigList();
        if (windConfigList == null) return;
        for (WindConfig windConfig : windConfigList) {
            this.windLayers.add(new WindLayer(windConfig.getWindSpeed(), windConfig.getGustSpeed(),
                    windConfig.getTimeStart(), windConfig.getTimeEnd(),
                    windConfig.getAltitudeBottom(), windConfig.getAltitudeTop(),
                    windConfig.getWindDirection()));
        }
    }

    /**
     * This function sorts the given WindLayer by time and altitude, prioritizing time over altitude
     */
    private void sortWindLayer() {
        windLayers.sort(Comparator.comparing(WindLayer::getAltitudeBottom));
        windLayers.sort(Comparator.comparing(WindLayer::getTimeStart));
    }

    /**
     * This Method normalizes the  already sorted WindLayers.
     * <ul>
     *     <li>The borders of the wind layers are rounded up to the doubled interpolation range</li>
     *     <li>Layers with overlapping borders are being removed</li>
     *     <li>Layers with invalid values are being removed</li>
     * </ul>
     * @see Wind#WIND_LAYER_INTERPOLATION_TIME_RANGE
     * @see Wind#WIND_LAYER_INTERPOLATION_ALTITUDE_RANGE
     */
    private void normalize() {
        // Round time and altitude
        windLayers.forEach(WindLayer::normalize);

        Collection<WindLayer> removed = new ArrayList<>();
        for (int i = 0; i < windLayers.size(); i++) {
            WindLayer currentLayer = windLayers.get(i);
            // Check if layer is already removed
            if (removed.contains(currentLayer)) {
                continue;
            }

            // Check layer length is valid
            if (!currentLayer.isValid()) {
                removed.add(currentLayer);
                logger.warn("Wind layer ({}) is invalid and therefore removed!", i);
                continue;
            }
            // Check if any following layer violates borders of current layers
            for (int x = i + 1; x < windLayers.size(); x++) {
                WindLayer followingLayer = windLayers.get(x);
                // Check if two layers overlap
                if (currentLayer.overlapsWith(followingLayer)) {
                    // Remove layer from list
                    removed.add(followingLayer);
                    logger.warn("Wind layer ({}) violates the border of layer ({}) and is therefore removed!", x, i);
                    logger.warn("Borders of different wind layers must not overlap.");
                }
            }
        }
        // Remove preciously collected invalid layers
        windLayers.removeAll(removed);
    }

    /**
     * Searches for latest layer based on time
     * @param time Latest time taken into consideration
     */
    private void updateLatestLayer(double time) {
        for (int i = latestLayerId; i < windLayers.size(); i++) {
            if (windLayers.get(i).getTimeEnd() <= time) {
                // Increment id as this layer will not be used anymore
                latestLayerId = i;
            } else {
                break;
            }
        }
    }

    /**
     * @param alt Altitude at which to search the layer
     * @param time Time at which to search for the layer
     * @return The layer the the given altitude and time or null if no layer was found
     */
    private WindLayer findWindLayer(double alt, double time) {
        for (ListIterator<WindLayer> it = windLayers.listIterator(latestLayerId); it.hasNext(); ) {
            WindLayer layer = it.next();
            if (layer.getAltitudeBottom() <= alt
                    && layer.getAltitudeTop() > alt
                    && layer.getTimeStart() <= time
                    && layer.getTimeEnd() > time) {
                return layer;
            }

            // As layers are sorted by start time, we can stop the search if time excites start time here
            if (layer.getTimeStart() > time) break;
        }
        return null;
    }

    /**
     * Applies wind based on the current location.
     * @param location Location of the drone
     */
    public void applyWind(Location location, double time) {
        time /= 1000;

        // Update latest layer based on time to set start point of search algorithm
        updateLatestLayer(time - WIND_LAYER_INTERPOLATION_TIME_RANGE);

        // Find all 4 layers required. More layers can't have any effect by definition
        WindLayer lowerPrevLayer = findWindLayer(location.getY() - WIND_LAYER_INTERPOLATION_ALTITUDE_RANGE,
                time - WIND_LAYER_INTERPOLATION_TIME_RANGE);
        WindLayer upperPrevLayer = findWindLayer(location.getY() + WIND_LAYER_INTERPOLATION_ALTITUDE_RANGE,
                time - WIND_LAYER_INTERPOLATION_TIME_RANGE);
        WindLayer lowerNextLayer = findWindLayer(location.getY() - WIND_LAYER_INTERPOLATION_ALTITUDE_RANGE,
                time + WIND_LAYER_INTERPOLATION_TIME_RANGE);
        WindLayer upperNextLayer = findWindLayer(location.getY() + WIND_LAYER_INTERPOLATION_ALTITUDE_RANGE,
                time + WIND_LAYER_INTERPOLATION_TIME_RANGE);

        // In case no layer was found, no wind applies
        if (lowerPrevLayer == null && upperPrevLayer == null && lowerNextLayer == null && upperNextLayer == null) {
            // Set track to hdg, gs to tas
            location.setTrack(location.getHeading());
            location.setGroundSpeed(location.getAirspeed());
            return;
        }

        // Check if it is preferred to interpolate time first
        boolean timeInterpolationFirst = false;
        if (lowerPrevLayer != null && upperNextLayer != null) {
            timeInterpolationFirst = lowerPrevLayer.getTimeEnd() != upperNextLayer.getTimeStart();
        } else if (upperPrevLayer != null && lowerNextLayer != null) {
            timeInterpolationFirst = upperPrevLayer.getTimeEnd() != lowerNextLayer.getTimeStart();
        } else if ((lowerNextLayer != null && upperNextLayer != null)
                || (lowerPrevLayer != null && upperPrevLayer != null)) {
            timeInterpolationFirst = true;
        }

        Vector3d windSpeedVector = new Vector3d();
        if (timeInterpolationFirst) {
            Vector3d lowerSpeed = interpolateTimeLayers(lowerPrevLayer, lowerNextLayer, time);
            Vector3d upperSpeed = interpolateTimeLayers(upperPrevLayer, upperNextLayer, time);

            // If both changes are identical or zero, no further interpolation is needed
            if (lowerSpeed.equals(upperSpeed)) {
                windSpeedVector = lowerSpeed;
            } else if (lowerSpeed.length() != 0 || upperSpeed.length() != 0) {
                // Either lowerPrevLayer or lowerNextLayer has to be not null as timeInterpolationFirst is set to true
                double ref = lowerPrevLayer != null ? lowerPrevLayer.getAltitudeTop() : lowerNextLayer.getAltitudeTop();
                // Interpolate time
                windSpeedVector = interpolate(lowerSpeed, upperSpeed,
                        location.getY() - ref,
                        WIND_LAYER_INTERPOLATION_TIME_RANGE);
            }
        } else {
            // Interpolate altitude first
            Vector3d prevSpeed = interpolateAltitudeLayers(lowerPrevLayer, upperPrevLayer, location.getY(), time);
            Vector3d nextSpeed = interpolateAltitudeLayers(lowerNextLayer, upperNextLayer, location.getY(), time);

            // If both changes are identical or zero, no further interpolation is needed
            if (prevSpeed.equals(nextSpeed)) {
                windSpeedVector = prevSpeed;
            } else if (prevSpeed.length() != 0 || nextSpeed.length() != 0) {
                // At least one layer has to be not null
                double ref = lowerPrevLayer != null ? lowerPrevLayer.getTimeEnd() :
                        (lowerNextLayer != null ? lowerNextLayer.getTimeStart() :
                                (upperPrevLayer != null ? upperPrevLayer.getTimeEnd() : upperNextLayer.getTimeStart()));
                // Interpolate time
                windSpeedVector = interpolate(prevSpeed, nextSpeed,
                        time - ref,
                        WIND_LAYER_INTERPOLATION_TIME_RANGE);
            }
        }

        Vector3d speedVector = createSpeedVector(location.getHeading(), location.getAirspeed(),
                location.getVerticalSpeed());
        speedVector.add(windSpeedVector);

        double track = calculateAngleOfVector(speedVector);
        // Apply changes
        location.setTrack(track);
        location.setGroundSpeed(Math.sqrt(speedVector.x * speedVector.x + speedVector.z * speedVector.z));
        location.setVerticalSpeed(speedVector.y);
    }

    /**
     * Creates a vector out of direction, airspeed and vertical speed
     * @param direction Speed direction in deg
     * @param speed Speed in m/s
     * @return A vector with x for east speed and z for north speed
     */
    public static Vector3d createSpeedVector(double direction, double speed, double verticalSpeed) {
        Vector3d vector = new Vector3d();
        vector.x = speed * Math.cos(Math.toRadians(direction));
        vector.y = verticalSpeed;
        vector.z = speed * Math.sin(Math.toRadians(direction));
        return vector;
    }

    /**
     * Calculates the angle relative to north of a given vector
     * @param vector Vector to be converted
     * @return The angle in ged with 0?? <= angle < 360??
     */
    public static double calculateAngleOfVector(Vector3d vector) {
        if (vector.x == 0)
            return (vector.z > 0) ? 90 : (vector.z == 0) ? 0 : 270;
        if (vector.z == 0)
            return  (vector.x >= 0) ? 0 : 180;

        double angle = Math.toDegrees(Math.atan(vector.z / vector.x));
        if (vector.x < 0 && vector.z < 0) // quadrant ???
            return 180 + angle;
        if (vector.x < 0) // quadrant ???
            return 180 + angle;
        if (vector.z < 0) // quadrant ???
            return 270 + (90 + angle);
        return angle;
    }

    /**
     * Interpolates two layers based on time
     * @param prevLayer Layer before current point of time
     * @param nextLayer Layer after current point of time
     * @param time Current simulation time
     * @return The interpolated wind speed vector
     */
    private Vector3d interpolateTimeLayers(WindLayer prevLayer, WindLayer nextLayer, double time) {
        Vector3d prevSpeed = WindLayer.convertSpeedOrZero(prevLayer, time);
        // Check if layers are identical in which case an interpolation would be unnecessary
        if (prevLayer == nextLayer)
            return prevSpeed;
        Vector3d nextSpeed = WindLayer.convertSpeedOrZero(nextLayer, time);
        // Interpolate between both vectors
        double ref = prevLayer != null ? prevLayer.getTimeEnd() : nextLayer.getTimeStart();
        return interpolate(prevSpeed, nextSpeed,
                time - ref,
                WIND_LAYER_INTERPOLATION_ALTITUDE_RANGE);
    }

    /**
     * Interpolates between two layers based on their altitude
     * @param lowerLayer Lower wind layer
     * @param upperLayer Upper wind layer (above lower layer)
     * @param altitude Altitude of reference point
     * @return A vector with the interpolated wind speed
     */
    private Vector3d interpolateAltitudeLayers(WindLayer lowerLayer, WindLayer upperLayer, double altitude, double time) {
        Vector3d prevSpeed = WindLayer.convertSpeedOrZero(lowerLayer, time);
        // Check if layers are identical in which case an interpolation would be unnecessary
        if (lowerLayer == upperLayer)
            return prevSpeed;
        Vector3d nextSpeed = WindLayer.convertSpeedOrZero(upperLayer, time);
        // Interpolate between both vectors
        double borderAlt = upperLayer != null ? upperLayer.getAltitudeBottom() : lowerLayer.getAltitudeTop();
        return interpolate(prevSpeed, nextSpeed,
                altitude - borderAlt,
                WIND_LAYER_INTERPOLATION_ALTITUDE_RANGE);
    }

    /**
     * Interpolates two speed vectors based on the position.
     * @param first First speed vector
     * @param second Second speed vector
     * @param x Requested position for interpolation relative to the border
     * @param range Interpolation range
     * @return A vector with the result of the interpolation
     */
    private Vector3d interpolate(Vector3d first, Vector3d second, double x, double range) {
        Vector3d result = new Vector3d();
        first.scale(-0.5 / range * x + 0.5);
        second.scale(0.5 / range * x + 0.5);
        result.add(first);
        result.add(second);
        return result;
    }

    /**
     * @return A list of wind layers. After simulation initialization, the list is sorted and normalized
     */
    public List<WindLayer> getWindLayers() {
        return windLayers;
    }

    public static final class CurrentWind {

        private final double windDirection;
        private final double windSpeed;

        /**
         *
         * @param windDirection Wind direction in deg
         * @param windSpeed Wind speed in m/s
         */
        public CurrentWind(double windDirection, double windSpeed) {
            this.windDirection = windDirection;
            this.windSpeed = windSpeed;
        }

        /**
         *
         * @return Wind speed in m/s
         */
        public double getWindSpeed() {
            return windSpeed;
        }

        /**
         *
         * @return Wind direction in deg
         */
        public double getWindDirection() {
            return windDirection;
        }
    }

}
