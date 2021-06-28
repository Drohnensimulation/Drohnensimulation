package de.thi.dronesim.system;

import de.thi.dronesim.Simulation;
import de.thi.dronesim.autopilot.AsyncAutopilot;
import de.thi.dronesim.autopilot.Autopilot;
import de.thi.dronesim.gui.GuiManager;
import jdk.jshell.spi.ExecutionControl;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Class that tests each module working together.
 * <p>
 * One can reuse one of the flight patterns and add a new config for it.
 * Simply change the filename to the corresponding config.
 * <p>
 * Do not run all tests at once, but individually!
 *
 * @author Daniel Dunger
 */
public class SystemTest {

    // The file to load for testing. Either change it or use one of the other ones, only one, obviously.
    /** Custom test */
    //private static final String FILENAME = "customTest.json";

    /**
     * Testing flying with Wind influences + Wind Sensor
     */
    //private static final String FILENAME = "wind/wind_flight_1.json"; // Wind from North      ^
    //private static final String FILENAME = "wind/wind_flight_2.json"; // Wind from North-East ^>
    //private static final String FILENAME = "wind/wind_flight_3.json"; // Wind from East       >
    //private static final String FILENAME = "wind/wind_flight_4.json"; // Wind from South-East v>
    //private static final String FILENAME = "wind/wind_flight_5.json"; // Wind from South      v

    private static final String FILENAME = "wind/wind_flight_6.json"; // Wind from South-West <v

    //private static final String FILENAME = "wind/wind_flight_7.json"; // Wind from West       <
    //private static final String FILENAME = "wind/wind_flight_8.json"; // Wind from North-West <^
    //private static final String FILENAME = "wind/wind_and_gust_flight_1.json"; // Wind from North + Gusts
    //private static final String FILENAME = "wind/wind_and_gust_flight_2.json"; // Wind from North-East + Gusts
    //private static final String FILENAME = "wind/wind_and_gust_flight_3.json"; // Wind from East + Gusts
    //private static final String FILENAME = "wind/wind_and_gust_flight_4.json"; // Wind from South-East + Gusts
    //private static final String FILENAME = "wind/wind_and_gust_flight_5.json"; // Wind from South + Gusts
    //private static final String FILENAME = "wind/wind_and_gust_flight_6.json"; // Wind from South-West + Gusts
    //private static final String FILENAME = "wind/wind_and_gust_flight_7.json"; // Wind from West + Gusts
    //private static final String FILENAME = "wind/wind_and_gust_flight_8.json"; // Wind from North-West + Gusts

    /** Testing flying with obstacles, crashing and wind influences + Obstacle-Detecting Sensors */
    //private static final String FILENAME = "obstacles/obstacles_1.json"; // Fly-By - Right Side of Obstacle
    //private static final String FILENAME = "obstacles/obstacles_2.json"; // Fly-By - Left Side of Obstacle

    //private static final String FILENAME = "obstacles/obstacles_3.json"; // Fly-By - Over Obstacle
    //private static final String FILENAME = "obstacles/obstacles_4.json"; // Fly-By - Under Obstacle
    //private static final String FILENAME = "obstacles/obstacles_5.json"; // Fly-By - Into Obstacle
    //private static final String FILENAME = "obstacles/obstacles_and_wind_flight_1.json"; // Fly-By South + North-Wind
    //private static final String FILENAME = "obstacles/obstacles_and_wind_flight_2.json"; // Fly-By North + North-Wind
    //private static final String FILENAME = "obstacles/obstacles_and_wind_flight_3.json"; // Overflight + North-Wind
    //private static final String FILENAME = "obstacles/obstacles_and_wind_flight_4.json"; // Overflight + East-Wind
    //private static final String FILENAME = "obstacles/obstacles_and_wind_flight_5.json"; // Underflight + North-Wind
    //private static final String FILENAME = "obstacles/obstacles_and_wind_flight_6.json"; // Underflight + West-Wind
    //private static final String FILENAME = "obstacles/obstacles_and_wind_flight_7.json"; // Fly-By Crash North-Wind
    //private static final String FILENAME = "obstacles/obstacles_and_wind_flight_8.json"; // Direct-Crash West-Wind

