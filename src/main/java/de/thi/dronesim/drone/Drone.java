package de.thi.dronesim.drone;

public class Drone {

    // TODO: Aktuell ein Dummy für ASensor, hier müssen die nötigen Werte aus UfoSim ausgelagert werden.
    /**
     * A Drone has a sort of Location State
     */
    private final Location location;

    public Drone() {
        location = new Location(0, 0, 0);
    }

    public Location getLocation() {
        return location;
    }

}
