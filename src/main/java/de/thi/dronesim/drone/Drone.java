package de.thi.dronesim.drone;

public class Drone {

    // TODO: Aktuell ein Dummy für ASensor, hier müssen die nötigen Werte aus UfoSim ausgelagert werden.
    /**
     * A Drone has a sort of Location State
     */
    private final Location location;
    /**
     * Drone's spherical hitbox radius for collision detection
     */
    private float radius;

    /**
     * default (0, 0, 0, 1)
     */
    public Drone() {
        location = new Location(0, 0, 0);
        radius = 1f;
    }
    
    /**
     * radius default value is 1
     * 
     * @deprecated use {@link #Drone(x, y, z, radius)} to manually define hitbox radius instead.
     */
    @Deprecated
    public Drone(float x, float y, float z) {
        location = new Location(x, y, z);
        this.radius = 1f;
    }
    
    public Drone(float x, float y, float z, float radius) {
    	location = new Location(x, y, z);
    	this.radius = radius;
    }

    public Location getLocation() {
        return location;
    }
    
    public float getRadius() {
		return radius;
	}

}
