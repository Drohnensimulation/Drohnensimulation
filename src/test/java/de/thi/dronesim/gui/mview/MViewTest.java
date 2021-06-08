package de.thi.dronesim.gui.mview;

import de.thi.dronesim.Simulation;
import de.thi.dronesim.gui.GuiManager;

/**
 * Test for {@link MView}
 *
 * @author Daniel Dunger, Michael Weichenrieder
 */
public class MViewTest {

    public static void main(String[] args) {
        Simulation simulation = new Simulation();
        simulation.prepare();
        simulation.getChild(GuiManager.class).openMViewGui();
    }
}
