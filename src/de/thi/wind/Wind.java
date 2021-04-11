package de.thi.wind;

import de.thi.ufo.Location;
import de.thi.ufo.UfoSim;

import java.util.Collections;
import java.util.List;

public class Wind {

    private static final WindLayer NO_WIND = new WindLayer(0, 0, 0, 0, 0, 0, 0);
    private static final double WIND_LAYER_INTERPOLATION_RANGE = 5;


    private List<WindLayer> windLayers;             // list of wind layers      [Windlayer]

    public Wind(){

    }

    public Wind(List<WindLayer> layers){
        this.windLayers = layers;
    }

    /**
     *
     * <p>
     * By definition, each layer must be adjacent or at least 5m apart from each other
     * </p>
     *
     */
    public void load() {
        // TODO check for overlapping and small gaps
    }

    /**
     * applies wind based on the current location
     * @param location
     */
    public void applyWind(Location location){

        WindLayer lower = null;
        WindLayer upper = null;

        for (WindLayer layer : windLayers) {
            if (layer.getAltitudeBottom() <= location.getZ() - WIND_LAYER_INTERPOLATION_RANGE
                    && (lower == null || layer.getAltitudeBottom() > lower.getAltitudeBottom())) {
                lower = layer;
            }
            if (layer.getAltitudeTop() >= location.getZ() + WIND_LAYER_INTERPOLATION_RANGE
                    && (upper == null || layer.getAltitudeTop() < upper.getAltitudeTop())) {
                upper = layer;
            }
            // Drone in
            if (lower == upper) {
                WindChange change = layer.applyForces(location);
                location.setGroundSpeed(change.gs);
                location.setTrack(change.track);
                return;
            }
        }
        // No suitable layers found
        if (lower == null && upper == null) {
            return;
        }

        WindChange change;
        if (lower == null) {
            lower = upper.copy();
            lower.setAltitudeTop(location.getZ() - WIND_LAYER_INTERPOLATION_RANGE);
            lower.setWindSpeed(0);
            lower.setGustSpeed(0);
        } else if (upper == null) {
            upper = lower.copy();
            upper.setAltitudeBottom(location.getZ() + WIND_LAYER_INTERPOLATION_RANGE);
            upper.setWindSpeed(0);
            upper.setGustSpeed(0);
        }
        change = interpolate(lower, upper, location);

        location.setGroundSpeed(change.gs);
        location.setTrack(change.track);

        // TODO interpolate between lower and upper

    }

    public void sortWindLayer(){
        Collections.sort(windLayers);
    }

    private WindChange interpolate(WindLayer firstLayer, WindLayer secondLayer, Location location) {
        WindChange first = secondLayer.applyForces(location);
        WindChange second = firstLayer.applyForces(location);

        // Calculate change in track
        double trackDiff = (((second.track - first.track) + 540) % 360) - 180;

        // Calculate change of track and ground speed
        double dTrack = (trackDiff / (WIND_LAYER_INTERPOLATION_RANGE * 2)
                * (location.getZ() - firstLayer.getAltitudeTop() - WIND_LAYER_INTERPOLATION_RANGE));
        double dgs = (second.gs - first.gs)/(WIND_LAYER_INTERPOLATION_RANGE * 2)
                * (location.getZ() - firstLayer.getAltitudeTop() - WIND_LAYER_INTERPOLATION_RANGE);

        // Calculate new track and ground speed
        double track = (second.track + dTrack + 360) % 360;
        double gs = second.gs + dgs;
        return new WindChange(track, gs);
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
