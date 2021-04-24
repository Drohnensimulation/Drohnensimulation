package de.thi.dronesim.gui.jframe;

import de.thi.dronesim.drone.Location;

/**
 * InstrumentView Interface
 *
 * @author Michael Weichenrieder
 */
public interface IInstrumentView {

    void updateDroneStatus(Location location);
}
