package de.thi.dronesim.example;// Importiere die Simulation

import de.thi.dronesim.Simulation;
import de.thi.dronesim.autopilot.AsyncAutopilot;
import de.thi.dronesim.autopilot.Autopilot;
import de.thi.dronesim.gui.GuiManager;

// Das Programmierbeispiel in der Klasse de.thi.dronesim.example.UfoExample
public class UfoExample {

    // Es spielt sich alles im Hauptprogramm ab.
    public static void main(String[] args) throws InterruptedException {

        //Lädt die Simulationsconfig und bereitet die Simulation vor.
        Simulation sim = new Simulation("src/main/java/de/thi/dronesim/example/simtestconf.json");
        sim.prepare();

        GuiManager gui = sim.getChild(GuiManager.class);
        sim.start();
        gui.openDViewGui();

        Autopilot auto = sim.getChild(Autopilot.class);

        //Wait until GUI is ready. Can be deleted if start button works.
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        testflight(auto);
        //testcrash(auto);
    }

    private static void testflight(Autopilot auto) throws InterruptedException {
        //Set low speed for the rotor rotation
        auto.requestLocationDelta(new AsyncAutopilot.DeltaRequest().requestDeltaAirspeed(0.00001));
        auto.awaitCondition(event -> event.getDrone().getLocation().getAirspeed() >= 0.00001);

        //Fly drone to 2m high
        auto.requestLocationDelta(new AsyncAutopilot.DeltaRequest().requestDeltaVerticalSpeed(1));
        auto.awaitCondition(event -> event.getDrone().getLocation().getPosition().y > 2);
        auto.requestLocationDelta(new AsyncAutopilot.DeltaRequest().requestDeltaVerticalSpeed(-1));
        auto.awaitCondition(event -> event.getDrone().getLocation().getVerticalSpeed() <= 0.1);

        //Rotate drone 180°
        auto.requestLocationDelta(new AsyncAutopilot.DeltaRequest().requestDeltaHeading(180));
        auto.awaitCondition(event -> event.getDrone().getLocation().getHeading() == 180);

        //Fly drone near z = 0
        auto.requestLocationDelta(new AsyncAutopilot.DeltaRequest().requestDeltaAirspeed(1));
        auto.awaitCondition(event -> event.getDrone().getLocation().getPosition().z < 0.2);
        auto.requestLocationDelta(new AsyncAutopilot.DeltaRequest().requestDeltaAirspeed(-1));
        auto.awaitCondition(event -> event.getDrone().getLocation().getAirspeed() <= 0.1);

        //Turn drone -90°
        auto.requestLocationDelta(new AsyncAutopilot.DeltaRequest().requestDeltaHeading(-90));
        auto.awaitCondition(event -> event.getDrone().getLocation().getHeading() == 90);

        //Fly drone near x = 0
        auto.requestLocationDelta(new AsyncAutopilot.DeltaRequest().requestDeltaAirspeed(1));
        auto.awaitCondition(event -> event.getDrone().getLocation().getPosition().x < 0.5);
        auto.requestLocationDelta(new AsyncAutopilot.DeltaRequest().requestDeltaAirspeed(-1));
        auto.awaitCondition(event -> event.getDrone().getLocation().getAirspeed() <= 0.1);

        //Fly around in a circle
        auto.requestLocationDelta(new AsyncAutopilot.DeltaRequest().requestDeltaAirspeed(1));
        auto.awaitCondition(event -> event.getDrone().getLocation().getAirspeed() >= 1);
        auto.requestLocationDelta(new AsyncAutopilot.DeltaRequest().requestDeltaHeading(360));
        auto.awaitCondition(event -> event.getDrone().getLocation().getHeading() < 90);
        auto.awaitCondition(event -> event.getDrone().getLocation().getHeading() == 90);
        auto.requestLocationDelta(new AsyncAutopilot.DeltaRequest().requestDeltaAirspeed(-2));
        auto.awaitCondition(event -> event.getDrone().getLocation().getAirspeed() <= 0.1);

        //Fly down to 1.25m
        auto.requestLocationDelta(new AsyncAutopilot.DeltaRequest().requestDeltaVerticalSpeed(-1));
        auto.awaitCondition(event -> event.getDrone().getLocation().getPosition().y <= 1.25);
        auto.requestLocationDelta(new AsyncAutopilot.DeltaRequest().requestDeltaVerticalSpeed(1));
        auto.awaitCondition(event -> event.getDrone().getLocation().getVerticalSpeed() >= -0.1);

        //Short pause
        auto.getSimulation().getDrone().getLocation().setAirspeed(0);
        auto.getSimulation().getDrone().getLocation().setVerticalSpeed(0);
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        auto.requestLocationDelta(new AsyncAutopilot.DeltaRequest().requestDeltaAirspeed(0.00001));
        auto.awaitCondition(event -> event.getDrone().getLocation().getAirspeed() >= 0.00001);

        //Fly up a certain distance depending on the current location
        auto.requestLocationDelta(new AsyncAutopilot.DeltaRequest().requestDeltaVerticalSpeed(0.5));
        double i = (auto.getSimulation().getDrone().getLocation().getPosition().y + 0.5);
        auto.awaitCondition(event ->
                event.getDrone().getLocation().getPosition().y >= i);
        auto.requestLocationDelta(new AsyncAutopilot.DeltaRequest().requestDeltaVerticalSpeed(-0.5));
        auto.awaitCondition(event -> event.getDrone().getLocation().getVerticalSpeed() >= -0.1);


        //Fly in a circle down to the ground and back up
        auto.requestLocationDelta(new AsyncAutopilot.DeltaRequest().requestDeltaAirspeed(1));
        auto.awaitCondition(event -> event.getDrone().getLocation().getAirspeed() >= 1);

        auto.requestLocationDelta(new AsyncAutopilot.DeltaRequest().requestDeltaVerticalSpeed(-0.25));
        auto.awaitCondition(event -> event.getDrone().getLocation().getVerticalSpeed() <= 0.2);
        auto.requestLocationDelta(new AsyncAutopilot.DeltaRequest().requestDeltaHeading(-360));

        auto.awaitCondition(event -> (event.getDrone().getLocation().getHeading() > 90
                || event.getDrone().getLocation().getHeading() < 270)
                && event.getDrone().getLocation().getPosition().y <= 0.1);

        auto.requestLocationDelta(new AsyncAutopilot.DeltaRequest().requestDeltaVerticalSpeed(0.75));

        auto.awaitCondition(event -> event.getDrone().getLocation().getHeading() <= 90
                && event.getDrone().getLocation().getPosition().y >= 0.75);

        auto.requestLocationDelta(new AsyncAutopilot.DeltaRequest().requestDeltaVerticalSpeed(-0.25));
        auto.awaitCondition(event -> event.getDrone().getLocation().getHeading() <= 90);

        auto.requestLocationDelta(new AsyncAutopilot.DeltaRequest().requestDeltaAirspeed(-1));
        auto.awaitCondition(event -> event.getDrone().getLocation().getAirspeed() <= 0.1);
        auto.requestLocationDelta(new AsyncAutopilot.DeltaRequest().requestDeltaVerticalSpeed(-0.25));
        auto.awaitCondition(event -> event.getDrone().getLocation().getVerticalSpeed() <= 0.1);


        //Turn around 180°
        auto.requestLocationDelta(new AsyncAutopilot.DeltaRequest().requestDeltaHeading(-180));
        auto.awaitCondition(event -> event.getDrone().getLocation().getHeading() == 270);

        //Fly near x = 0
        auto.requestLocationDelta(new AsyncAutopilot.DeltaRequest().requestDeltaAirspeed(1));
        auto.awaitCondition(event -> event.getDrone().getLocation().getPosition().x >= -0.2);
        auto.requestLocationDelta(new AsyncAutopilot.DeltaRequest().requestDeltaAirspeed(-1));
        auto.awaitCondition(event -> event.getDrone().getLocation().getAirspeed() >= 0.1);

        //Fly down to 1.25m
        auto.requestLocationDelta(new AsyncAutopilot.DeltaRequest().requestDeltaVerticalSpeed(-1));
        auto.awaitCondition(event -> event.getDrone().getLocation().getPosition().y <= 1.25);
        auto.requestLocationDelta(new AsyncAutopilot.DeltaRequest().requestDeltaVerticalSpeed(1));
        auto.awaitCondition(event -> event.getDrone().getLocation().getVerticalSpeed() >= -0.1);

        //Set both speed values to 0 to stop the rotation of the rotors
        auto.getSimulation().getDrone().getLocation().setAirspeed(0);
        auto.getSimulation().getDrone().getLocation().setVerticalSpeed(0);
    }

