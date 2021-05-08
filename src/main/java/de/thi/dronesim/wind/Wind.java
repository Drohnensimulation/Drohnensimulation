package de.thi.dronesim.wind;

import de.thi.dronesim.ISimulationChild;
import de.thi.dronesim.Simulation;
import de.thi.dronesim.drone.Location;
import de.thi.dronesim.persistence.ConfigReader;
import de.thi.dronesim.persistence.entity.SimulationConfig;
import de.thi.dronesim.persistence.entity.WindConfig;

import javax.vecmath.Vector3d;
import java.util.*;

public class Wind implements ISimulationChild {

    protected static final double WIND_LAYER_INTERPOLATION_ALTITUDE_RANGE = 5;        // range in m
    protected static final double WIND_LAYER_INTERPOLATION_TIME_RANGE = 5;            // range in s

    //Main simulation
    private Simulation simulation;
    private boolean configLoaded = false;

    private List<WindLayer> windLayers;             // list of wind layers      [Windlayer]
    private int latestLayerId = 0;

    public Wind(String configPath) {
        loadFromConfig(configPath);
    }

    public Wind(List<WindLayer> layers) {
        this.configLoaded = true;
        this.windLayers = layers;
        load();
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
     *
     * <p>
     * By definition, each layer must be adjacent or at least 2 * WIND_LAYER_INTERPOLATION_ALTITUDE_RANGE apart from each other. Same applies for time.
     * </p>
     */
    private void load() {
        sortWindLayer();
        normalize();
        this.configLoaded = true;
    }

    /**
     * This function is loading a JSON File and converts it into a List<WindLayer>
     * @param configPath path to the WindLayer configuration File
     */
    private void loadFromConfig(String configPath) {
        SimulationConfig windSimulationConfig =  ConfigReader.readConfig(configPath);
        List<WindConfig> windConfigList = windSimulationConfig.getWindConfigList();
        for (WindConfig windConfig : windConfigList) {
            WindLayer windLayer = new WindLayer(windConfig.getWindSpeed(),
                    windConfig.getGustSpeed(), windConfig.getTimeStart(),
                    windConfig.getTimeEnd(), windConfig.getAltitudeBottom(),
                    windConfig.getAltitudeTop(), windConfig.getWindDirection());
            windLayers.add(windLayer);
        }
        load();
    }

    /**
     * This function sorts the given WindLayer by Time
     */
    private void sortWindLayer() {
        windLayers.sort(Comparator.comparing(WindLayer::getAltitudeBottom));
        windLayers.sort(Comparator.comparing(WindLayer::getTimeStart));
    }

    /**
     *
     * This Method normalizes the WindLayer already sorted, as defined in function load
     * The borders of the wind layers are rounded up to the doubled interpolation range
     */
    private void normalize() {
        final double altDistance = 2 * WIND_LAYER_INTERPOLATION_ALTITUDE_RANGE;
        final double timeDistance = 2 * WIND_LAYER_INTERPOLATION_TIME_RANGE;

        // Round time and altitude
        windLayers.forEach(WindLayer::normalize);

        Collection<WindLayer> removed = new ArrayList<>();
        for (int i = 0; i < windLayers.size(); i++) {
            WindLayer currentLayer = windLayers.get(i);
            // Check layer length is valid
            if (!currentLayer.isValid()) {
                removed.add(currentLayer);
                continue;
            }
            // Check if any following layer violates borders of current layers
            for (int x = i + 1; x < windLayers.size(); x++) {
                WindLayer followingLayer = windLayers.get(x);
                // Check if two layers overlap
                if (currentLayer.overlapsWith(followingLayer)) {
                    // Remove layer from list
                    removed.add(followingLayer);

                    // TODO use logger here
                    System.err.println("[Wind] Wind layer ("+x
                            +") violates the border of another layer and is therefore removed!");
                    System.out.println("[Wind] Borders of different wind layers must not overlap.");
                }
            }
        }
        windLayers.removeAll(removed);
    }

    public void reset() {
        latestLayerId = 0;
    }

    /**
     * Searches oldest layer by time
     * @param time Oldest time still in use
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
    public void applyWind(Location location) {
        int time = simulation.getTime();

        // Update oldest layer to set start point of search algorithm
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
            Vector3d lowerSpeed = interpolateTimeLayers(lowerPrevLayer, lowerNextLayer);
            Vector3d upperSpeed = interpolateTimeLayers(upperPrevLayer, upperNextLayer);

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
            Vector3d prevSpeed = interpolateAltitudeLayers(lowerPrevLayer, upperPrevLayer, location.getY(),
                    simulation.getTime());
            Vector3d nextSpeed = interpolateAltitudeLayers(lowerNextLayer, upperNextLayer, location.getY(),
                    simulation.getTime());

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

        Vector3d speedVector = createSpeedVector(location.getHeading(), location.getAirspeed());
        speedVector.add(windSpeedVector);


        double track = calculateAngleOfVector(speedVector);
        // Apply changes
        location.setTrack(track);
        location.setGroundSpeed(speedVector.length());
    }

    /**
     * 
     * @param direction Speed direction in deg
     * @param speed Speed in m/s
     * @return A vector with x for east speed and z for north speed
     */
    public static Vector3d createSpeedVector(double direction, double speed) {
        Vector3d vector = new Vector3d();
        vector.x = speed * Math.cos(Math.toRadians(direction));
        vector.y = 0;
        vector.z = speed * Math.sin(Math.toRadians(direction));
        return vector;
    }

    /**
     * Calculates the angle relative to north of a given vector
     * @param vector Vector to be converted
     * @return The angle in ged with 0° <= angle < 360°
     */
    public static double calculateAngleOfVector(Vector3d vector) {
        if (vector.x == 0)
            return (vector.z > 0) ? 90 : (vector.z == 0) ? 0 : 270;
        if (vector.z == 0)
            return  (vector.x >= 0) ? 0 : 180;

        double ang = Math.toDegrees(Math.atan(vector.z / vector.x));
        if (vector.x < 0 && vector.z < 0) // quadrant Ⅲ
            return 180 + ang;
        if (vector.x < 0) // quadrant Ⅱ
            return 180 + ang;
        if (vector.z < 0) // quadrant Ⅳ
            return 270 + (90 + ang);
        return ang;
    }

    private Vector3d interpolateTimeLayers(WindLayer prevLayer, WindLayer nextLayer) {
        int time = simulation.getTime();

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
    private Vector3d interpolateAltitudeLayers(WindLayer lowerLayer, WindLayer upperLayer, double altitude, int time) {
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

    protected List<WindLayer> getWindLayers() {
        return windLayers;
    }

    @Override
    public void setSimulation(Simulation simulation) {
        this.simulation = simulation;
    }

    @Override
    public Simulation getSimulation() {
        return this.simulation;
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
