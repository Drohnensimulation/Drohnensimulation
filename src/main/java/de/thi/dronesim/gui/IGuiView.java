package de.thi.dronesim.gui;

import de.thi.dronesim.Simulation;
import de.thi.dronesim.SimulationUpdateEvent;

/**
 * InstrumentView Interface
 *
 * @author Michael Weichenrieder
 */
public interface IGuiView {

    void init(Simulation simulation);

    void updateDroneStatus(SimulationUpdateEvent simulationUpdateEvent);
}
