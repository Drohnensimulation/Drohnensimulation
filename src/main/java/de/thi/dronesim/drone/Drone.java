package de.thi.dronesim.drone;

import de.thi.dronesim.sensor.ASensor;

import java.util.List;

public class Drone {

    // TODO: Aktuell ein Dummy für ASensor, hier müssen die nötigen Werte aus UfoSim ausgelagert werden.
    /**
     * A Drone has a sort of Location State
     */
    private Location location;

    /**
     * A Drone has an Collections of Sensors attached to it
     */
    private List<ASensor> sensors;

    private volatile double time;        // elapsed flight time with v > 0 since reset [s]
    private static Drone instance;      // singleton instance

    public Drone(){

    }

    /**
     * returns Simulation time
     * @author Laurence Hupp
     * @return time
     */
    public double getTime() {
        return time;
    }

    /**
     * get the current Instance of the Drone
     * @author Laurence Hupp
     * @return Drone Instance
     */
    public static Drone getInstance() {
        if (instance == null) {
            instance = new Drone();
        }

        return instance;
    }
}
