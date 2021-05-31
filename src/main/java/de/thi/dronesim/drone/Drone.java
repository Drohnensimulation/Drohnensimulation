package de.thi.dronesim.drone;

public class Drone {

    /**
     * A Drone has a sort of Location State
     */
    private final Location location;

    public Drone() {
        location = new Location(0, 0, 0);
    }

    public Drone(float x, float y, float z) {
        location = new Location(x, y, z);
    }

    public Location getLocation() {
        return location;
    }

}
