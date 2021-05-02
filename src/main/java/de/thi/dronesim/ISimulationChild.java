package de.thi.dronesim;

/**
 * The Interface every Simulation Child should Implement, automatically serves as a singleton Factory
 *
 * @author Christian Schmied
 */
public interface ISimulationChild {

    void setSimulation(Simulation simulation);
    Simulation getSimulation();
}
