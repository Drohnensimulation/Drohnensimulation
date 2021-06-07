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
        // Start Position: (10, 0, 10) -> Goal (-10, 1, -10)
        Simulation sim = new Simulation("src/test/java/de/thi/dronesim/system/testconfig_1.json");

        sim.prepare();
        GuiManager gui = sim.getChild(GuiManager.class);
        assertNotNull(gui, "Could not get GuiManager.");

        gui.openDViewGui();

        // Just waiting so it can properly load in, since it auto-starts atm.
        try{
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        sim.start();

        // Normally, an autopilot should be configurable to pilot towards the target destination
        Location l = sim.getDrone().getLocation();
        assertNotNull(l, "Could not get Location of Drone.");

        // Take off
        l.requestDeltaVerticalSpeed(1);
        while(l.getY() <= 4.4f) {}
        l.requestDeltaVerticalSpeed(-0.9);
        while(l.getY() <= 4.995f) {}
        l.requestDeltaVerticalSpeed(-0.1);

        // Heading
        l.requestDeltaHeading(135);
        while(l.getHeading() != 135) {}

        // Speed up towards target:
        l.requestDeltaAirspeed(2);
        while(l.getPosition().x >= -8 || l.getPosition().z >= -8) {}

        // Slow down towards Target
        l.requestDeltaAirspeed(-1.5);
        while(l.getPosition().x >= -9.5 || l.getPosition().z >= -9.5) {}

        l.requestDeltaAirspeed(-0.4);
        while(l.getPosition().x >= -10 || l.getPosition().z >= -10) {}

        l.requestDeltaAirspeed(-0.1);

        l.requestDeltaVerticalSpeed(-0.5);
        l.requestDeltaHeading(0);
        while(l.getPosition().y >= 1.5) {}

        l.requestDeltaVerticalSpeed(0.4);
        while(l.getPosition().y >= 1.05) {}

        l.requestDeltaVerticalSpeed(0.1);

        System.out.println("Test Finished: DView");

        // Keep open:
        while(true) {}
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
        // Just waiting so it can properly load in, since it auto-starts atm.
        try{
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        sim.start();

        // Normally, an autopilot should be configurable to pilot towards the target destination
        Location l = sim.getDrone().getLocation();
        assertNotNull(l, "Could not get Location of Drone.");

        // Take off
        l.requestDeltaVerticalSpeed(1);
        while(l.getY() <= 4.4f) {}
        l.requestDeltaVerticalSpeed(-0.9);
        while(l.getY() <= 4.995f) {}
        l.requestDeltaVerticalSpeed(-0.1);

        // Heading
        l.requestDeltaHeading(135);
        while(l.getHeading() != 135) {}

        // Speed up towards target:
        l.requestDeltaAirspeed(2);
        while(l.getPosition().x >= -8 || l.getPosition().z >= -8) {}

        // Slow down towards Target
        l.requestDeltaAirspeed(-1.5);
        while(l.getPosition().x >= -9.5 || l.getPosition().z >= -9.5) {}

        l.requestDeltaAirspeed(-0.4);
        while(l.getPosition().x >= -10 || l.getPosition().z >= -10) {}

        l.requestDeltaAirspeed(-0.1);

        l.requestDeltaVerticalSpeed(-0.5);
        l.requestDeltaHeading(0);
        while(l.getPosition().y >= 1.5) {}

        l.requestDeltaVerticalSpeed(0.4);
        while(l.getPosition().y >= 1.05) {}

        l.requestDeltaVerticalSpeed(0.1);

        System.out.println("Test Finished: DView");

        // Keep open:
        while(true) {}
    }
}
