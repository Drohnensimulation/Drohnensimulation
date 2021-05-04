package de.thi.dronesim.wind;

import de.thi.dronesim.ISimulationChild;
import de.thi.dronesim.Simulation;
import de.thi.dronesim.drone.Location;
import de.thi.dronesim.drone.UfoSim;

import java.util.Collections;
import java.util.List;

public class Wind implements ISimulationChild {

    private static final double WIND_LAYER_INTERPOLATION_ALTITUDE_RANGE = 5;        // range in m
    private static final double WIND_LAYER_INTERPOLATION_TIME_RANGE = 5;            // range in s

    //Main simulation
    private Simulation simulation;

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
    public void load() {
        // TODO check for overlapping and small gaps
    }

    private void normalize() {
        // TODO change height and time here according to definition #load
        // TODO round time to WIND_LAYER_INTERPOLATION_TIME_RANGE * 2
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
        double time = UfoSim.getInstance().getTime();

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

    @Override
    public void setSimulation(Simulation simulation) {
        this.simulation = simulation;
    }

    @Override
    public Simulation getSimulation() {
        return this.simulation;
    }

}
