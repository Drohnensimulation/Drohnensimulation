package de.thi.dronesim.example;// Importiere die Simulation

import de.thi.dronesim.Simulation;
import de.thi.dronesim.autopilot.AsyncAutopilot;
import de.thi.dronesim.autopilot.Autopilot;
import de.thi.dronesim.gui.GuiManager;

// Das Programmierbeispiel in der Klasse de.thi.dronesim.example.UfoExample
public class UfoExample {

    // Es spielt sich alles im Hauptprogramm ab.
    public static void main(String[] args) throws InterruptedException {

        //Load Simulation config and prepares Simulation
        Simulation sim = new Simulation("src/main/java/de/thi/dronesim/example/simtestconf.json");
        sim.prepare();

        GuiManager gui = sim.getChild(GuiManager.class);
        gui.openDViewGui();
        //gui.openMViewGui();

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

        //Fly drone to 2m high
        auto.requestLocationDelta(new AsyncAutopilot.DeltaRequest().requestDeltaVerticalSpeed(1));
        auto.awaitCondition(event -> event.getDrone().getLocation().getPosition().y > 2);
        auto.requestLocationDelta(new AsyncAutopilot.DeltaRequest().requestDeltaVerticalSpeed(-1));
        auto.awaitCondition(event -> event.getDrone().getLocation().getVerticalSpeed() == 0);

        //Rotate drone 180°
        auto.requestLocationDelta(new AsyncAutopilot.DeltaRequest().requestDeltaHeading(180));
        auto.awaitCondition(event -> event.getDrone().getLocation().getHeading() == 180);

        //Fly drone near z = 0
        auto.requestLocationDelta(new AsyncAutopilot.DeltaRequest().requestDeltaAirspeed(1));
        auto.awaitCondition(event -> event.getDrone().getLocation().getPosition().z < 0.2);
        auto.requestLocationDelta(new AsyncAutopilot.DeltaRequest().requestDeltaAirspeed(-1));
        auto.awaitCondition(event -> event.getDrone().getLocation().getAirspeed() == 0);

        //Turn drone -90°
        auto.requestLocationDelta(new AsyncAutopilot.DeltaRequest().requestDeltaHeading(-90));
        auto.awaitCondition(event -> event.getDrone().getLocation().getHeading() == 90);

        //Fly drone near x = 0
        auto.requestLocationDelta(new AsyncAutopilot.DeltaRequest().requestDeltaAirspeed(1));
        auto.awaitCondition(event -> event.getDrone().getLocation().getPosition().x < 0.5);
        auto.requestLocationDelta(new AsyncAutopilot.DeltaRequest().requestDeltaAirspeed(-1));
        auto.awaitCondition(event -> event.getDrone().getLocation().getAirspeed() == 0);

        //Fly around in a circle
        auto.requestLocationDelta(new AsyncAutopilot.DeltaRequest().requestDeltaAirspeed(1));
        auto.awaitCondition(event -> event.getDrone().getLocation().getAirspeed() == 1);
        auto.requestLocationDelta(new AsyncAutopilot.DeltaRequest().requestDeltaHeading(360));
        auto.awaitCondition(event -> event.getDrone().getLocation().getHeading() < 90);
        auto.awaitCondition(event -> event.getDrone().getLocation().getHeading() == 90);
        auto.requestLocationDelta(new AsyncAutopilot.DeltaRequest().requestDeltaAirspeed(-2));
        auto.awaitCondition(event -> event.getDrone().getLocation().getAirspeed() == 0);

        //Fly down to 1.25m
        auto.requestLocationDelta(new AsyncAutopilot.DeltaRequest().requestDeltaVerticalSpeed(-1));
        auto.awaitCondition(event -> event.getDrone().getLocation().getPosition().y <= 1.25);
        auto.requestLocationDelta(new AsyncAutopilot.DeltaRequest().requestDeltaVerticalSpeed(1));
        auto.awaitCondition(event -> event.getDrone().getLocation().getVerticalSpeed() == 0);

        //Short pause
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        //Fly up a certain distance depending on the current location
        auto.requestLocationDelta(new AsyncAutopilot.DeltaRequest().requestDeltaVerticalSpeed(0.5));
        double i = (auto.getSimulation().getDrone().getLocation().getPosition().y + 0.5);
        auto.awaitCondition(event ->
                event.getDrone().getLocation().getPosition().y >= i);
        auto.requestLocationDelta(new AsyncAutopilot.DeltaRequest().requestDeltaVerticalSpeed(-0.5));
        auto.awaitCondition(event -> event.getDrone().getLocation().getVerticalSpeed() == 0);


        //Fly in a circle down to the ground and back up
        auto.requestLocationDelta(new AsyncAutopilot.DeltaRequest().requestDeltaAirspeed(1));
        auto.awaitCondition(event -> event.getDrone().getLocation().getAirspeed() == 1);

        auto.requestLocationDelta(new AsyncAutopilot.DeltaRequest().requestDeltaVerticalSpeed(-0.25));
        auto.awaitCondition(event -> event.getDrone().getLocation().getVerticalSpeed() == -0.25);
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
        auto.awaitCondition(event -> event.getDrone().getLocation().getAirspeed() == 0);
        auto.requestLocationDelta(new AsyncAutopilot.DeltaRequest().requestDeltaVerticalSpeed(-0.25));
        auto.awaitCondition(event -> event.getDrone().getLocation().getVerticalSpeed() <= 0.25);


        //Turn around 180°
        auto.requestLocationDelta(new AsyncAutopilot.DeltaRequest().requestDeltaHeading(-180));
        auto.awaitCondition(event -> event.getDrone().getLocation().getHeading() == 270);

        //Fly near x = 0
        auto.requestLocationDelta(new AsyncAutopilot.DeltaRequest().requestDeltaAirspeed(1));
        auto.awaitCondition(event -> event.getDrone().getLocation().getPosition().x >= -0.2);
        auto.requestLocationDelta(new AsyncAutopilot.DeltaRequest().requestDeltaAirspeed(-1));
        auto.awaitCondition(event -> event.getDrone().getLocation().getAirspeed() == 0);

        //Fly down to 1.1m
        auto.requestLocationDelta(new AsyncAutopilot.DeltaRequest().requestDeltaVerticalSpeed(-1));
        auto.awaitCondition(event -> event.getDrone().getLocation().getPosition().y <= 1.1);
        auto.requestLocationDelta(new AsyncAutopilot.DeltaRequest().requestDeltaVerticalSpeed(1));
        auto.awaitCondition(event -> event.getDrone().getLocation().getVerticalSpeed() <= 0.1);

        auto.getSimulation().stop();
    }

