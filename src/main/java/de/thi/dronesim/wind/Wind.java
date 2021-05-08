package de.thi.dronesim.wind;

import de.thi.dronesim.ISimulationChild;
import de.thi.dronesim.Simulation;
import de.thi.dronesim.drone.Location;
import de.thi.dronesim.persistence.ConfigReader;
import de.thi.dronesim.persistence.entity.SimulationConfig;
import de.thi.dronesim.persistence.entity.WindConfig;

import java.util.*;

public class Wind implements ISimulationChild {

    private static final double WIND_LAYER_INTERPOLATION_ALTITUDE_RANGE = 5;        // range in m
    private static final double WIND_LAYER_INTERPOLATION_TIME_RANGE = 5;            // range in s

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
        if ((location.getHeading() + wca) % 360 != location.getTrack()) wca *= -1;
        // Calculate wind speed
        double ws = Math.sqrt(gs * gs + tas * tas - 2 * gs * tas * Math.cos(Math.toRadians(Math.abs(wca))));
        // Calculate wind angle
        double wa = Math.toDegrees(Math.asin(Math.sin(Math.toRadians(Math.abs(wca))) / ws * gs));
        // Use vertex angle for obtuse triangle
        if (Math.abs(wca) < 90 && gs > tas) wa = 180 - wa;
        // Calculate wind direction
        double wd = (location.getHeading() - wa *  Math.signum(wca) + 360) % 360;

        return new CurrentWind(wa, wd);
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
//        windLayers.forEach();
        for (WindLayer windLayer : windLayers) {
            windLayer.setAltitudeBottom(Math.ceil(windLayer.getAltitudeBottom() / altDistance) * altDistance);
            windLayer.setAltitudeTop(Math.ceil(windLayer.getAltitudeTop() / altDistance) * altDistance);
            windLayer.setTimeStart(Math.ceil(windLayer.getTimeStart() / timeDistance) * timeDistance);
            windLayer.setTimeEnd(Math.ceil(windLayer.getTimeEnd() / timeDistance) * timeDistance);
        }

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

        boolean timeInterpolationFirst = false;
        if (lowerPrevLayer != null && upperNextLayer != null) {
            timeInterpolationFirst = lowerPrevLayer.getTimeEnd() != upperNextLayer.getTimeStart();
        } else if (upperPrevLayer != null && lowerNextLayer != null) {
            timeInterpolationFirst = upperPrevLayer.getTimeEnd() != lowerNextLayer.getTimeStart();
        } else if ((lowerNextLayer != null && upperNextLayer != null)
                || (lowerPrevLayer != null && upperPrevLayer != null)) {
            timeInterpolationFirst = true;
        }

        WindChange windChange;
        if (timeInterpolationFirst) {
            WindChange upperChange = interpolateTimeLayers(lowerPrevLayer, lowerNextLayer, location);
            WindChange lowerChange = interpolateTimeLayers(upperPrevLayer, upperNextLayer, location);

            // If both changes are zero, no further interpolation is needed
            if (upperChange.gs == 0 && lowerChange.gs == 0) {
                windChange = upperChange;
            } else {
                // Either lowerPrevLayer or lowerNextLayer has to be not null as timeInterpolationFirst is set to true
                double ref = lowerPrevLayer != null ? lowerPrevLayer.getAltitudeTop() : lowerNextLayer.getAltitudeTop();
                // Interpolate time
                windChange = interpolate(upperChange, lowerChange,
                        location.getY() - ref,
                        WIND_LAYER_INTERPOLATION_TIME_RANGE);
            }
        } else {
            // Interpolate altitude first
            WindChange prevChange = interpolateAltitudeLayers(lowerPrevLayer, upperPrevLayer, location);
            WindChange nextChange = interpolateAltitudeLayers(lowerNextLayer, upperNextLayer, location);

            // If both changes are zero, no further interpolation is needed
            if (prevChange.gs == 0 && nextChange.gs == 0) {
                windChange = prevChange;
            } else {
                // At least one layer has to be not null
                double ref = lowerPrevLayer != null ? lowerPrevLayer.getTimeEnd() :
                        (lowerNextLayer != null ? lowerNextLayer.getTimeStart() :
                                (upperPrevLayer != null ? upperPrevLayer.getTimeEnd() : upperNextLayer.getTimeStart()));
                // Interpolate time
                windChange = interpolate(prevChange, nextChange,
                        time - ref,
                        WIND_LAYER_INTERPOLATION_TIME_RANGE);
            }
        }

        // Apply changes
        location.setTrack(windChange.track);
        location.setGroundSpeed(windChange.gs);
    }

    private WindChange interpolateTimeLayers(WindLayer prevLayer, WindLayer nextLayer, Location location) {
        double time = simulation.getTime();
        WindChange prevChange = WindLayer.applyOrZero(prevLayer, location, time);
        WindChange nextChange = WindLayer.applyOrZero(nextLayer, location, time);

        if (prevLayer != null || nextLayer != null) {
            double ref = prevLayer != null ? prevLayer.getTimeEnd() : nextLayer.getTimeStart();
            return interpolate(prevChange, nextChange,
                    time - ref,
                    WIND_LAYER_INTERPOLATION_ALTITUDE_RANGE);
        }
        // No layer applies so no wind is set
        return new WindChange(location.getHeading(), 0);
    }

    private WindChange interpolateAltitudeLayers(WindLayer lowerLayer, WindLayer upperLayer, Location location) {
        WindChange lowerChange = WindLayer.applyOrZero(lowerLayer, location, simulation.getTime());
        WindChange upperChange = WindLayer.applyOrZero(upperLayer, location, simulation.getTime());

        if (lowerLayer != null || upperLayer != null) {
            double alt = upperLayer != null ? upperLayer.getAltitudeBottom() : lowerLayer.getAltitudeTop();
            return interpolate(lowerChange, upperChange,
                    location.getZ() - alt,
                    WIND_LAYER_INTERPOLATION_ALTITUDE_RANGE);
        }
        // If both layers are null, each change is a zero change.
        return lowerChange;
    }

    private WindChange interpolate(WindChange first, WindChange second, double x, double range) {

        // Calculate change in track
        double trackDiff = (((second.track - first.track) + 540) % 360) - 180;

        // Calculate change of track and ground speed
        double dTrack = (trackDiff / (range * 2) * (x - range));
        double dgs = (second.gs - first.gs)/(range * 2) * (x - range);

        // Calculate new track and ground speed
        double track = (second.track + dTrack + 360) % 360;
        double gs = second.gs + dgs;
        return new WindChange(track, gs);
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

    protected static final class WindChange {

        private final double track;
        private final double gs;

        protected WindChange(double track, double gs) {
            this.track = track;
            this.gs = gs;
        }

    }

    public static final class CurrentWind {

        private final double windDirection;
        private final double windSpeed;

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
