package de.thi.dronesim.gui;

import de.thi.dronesim.SimulationUpdateEvent;

/**
 * InstrumentView Interface
 *
 * @author Michael Weichenrieder
 */
public interface IGuiView {

    void updateDroneStatus(SimulationUpdateEvent simulationUpdateEvent);
}
