package de.thi.dronesim.drone;

import de.thi.dronesim.sensor.types.DistanceSensor;

import java.util.List;

public class Drone {

    // TODO: Aktuell ein Dummy für ASensor, hier müssen die nötigen Werte aus UfoSim ausgelagert werden.
    /**
     * A Drone has a sort of Location State
     */
    private Location location;
    private float heading;
    private float movementDirection;
    private float verticalSpeed; //how to set apart going up and going down? 
    private float horizontalSpeed;

	/**
     * A Drone has an Collections of Sensors attached to it
     */
    private List<DistanceSensor> sensors;

    private static Drone instance;      // singleton instance

    public Drone(){

    }
    
    public Location getLocation() {
    	return this.location;
    }
    
    public float getHeading() {
		return heading;
	}

	public float getMovementDirection() {
		return movementDirection;
	}

	public float getVerticalSpeed() {
		return verticalSpeed;
	}

	public float getHorizontalSpeed() {
		return horizontalSpeed;
	}
}
