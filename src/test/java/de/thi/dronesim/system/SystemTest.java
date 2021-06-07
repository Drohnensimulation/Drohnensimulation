package de.thi.dronesim.system;

import de.thi.dronesim.Simulation;
import de.thi.dronesim.drone.Location;
import de.thi.dronesim.gui.GuiManager;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.*;

public class SystemTest {

    // Test with graphical output
    @Test
    @Order(1)
    void TestSimulationDView()  {
        Simulation sim = new Simulation("src/test/java/de/thi/dronesim/system/testconfig_1.json");

        sim.prepare();
        GuiManager gui = sim.getChild(GuiManager.class);
        assertNotNull(gui, "Could not get GuiManager.");

        sim.start();
        gui.openDViewGui();

        try{
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // Normally, an autopilot should be configurable to pilot towards the target destination
        Location l = sim.getDrone().getLocation();

        assertNotNull(l, "Could not get Location of Drone.");

        l.requestDeltaVerticalSpeed(1);
        while(l.getPosition().y <= 2.45f) {
            // Wait until drone has reached the specified height. Autopilot should be more clever and use wait()
            // While requestDeltaX actually notifies the caller once it has reached the desired value.
        }
        l.requestDeltaVerticalSpeed(-1);
        while(l.getDeltaVerticalSpeed() != 0)
        assertEquals(l.getPosition().y, 2.5f, 0.05);
        l.setY(0);

        l.requestDeltaHeading(-90);
        while(l.getHeading() - 0.05 < 90 || l.getHeading() + 0.05 < 90) {
        }
        assertEquals(l.getHeading(), 270);

        l.requestDeltaAirspeed(2);
        while(l.getPosition().x < 3.5)
        {}
        assertEquals(l.getPosition().x, 3.5, 0.05);

        l.requestDeltaAirspeed(-1.5);
        l.requestDeltaVerticalSpeed(-0.25);

        while(l.getPosition().y > 0.04) {}
        l.requestDeltaVerticalSpeed(0.25);

        assertEquals(l.getPosition().x, 3.5, 0.05);
        assertEquals(l.getPosition().y, 0, 0.05);
        assertEquals(l.getPosition().z, 0, 0.01);
        System.out.println("Test Finished: DView");
    }

    // Test without graphical output. Basically the same as above!
    @Test
    @Order(2)
    void TestSimulationMView() {
        // Test normal config
        Simulation sim = new Simulation("src/test/java/de/thi/dronesim/system/testconfig_1.json");
        sim.prepare();

        GuiManager gui = sim.getChild(GuiManager.class);
        assertNotNull(gui, "Could not get GuiManager");

        gui.openMViewGui();

        // Below is pretty much the same as above, just without the graphical output.
        // Result should be the same
    }
}
