package de.thi.dronesim;

public abstract class GenericSimulationChild {
    private final Simulation simulation;

    public GenericSimulationChild(Simulation simulation) {
        this.simulation = simulation;
    }

    protected Simulation getSimulation() {
        return simulation;
    }
}
