package de.thi.dronesim.system;

import de.thi.dronesim.Simulation;
import de.thi.dronesim.autopilot.AsyncAutopilot;
import de.thi.dronesim.drone.Location;
import de.thi.dronesim.gui.GuiManager;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Daniel Dunger
 */
public class SystemTest {

    // Test files are to be placed in "testconfigs"
    // The file to load for testing
    private static final String FILENAME = "testconfig_1.json";

    // Test with graphical output
    @Test
    void TestSimulationDView()  {
        // Start Position: (0, 0, 10) -> Goal (0, 1, -10)
        Simulation sim = new Simulation("src/test/java/de/thi/dronesim/system/testconfigs/".concat(FILENAME));
        assertNotNull(sim);
        sim.prepare();

        GuiManager gui = sim.getChild(GuiManager.class);
        assertNotNull(gui, "Could not get GuiManager.");
        gui.openDViewGui();

        // A forced wait to give the gui a chance to load up before running the sim
        try{
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("Test Start!");
        sim.start();

        Location l = sim.getDrone().getLocation();
        assertNotNull(l, "Could not get Location of Drone.");

        // The following attempts to fly the drone from (0, 0, 10) to (0, 0, -10)
        // at a fly-height of 5, with maximum speed and a heading of 90 degrees (eastwards)

        // Take off
        // TODO: Autopilot
    }

    // Test without graphical output. 1:1 the same actions as above. Results should be equal
    @Test
    void TestSimulationMView() {
        // Test normal config
        Simulation sim = new Simulation("src/test/java/de/thi/dronesim/system/testconfigs/".concat(FILENAME));
        assertNotNull(sim);
        sim.prepare();

        GuiManager gui = sim.getChild(GuiManager.class);
        assertNotNull(gui, "Could not get GuiManager");
        gui.openMViewGui();

        // A forced wait to give the gui a chance to load up before running the sim
        try{
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("Test Start!");
        sim.start();

        Location l = sim.getDrone().getLocation();
        assertNotNull(l, "Could not get Location of Drone.");

        // The following attempts to fly the drone from (0, 0, 10) to (0, 0, -10)
        // at a fly-height of 5, with maximum speed and a heading of 90 degrees (eastwards)

        // TODO: Autopilot
    }
}