    /**
     * Complex tests, that basically test a real flight scenario
     */
    //private static final String FILENAME = "complex/complex_flight_scenario_1.json";
    //private static final String FILENAME = "complex/complex_flight_scenario_2.json";
    //private static final String FILENAME = "complex/complex_flight_scenario_3.json";
    //private static final String FILENAME = "complex/complex_flight_scenario_4.json";

    private Simulation sim;
    private GuiManager gui;
    private Autopilot ap;

    /**
     * This test is supposed to be used with the Wind, Obstacles or GUI Tests.
     * <p>
     * It is a simple, straight flight without any obstacles from (10, 0, 0) towards (-10, 0, 0)
     * with a fly height of 10.
     */
    @Test
    void TestSimulation_Wind_Obstacle_GUI() {
        // Preparation
        Simulation sim = new Simulation("src/test/java/de/thi/dronesim/system/testconfigs/".concat(FILENAME));
        assertNotNull(sim, "Unable to create simulation");
        sim.prepare();

        GuiManager gui = sim.getChild(GuiManager.class);
        assertNotNull(gui, "Could not get GuiManager.");
        gui.openDViewGui();

        Autopilot ap = sim.getChild(Autopilot.class);
        assertNotNull(ap, "Could not get autopilot.");

        System.out.printf("\n\nTest Valid. Config: %s\n\n", FILENAME);

        // Autopilot flight
        try {
            flightTest1(ap);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("Test Finished!");

        while (true) {
            // Stay open
        }
    }

    /**
     * This test is supposed to run with one of the complex flight tests.
     * Each test requires its own flight code below.
     */
    @Test
    public void TestSimulation_Complex() {
        // Preparation
        Simulation sim = new Simulation("src/test/java/de/thi/dronesim/system/testconfigs/".concat(FILENAME));
        assertNotNull(sim, "Unable to create simulation");
        sim.prepare();

        GuiManager gui = sim.getChild(GuiManager.class);
        assertNotNull(gui, "Could not get GuiManager.");
        gui.openDViewGui();

        Autopilot ap = sim.getChild(Autopilot.class);
        assertNotNull(ap, "Could not get autopilot.");

        System.out.printf("\n\nTest Valid. Config: %s\n\n", FILENAME);

        // Autopilot flight
        try {
            flightTest_Complex_1(ap);
            //flightTest_Complex_2(ap);
            //flightTest_Complex_3(ap);
            //flightTest_Complex_4(ap);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("Test Finished!");

        while (true) {
            // Stay open
        }
    }

    /* ********************************************************************************************************
                                        Autopilot Flight control below
     ******************************************************************************************************* */

    /**
     * The AutoPilot flight code for the Wind/Obstacle/Gui test scenarios
     *
     * @param ap The autopilot of the current simulation.
     * @throws InterruptedException When the autopilot gets interrupted by something
     */
    private void flightTest1(Autopilot ap) throws InterruptedException {
        // Take off
        ap.requestLocationDelta(new AsyncAutopilot.DeltaRequest(1, 0, 0));
        ap.awaitCondition(event -> event.getDrone().getLocation().getY() >= 10);

        // Fly towards Goal
        ap.requestLocationDelta(new AsyncAutopilot.DeltaRequest(-1, 2, 0));
        ap.awaitCondition(event -> event.getDrone().getLocation().getZ() >= 9.75);

        // Slow down when approaching
        ap.requestLocationDelta(new AsyncAutopilot.DeltaRequest(0, -1.6, 0));
        ap.awaitCondition(event -> event.getDrone().getLocation().getZ() >= 9.95);

        // Landing process
        ap.requestLocationDelta(new AsyncAutopilot.DeltaRequest(-1, -0.4, 0));
        ap.awaitCondition(event -> event.getDrone().getLocation().getY() <= 1);
        ap.requestLocationDelta(new AsyncAutopilot.DeltaRequest(0.8, 0, 0));
        ap.awaitCondition(event -> event.getDrone().getLocation().getY() <= 0.05);
        ap.requestLocationDelta(new AsyncAutopilot.DeltaRequest(0.2, 0, 0));
    }

    /**
     * The AutoPilot flight code for the complex test scenario 1.
     *
     * @param ap The autopilot of the current simulation.
     * @throws InterruptedException When the autopilot gets interrupted by something
     */
    private void flightTest_Complex_1(Autopilot ap) throws InterruptedException {
        // This Code and scenario was taken from UfoExample, made by Fabian Fischer, slightly adjusted scenario by me

        //Fly drone to 2m high
        ap.requestLocationDelta(new AsyncAutopilot.DeltaRequest().requestDeltaVerticalSpeed(1));
        ap.awaitCondition(event -> event.getDrone().getLocation().getPosition().y > 2);
        ap.requestLocationDelta(new AsyncAutopilot.DeltaRequest().requestDeltaVerticalSpeed(-1));
        ap.awaitCondition(event -> event.getDrone().getLocation().getVerticalSpeed() == 0);

        //Rotate drone 180°
        ap.requestLocationDelta(new AsyncAutopilot.DeltaRequest().requestDeltaHeading(180));
        ap.awaitCondition(event -> event.getDrone().getLocation().getHeading() == 180);

        //Fly drone near z = 0
        ap.requestLocationDelta(new AsyncAutopilot.DeltaRequest().requestDeltaAirspeed(1));
        ap.awaitCondition(event -> event.getDrone().getLocation().getPosition().z < 0.2);
        ap.requestLocationDelta(new AsyncAutopilot.DeltaRequest().requestDeltaAirspeed(-1));
        ap.awaitCondition(event -> event.getDrone().getLocation().getAirspeed() == 0);

        //Turn drone -90°
        ap.requestLocationDelta(new AsyncAutopilot.DeltaRequest().requestDeltaHeading(-90));
        ap.awaitCondition(event -> event.getDrone().getLocation().getHeading() == 90);

        //Fly drone near x = 0
        ap.requestLocationDelta(new AsyncAutopilot.DeltaRequest().requestDeltaAirspeed(1));
        ap.awaitCondition(event -> event.getDrone().getLocation().getPosition().x < 0.5);
        ap.requestLocationDelta(new AsyncAutopilot.DeltaRequest().requestDeltaAirspeed(-1));
        ap.awaitCondition(event -> event.getDrone().getLocation().getAirspeed() == 0);

        //Fly around in a circle
        ap.requestLocationDelta(new AsyncAutopilot.DeltaRequest().requestDeltaAirspeed(1));
        ap.awaitCondition(event -> event.getDrone().getLocation().getAirspeed() == 1);
        ap.requestLocationDelta(new AsyncAutopilot.DeltaRequest().requestDeltaHeading(360));
        ap.awaitCondition(event -> event.getDrone().getLocation().getHeading() < 90);
        ap.awaitCondition(event -> event.getDrone().getLocation().getHeading() == 90);
        ap.requestLocationDelta(new AsyncAutopilot.DeltaRequest().requestDeltaAirspeed(-2));
        ap.awaitCondition(event -> event.getDrone().getLocation().getAirspeed() == 0);

        //Fly down to 1.25m
        ap.requestLocationDelta(new AsyncAutopilot.DeltaRequest().requestDeltaVerticalSpeed(-1));
        ap.awaitCondition(event -> event.getDrone().getLocation().getPosition().y <= 1.25);
        ap.requestLocationDelta(new AsyncAutopilot.DeltaRequest().requestDeltaVerticalSpeed(1));
        ap.awaitCondition(event -> event.getDrone().getLocation().getVerticalSpeed() == 0);

        //Short pause
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        //Fly up a certain distance depending on the current location
        ap.requestLocationDelta(new AsyncAutopilot.DeltaRequest().requestDeltaVerticalSpeed(0.5));
        double i = (ap.getSimulation().getDrone().getLocation().getPosition().y + 0.5);
        ap.awaitCondition(event ->
                event.getDrone().getLocation().getPosition().y >= i);
        ap.requestLocationDelta(new AsyncAutopilot.DeltaRequest().requestDeltaVerticalSpeed(-0.5));
        ap.awaitCondition(event -> event.getDrone().getLocation().getVerticalSpeed() == 0);


        //Fly in a circle down to the ground and back up
        ap.requestLocationDelta(new AsyncAutopilot.DeltaRequest().requestDeltaAirspeed(1));
        ap.awaitCondition(event -> event.getDrone().getLocation().getAirspeed() == 1);

        ap.requestLocationDelta(new AsyncAutopilot.DeltaRequest().requestDeltaVerticalSpeed(-0.25));
        ap.awaitCondition(event -> event.getDrone().getLocation().getVerticalSpeed() == -0.25);
        ap.requestLocationDelta(new AsyncAutopilot.DeltaRequest().requestDeltaHeading(-360));

        ap.awaitCondition(event -> (event.getDrone().getLocation().getHeading() > 90
                || event.getDrone().getLocation().getHeading() < 270)
                && event.getDrone().getLocation().getPosition().y <= 0.1);

        ap.requestLocationDelta(new AsyncAutopilot.DeltaRequest().requestDeltaVerticalSpeed(0.75));
        ap.awaitCondition(event -> event.getDrone().getLocation().getHeading() <= 90
                && event.getDrone().getLocation().getPosition().y >= 0.75);

        ap.requestLocationDelta(new AsyncAutopilot.DeltaRequest().requestDeltaVerticalSpeed(-0.25));
        ap.awaitCondition(event -> event.getDrone().getLocation().getHeading() <= 90);

        ap.requestLocationDelta(new AsyncAutopilot.DeltaRequest().requestDeltaAirspeed(-1));
        ap.awaitCondition(event -> event.getDrone().getLocation().getAirspeed() == 0);
        ap.requestLocationDelta(new AsyncAutopilot.DeltaRequest().requestDeltaVerticalSpeed(-0.25));
        ap.awaitCondition(event -> event.getDrone().getLocation().getVerticalSpeed() <= 0.25);


        //Turn around 180°
        ap.requestLocationDelta(new AsyncAutopilot.DeltaRequest().requestDeltaHeading(-180));
        ap.awaitCondition(event -> event.getDrone().getLocation().getHeading() == 270);

        //Fly near x = 0
        ap.requestLocationDelta(new AsyncAutopilot.DeltaRequest().requestDeltaAirspeed(1));
        ap.awaitCondition(event -> event.getDrone().getLocation().getPosition().x >= -0.2);
        ap.requestLocationDelta(new AsyncAutopilot.DeltaRequest().requestDeltaAirspeed(-1));
        ap.awaitCondition(event -> event.getDrone().getLocation().getAirspeed() == 0);

        //Fly down to 1.1m
        ap.requestLocationDelta(new AsyncAutopilot.DeltaRequest().requestDeltaVerticalSpeed(-1));
        ap.awaitCondition(event -> event.getDrone().getLocation().getPosition().y <= 1.1);
        ap.requestLocationDelta(new AsyncAutopilot.DeltaRequest().requestDeltaVerticalSpeed(1));
        ap.awaitCondition(event -> event.getDrone().getLocation().getVerticalSpeed() <= 0.1);
    }

    /**
     * Stub for another complex test 2
     *
     * @param ap The autopilot of the current simulation
     * @throws ExecutionControl.NotImplementedException Because it does not exist yet, duh
     */
    private void flightTest_Complex_2(Autopilot ap) throws ExecutionControl.NotImplementedException {
        throw new ExecutionControl.NotImplementedException("Not Implemented");
    }

    /**
     * Stub for another complex test 3
     *
     * @param ap The autopilot of the current simulation
     * @throws ExecutionControl.NotImplementedException Because it does not exist yet, duh
     */
    private void flightTest_Complex_3(Autopilot ap) throws ExecutionControl.NotImplementedException {
        throw new ExecutionControl.NotImplementedException("Not Implemented");
    }

    /**
     * Stub for another complex test 4
     *
     * @param ap The autopilot of the current simulation
     * @throws ExecutionControl.NotImplementedException Because it does not exist yet, duh
     */
    private void flightTest_Complex_4(Autopilot ap) throws ExecutionControl.NotImplementedException {
        throw new ExecutionControl.NotImplementedException("Not Implemented");
    }
}
