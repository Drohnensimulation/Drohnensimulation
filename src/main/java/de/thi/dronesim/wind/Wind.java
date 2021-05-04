package de.thi.dronesim.wind;

import de.thi.dronesim.ufo.Location;
import de.thi.dronesim.ufo.UfoSim;

import java.util.Collections;
import java.util.List;
import java.util.ListIterator;

public class Wind {

    private static final double WIND_LAYER_INTERPOLATION_ALTITUDE_RANGE = 5;        // range in m
    private static final double WIND_LAYER_INTERPOLATION_TIME_RANGE = 5;            // range in s

    private List<WindLayer> windLayers;             // list of wind layers      [Windlayer]
    private int latestLayerId = 0;

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
    public void load() {
        // TODO check for overlapping and small gaps
    }

    private void normalize() {
        // TODO change height and time here according to definition #load
        // TODO round time to WIND_LAYER_INTERPOLATION_TIME_RANGE * 2
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

    public void sortWindLayer(){
        Collections.sort(windLayers);
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

}