    private static void testcrash(Autopilot auto) throws InterruptedException {
        auto.requestLocationDelta(new AsyncAutopilot.DeltaRequest().requestDeltaAirspeed(.00001));
        auto.awaitCondition(event -> event.getDrone().getLocation().getAirspeed() >= .00001);

        //Fly to 0.5m height
        auto.requestLocationDelta(new AsyncAutopilot.DeltaRequest().requestDeltaVerticalSpeed(1));
        auto.awaitCondition(event -> event.getDrone().getLocation().getPosition().y > 0.5);
        auto.requestLocationDelta(new AsyncAutopilot.DeltaRequest().requestDeltaVerticalSpeed(-1));
        auto.awaitCondition(event -> event.getDrone().getLocation().getVerticalSpeed() <= 0.1);

        //Turn around 180°
        auto.requestLocationDelta(new AsyncAutopilot.DeltaRequest().requestDeltaHeading(180));
        auto.awaitCondition(event -> event.getDrone().getLocation().getHeading() == 180);

        auto.getSimulation();
        //Fly forwards to z = -0.2
        auto.requestLocationDelta(new AsyncAutopilot.DeltaRequest().requestDeltaAirspeed(1));
        auto.awaitCondition(event -> event.getDrone().getLocation().getPosition().z <= -0.2);
        auto.requestLocationDelta(new AsyncAutopilot.DeltaRequest().requestDeltaAirspeed(-1));
        auto.awaitCondition(event -> event.getDrone().getLocation().getAirspeed() >= 0.1);

        System.out.println(auto.getSimulation().getDrone().isCrashed());
    }

}