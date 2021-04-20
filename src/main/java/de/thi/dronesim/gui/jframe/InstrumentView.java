package de.thi.dronesim.gui.jframe;

import de.thi.dronesim.gui.dview.DView;

import javax.swing.*;

public class InstrumentView extends JFrame implements InstrumentInterface {

    public InstrumentView(DView dView) {
        dView.getCanvas(); // Canvas has fixed height/width
        dView.setPerspective(DView.Perspective.FIRST_PERSON);
    }

    @Override
    public void updateDroneStatus(DroneStatus status) {
        // TODO
    }
}
