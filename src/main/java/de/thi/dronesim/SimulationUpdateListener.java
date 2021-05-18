package de.thi.dronesim;

public interface SimulationUpdateListener {

    /**
     * Handles every tick of the simulation.
     * @param event Event containing information about the current event
     */
    void onUpdate(SimulationUpdateEvent event);

}
