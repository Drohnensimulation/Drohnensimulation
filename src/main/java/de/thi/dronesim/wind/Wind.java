package de.thi.dronesim.wind;

import de.thi.dronesim.ufo.Location;
import de.thi.dronesim.ufo.UfoSim;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Wind {

    private static final double WIND_LAYER_INTERPOLATION_ALTITUDE_RANGE = 5;        // range in m
    private static final double WIND_LAYER_INTERPOLATION_TIME_RANGE = 5;            // range in s

    private List<WindLayer> windLayers;             // list of wind layers      [Windlayer]

    public Wind() {

    }

    public Wind(List<WindLayer> layers){
        this.windLayers = layers;
    }

    /**
     *
     * <p>
     * By definition, each layer must be adjacent or at least 2 * WIND_LAYER_INTERPOLATION_ALTITUDE_RANGE apart from each other. Same applies for time.
     * </p>
     *
     */
    public void load(String configPath) {
        // TODO check for overlapping and small gaps
        loadFromConfig(configPath);
        sortWindLayer(windLayers);
        normalize();
    }

    private void loadFromConfig(String configPath){
        List windConfigList = ConfigReader.readConfig(configPath);
        for(int i =0; i < windConfigList.size(); i++) {
            WindLayer windLayer = new WindLayer(windConfigList.get(i).windspeed,
                    windConfigList.get(i).gustspeed, windConfigList.get(i).timeStart,
                    windConfigList.get(i).timeEnd, windConfigList.get(i).altitudeBottom,
                    windConfigList.get(i).altitudeTop, windConfigList.get(i).windDirection);
            windLayers.add(windLayer);
        }
    }

    private void sortWindLayer(List<WindLayer> windLayers){
        windLayers.sort(Comparator.comparing(WindLayer::getTimeStart));
        sortWindLayerAltitudeBased();
    }

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

    private void normalize() {
        double altDistance = 2 * WIND_LAYER_INTERPOLATION_ALTITUDE_RANGE;
        double timeDistance = 2 * WIND_LAYER_INTERPOLATION_TIME_RANGE;

        for (int i =0; i < windLayers.size() - 1; i++){
            for(int x = i + 1; x < windLayers.size() - 1; x++){
                WindLayer oldLayer = windLayers.get(i);
                WindLayer nextLayer = windLayers.get(x);
                if(oldLayer.getAltitudeTop() < nextLayer.getAltitudeBottom() + altDistance
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

    private WindLayer findWindLayer(double alt, double time) {
        // TODO implement better search algorithm
        for (WindLayer layer: windLayers) {
            if (layer.getAltitudeBottom() <= alt
                    && layer.getAltitudeTop() > alt
                    && layer.getTimeStart() <= time
                    && layer.getTimeEnd() > time) {
                return layer;
            }
        }
        return null;
    }

    /**
     * applies wind based on the current location
     * @param location
     */
    public void applyWind(Location location){
        double time = UfoSim.getInstance().getTime(); // TODO needs to be adjusted because it is desperate

        // TODO introduce caching
        WindLayer lowerPrevLayer = findWindLayer(location.getZ() - WIND_LAYER_INTERPOLATION_ALTITUDE_RANGE,
                time - WIND_LAYER_INTERPOLATION_TIME_RANGE);
        WindLayer upperPrevLayer = findWindLayer(location.getZ() + WIND_LAYER_INTERPOLATION_ALTITUDE_RANGE,
                time - WIND_LAYER_INTERPOLATION_TIME_RANGE);
        WindLayer lowerNextLayer = findWindLayer(location.getZ() - WIND_LAYER_INTERPOLATION_ALTITUDE_RANGE,
                time + WIND_LAYER_INTERPOLATION_TIME_RANGE);
        WindLayer upperNextLayer = findWindLayer(location.getZ() + WIND_LAYER_INTERPOLATION_ALTITUDE_RANGE,
                time + WIND_LAYER_INTERPOLATION_TIME_RANGE);

        WindChange prevChange = calculateAltitudeChange(lowerPrevLayer, upperPrevLayer, location);
        WindChange nextChange = calculateAltitudeChange(lowerNextLayer, upperNextLayer, location);

        WindChange windChange;
        if (prevChange != null || nextChange != null) {
            // Check if one layer is a null layer
            if (prevChange == null) {
                prevChange = new WindChange(nextChange.track, 0);
            } else if (nextChange == null) {
                nextChange = new WindChange(prevChange.track, 0);
            }
            windChange = interpolate(prevChange, nextChange, 0, WIND_LAYER_INTERPOLATION_TIME_RANGE);
        } else {
            // Calm day today
            windChange = new WindChange(location.getHeading(), location.getAirspeed());
        }

        // Apply changes
        location.setTrack(windChange.track);
        location.setGroundSpeed(windChange.gs);
    }

    private WindChange calculateAltitudeChange(WindLayer lowerLayer, WindLayer upperLayer, Location location) {
        WindChange lowerChange;
        WindChange upperChange;

        if (lowerLayer != null || upperLayer != null) {
            double alt;
            if (lowerLayer == null) {
                upperChange = upperLayer.applyForces(location);
                lowerChange = new WindChange(upperChange.track, 0);
                alt = upperLayer.getAltitudeBottom();
            } else if (upperLayer == null) {
                lowerChange = lowerLayer.applyForces(location);
                upperChange = new WindChange(lowerChange.track, 0);
                alt = lowerLayer.getAltitudeTop();
            } else {
                lowerChange = lowerLayer.applyForces(location);
                upperChange = upperLayer.applyForces(location);
                alt = lowerLayer.getAltitudeTop();
            }
            return interpolate(lowerChange, upperChange,
                    location.getZ() - alt,
                    WIND_LAYER_INTERPOLATION_ALTITUDE_RANGE);
        }
        return null;
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

    public List<WindLayer> getWindLayers(){
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

}
