package de.thi.dronesim.wind;

import de.thi.dronesim.ISimulationChild;
import de.thi.dronesim.Simulation;
import de.thi.dronesim.drone.Drone;
import de.thi.dronesim.drone.Location;
import de.thi.dronesim.persistence.ConfigReader;
import de.thi.dronesim.persistence.entity.SimulationConfig;
import de.thi.dronesim.persistence.entity.WindConfig;

import java.util.Comparator;
import java.util.List;
import java.util.ListIterator;

public class Wind implements ISimulationChild {

    private static final double WIND_LAYER_INTERPOLATION_ALTITUDE_RANGE = 5;        // range in m
    private static final double WIND_LAYER_INTERPOLATION_TIME_RANGE = 5;            // range in s

    //Main simulation
    private Simulation simulation;
    private boolean configLoaded = false;
    private String configPath;

    private List<WindLayer> windLayers;             // list of wind layers      [Windlayer]
    private int latestLayerId = 0;

    public Wind(String configPath) {
        this.configPath = configPath;
        load();
    }

    public Wind(List<WindLayer> layers){
        this.configLoaded = true;
        this.windLayers = layers;
    }

    /**
     * Interface for receiving Wind data based on the current location and simulation time
     * @param location current Location
     * @param time current Simulation Time
     * @return return WindChange if configLoaded, returns null if Config not loaded
     */
    public WindChange getCurrentWind(Location location, int time){
        if(this.configLoaded){
            applyWind(location);
            return new WindChange(location.getTrack(), location.getGroundSpeed());
        }else {
            return null;
        }
    }

    /**
     *
     * <p>
     * By definition, each layer must be adjacent or at least 2 * WIND_LAYER_INTERPOLATION_ALTITUDE_RANGE apart from each other. Same applies for time.
     * </p>
     *
     */
    private void load() {
        // TODO check for overlapping and small gaps
        loadFromConfig(this.configPath);
        sortWindLayer(windLayers);
        normalize();
        this.configLoaded = true;
    }

    /**
     * This function is loading a JSON File and converts it into a List<WindLayer>
     * @param configPath path to the WindLayer configuration File
     */
    private void loadFromConfig(String configPath){
        SimulationConfig windSimulationConfig =  ConfigReader.readConfig(configPath);
        List<WindConfig> windConfigList = windSimulationConfig.getWindConfigList();
        for(int i =0; i < windConfigList.size(); i++) {
            WindLayer windLayer = new WindLayer(windConfigList.get(i).getWindSpeed(),
                    windConfigList.get(i).getGustSpeed(), windConfigList.get(i).getTimeStart(),
                    windConfigList.get(i).getTimeEnd(), windConfigList.get(i).getAltitudeBottom(),
                    windConfigList.get(i).getAltitudeTop(), windConfigList.get(i).getWindDirection());
            windLayers.add(windLayer);
        }
    }

    /**
     * This function sorts the given WindLayer by Time
     * @param windLayers List of unsorted WindLayer
     */
    private void sortWindLayer(List<WindLayer> windLayers){
        windLayers.sort(Comparator.comparing(WindLayer::getTimeStart));
        sortWindLayerAltitudeBased();
    }

    /**
     * This function sorts the given WindLayer by Altitude
     */
    private void sortWindLayerAltitudeBased(){
        for (int i =0; i < windLayers.size() - 1; i++){
            for(int x = 0; x < windLayers.size() - i - 1; x++){
                if(windLayers.get(x).getAltitudeBottom() > windLayers.get(x+1).getAltitudeBottom()
                        && windLayers.get(x).getTimeEnd() >= windLayers.get(x + 1).getTimeStart()){
                    WindLayer tempLayer = windLayers.get(x);
                    windLayers.set(x, windLayers.get(x + 1));
                    windLayers.set(x + 1, tempLayer);
                }
            }
        }
    }

    /**
     * This Method normalizes the WindLayer already sorted, as defined in function load
     */
    private void normalize() {
        double altDistance = 2 * WIND_LAYER_INTERPOLATION_ALTITUDE_RANGE;
        double timeDistance = 2 * WIND_LAYER_INTERPOLATION_TIME_RANGE;

        for (int i = 0; i < windLayers.size() - 1; i++){
            for (int x = i + 1; x < windLayers.size() - 1; x++){
                WindLayer oldLayer = windLayers.get(i);
                WindLayer nextLayer = windLayers.get(x);
                if (oldLayer.getAltitudeTop() < nextLayer.getAltitudeBottom() + altDistance
                        &&(oldLayer.getTimeEnd() > nextLayer.getTimeStart() ||
                        oldLayer.getTimeEnd() + timeDistance <= nextLayer.getTimeEnd() &&
                                nextLayer.getTimeStart()  - oldLayer.getTimeEnd() <= timeDistance)){
                    nextLayer.setAltitudeBottom(oldLayer.getAltitudeTop() + altDistance);
                } else if(oldLayer.getTimeEnd() < nextLayer.getTimeStart() + timeDistance
                        &&(oldLayer.getAltitudeTop() > nextLayer.getAltitudeBottom() ||
                        oldLayer.getAltitudeTop() + altDistance <= nextLayer.getAltitudeTop() &&
                                nextLayer.getAltitudeBottom()  - oldLayer.getAltitudeTop() <= altDistance)){
                    nextLayer.setTimeStart(oldLayer.getTimeEnd() + timeDistance);
                }
            }
        }

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
    public void applyWind(Location location){
        double time = UfoSim.getInstance().getTime();

        // Update oldest layer to set start point of search algorithm
        updateLatestLayer(time - WIND_LAYER_INTERPOLATION_TIME_RANGE);

        // Find all 4 layers required. More layers can't have any effect by definition
        WindLayer lowerPrevLayer = findWindLayer(location.getZ() - WIND_LAYER_INTERPOLATION_ALTITUDE_RANGE,
                time - WIND_LAYER_INTERPOLATION_TIME_RANGE);
        WindLayer upperPrevLayer = findWindLayer(location.getZ() + WIND_LAYER_INTERPOLATION_ALTITUDE_RANGE,
                time - WIND_LAYER_INTERPOLATION_TIME_RANGE);
        WindLayer lowerNextLayer = findWindLayer(location.getZ() - WIND_LAYER_INTERPOLATION_ALTITUDE_RANGE,
                time + WIND_LAYER_INTERPOLATION_TIME_RANGE);
        WindLayer upperNextLayer = findWindLayer(location.getZ() + WIND_LAYER_INTERPOLATION_ALTITUDE_RANGE,
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
            WindChange upperChange = interpolateTimeLayers(lowerPrevLayer, lowerNextLayer, location, time);
            WindChange lowerChange = interpolateTimeLayers(upperPrevLayer, upperNextLayer, location, time);

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

    private WindChange interpolateTimeLayers(WindLayer prevLayer, WindLayer nextLayer, Location location, double time) {
        WindChange prevChange = WindLayer.applyOrZero(prevLayer, location);
        WindChange nextChange = WindLayer.applyOrZero(nextLayer, location);

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
        WindChange lowerChange = WindLayer.applyOrZero(lowerLayer, location);
        WindChange upperChange = WindLayer.applyOrZero(upperLayer, location);

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

    protected static final class WindChange {

        private final double track;
        private final double gs;

        protected WindChange(double track, double gs) {
            this.track = track;
            this.gs = gs;
        }

    }

    @Override
    public void setSimulation(Simulation simulation) {
        this.simulation = simulation;
    }

    @Override
    public Simulation getSimulation() {
        return this.simulation;
    }

}
