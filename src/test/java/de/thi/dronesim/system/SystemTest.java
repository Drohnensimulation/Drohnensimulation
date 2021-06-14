package de.thi.dronesim.system;

import de.thi.dronesim.Simulation;
import de.thi.dronesim.drone.Location;
import de.thi.dronesim.gui.GuiManager;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.*;

public class SystemTest {

    // The file to load for testing. They need to be in the folder "testconfigs"
    private static final String FILENAME = "testconfig_1.json";

    // Test with graphical output
    @Test
    void TestSimulationDView()  {
        // Start Position: (0, 0, 10) -> Goal (0, 1, -10)
        Simulation sim = new Simulation("src/test/java/de/thi/dronesim/system/testconfigs/".concat(FILENAME));

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

        System.out.println("Test Start!");
        sim.start();

        Location l = sim.getDrone().getLocation();
        assertNotNull(l, "Could not get Location of Drone.");

        // The following attempts to fly the drone from (0, 0, 10) to (0, 0, -10)
        // at a fly-height of 5, with maximum speed and a heading of 90 degrees (eastwards)

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
    void TestSimulationMView() {
        // Test normal config
        Simulation sim = new Simulation("src/test/java/de/thi/dronesim/system/testconfigs/".concat(FILENAME));
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

        System.out.println("Test Start!");
        sim.start();

        Location l = sim.getDrone().getLocation();
        assertNotNull(l, "Could not get Location of Drone.");

        // The following attempts to fly the drone from (0, 0, 10) to (0, 0, -10)
        // at a fly-height of 5, with maximum speed and a heading of 90 degrees (eastwards)

        //Take off
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

        System.out.println("Test Finished: MView");

        // Keep open:
        while(true) {}
    }
}
