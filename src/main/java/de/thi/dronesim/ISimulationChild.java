package de.thi.dronesim;

/**
 * The Interface every Simulation Child should Implement, automatically serves as a singleton Factory
 *
 * @author Christian Schmied
 */
public interface ISimulationChild {

    /**
     * Sets the simulation of which it is a child of and registers update listeners if required.
     * @param simulation The parent simulation
     */
    void initialize(Simulation simulation);

    Simulation getSimulation();

    /**
     * Handles the start of the simulation
     */
    default void onSimulationStart() {}

    /**
     * Handles the end of the simulation
     */
    default void onSimulationStop() {}
}
