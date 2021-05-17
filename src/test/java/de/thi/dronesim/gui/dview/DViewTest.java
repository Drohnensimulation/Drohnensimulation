package de.thi.dronesim.gui.dview;

import de.thi.dronesim.Simulation;
import de.thi.dronesim.gui.GuiManager;

/**
 * Test for {@link DView}
 *
 * @author Michael Weichenrieder
 */
public class DViewTest {

    public static void main(String[] args) {
        Simulation simulation = new Simulation();
        simulation.prepare();
        simulation.getChild(GuiManager.class).openDViewGui();
    }
}
