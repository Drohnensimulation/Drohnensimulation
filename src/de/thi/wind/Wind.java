package de.thi.wind;

import de.thi.ufo.Location;

import java.util.Collections;
import java.util.List;

public class Wind {

    private static final WindLayer noWind = new WindLayer(0, 0, 0, 0, 0, 0, 0);

    private List<WindLayer> windLayers;             // list of wind layers      [Windlayer]

    public Wind(){

    }

    public void load() {

    }

    /**
     * applies wind based on the current location
     * @param location
     */
    public void applyWind(Location location){

        WindLayer lower = null;
        WindLayer upper = null;

        for (WindLayer layer : windLayers) {
            if (layer.getAltitudeBottom() <= location.getY()) {
                lower = layer;
            }
            if (layer.getAltitudeTop() >= location.getY()) {
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
            change = interpolate(noWind, upper);
        } else if (upper == null) {
            change = interpolate(lower, noWind);
        } else {
            change = interpolate(lower, upper);
        }

        location.setGroundSpeed(change.gs);
        location.setTrack(change.track);



        // TODO interpolate between lower and upper

    }

    public void sortWindLayer(){
        Collections.sort(windLayers);
    }

    private WindChange interpolate(WindLayer upper, WindLayer lower) {

        return WindChange.NO_CHANGE;

    }

    protected static final class WindChange {

        protected static final WindChange NO_CHANGE = new WindChange(0, 0);

        private final double track;
        private final double gs;

        protected WindChange(double track, double gs) {
            this.track = track;
            this.gs = gs;
        }

    }

}
