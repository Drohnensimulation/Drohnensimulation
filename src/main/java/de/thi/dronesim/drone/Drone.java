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

    private static Drone instance;      // singleton instance

    public Drone(){

    }

    public Location getLocation() {
        return location;
    }
    public void setLocation(Location location) { this.location = location; }
}