    private static void testcrash(Autopilot auto) throws InterruptedException {

        //Fly to 0.5m height
        auto.requestLocationDelta(new AsyncAutopilot.DeltaRequest().requestDeltaVerticalSpeed(1));
        auto.awaitCondition(event -> event.getDrone().getLocation().getPosition().y > 0.5);
        auto.requestLocationDelta(new AsyncAutopilot.DeltaRequest().requestDeltaVerticalSpeed(-1));
        auto.awaitCondition(event -> event.getDrone().getLocation().getVerticalSpeed() == 0);

        //Turn around 180°
        auto.requestLocationDelta(new AsyncAutopilot.DeltaRequest().requestDeltaHeading(180));
        auto.awaitCondition(event -> event.getDrone().getLocation().getHeading() == 180);

        //Fly forwards to z = -0.2
        auto.requestLocationDelta(new AsyncAutopilot.DeltaRequest().requestDeltaAirspeed(1));
        auto.awaitCondition(event -> event.getDrone().getLocation().getPosition().z <= -0.2);
        auto.requestLocationDelta(new AsyncAutopilot.DeltaRequest().requestDeltaAirspeed(-1));
        auto.awaitCondition(event -> event.getDrone().getLocation().getAirspeed() == 0);

        auto.getSimulation();
        System.out.println(auto.getSimulation().getDrone().isCrashed());
    }

}