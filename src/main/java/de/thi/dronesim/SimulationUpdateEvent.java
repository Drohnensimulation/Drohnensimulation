package de.thi.dronesim;

import de.thi.dronesim.drone.Drone;

public class SimulationUpdateEvent {

    private final Drone drone;
    private final double time;
    private final int tps;

    public SimulationUpdateEvent(Drone drone, double time, int tps) {
        this.drone = drone;
        this.time = time;
        this.tps = tps;
    }

    /**
     * @return Drone for which the update was called
     */
    public Drone getDrone() {
        return drone;
    }

    /**
     * @return Passed simulation time in s
     */
    public double getTime() {
        return time;
    }

    /**
     * @return Simulation update rate in ticks per second
     */
    public int getTps() {
        return tps;
    }

}
