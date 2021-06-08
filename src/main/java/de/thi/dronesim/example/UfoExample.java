package de.thi.dronesim.example;// Importiere die Simulation

import com.jme3.math.Vector3f;
import de.thi.dronesim.Simulation;
import de.thi.dronesim.drone.Location;
import de.thi.dronesim.gui.GuiManager;

// Das Programmierbeispiel in der Klasse de.thi.dronesim.example.UfoExample
public class UfoExample {

    // Es spielt sich alles im Hauptprogramm ab.
    public static void main(String[] args) {

        //Lädt die Simulationsconfig und bereitet die Simulation vor.
        Simulation sim = new Simulation("src/main/java/de/thi/dronesim/example/simtestconf.json");
        sim.prepare();

        GuiManager gui = sim.getChild(GuiManager.class);
        sim.start();
        gui.openDViewGui();


        //Steuert die Drone vorübergehend, sollte in Autopilot.java ausgelagert werden.
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Location loc = sim.getDrone().getLocation();
        loc.requestDeltaVerticalSpeed(0.0001);

//        loc.requestDeltaHeading(180);
//        while (loc.getHeading() != 180) {}
//        loc.requestDeltaAirspeed(1);
//        while(loc.getPosition().z > -5 ) {}


        loc.requestDeltaVerticalSpeed(1);
        while(loc.getPosition().y < 2 ) {}
        loc.requestDeltaVerticalSpeed(-1);

        loc.requestDeltaHeading(180);
        while (loc.getHeading() != 180) {}

        loc.requestDeltaAirspeed(1);
        while(loc.getPosition().z > 0.2)  {}
        loc.requestDeltaAirspeed(-1);

        loc.requestDeltaHeading(-90);
        while (loc.getHeading() != 90) {}

        loc.requestDeltaAirspeed(1);
        while(loc.getPosition().x > 0.2)  {}
        loc.requestDeltaAirspeed(-1);

        loc.requestDeltaAirspeed(0.75);
        loc.requestDeltaHeading(360);
        while (loc.getHeading() >= 90) {}
        while (loc.getHeading() < 90) {}
        loc.requestDeltaAirspeed(-2);



        loc.requestDeltaVerticalSpeed(-1);
        while(loc.getPosition().y > 1.25)  {}
        loc.requestDeltaVerticalSpeed(1);


        loc.requestDeltaVerticalSpeed(-0.0001);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        loc.requestDeltaVerticalSpeed(0.0001);


        loc.requestDeltaVerticalSpeed(0.5);
        Vector3f old = loc.getPosition();
        while(loc.getPosition().y <= old.y+0.5) {}
        loc.requestDeltaVerticalSpeed(-0.5);


        loc.requestDeltaHeading(-360);
        loc.requestDeltaAirspeed(1);
        loc.requestDeltaVerticalSpeed(-0.25);
        while ((loc.getHeading() <= 90 || loc.getHeading() >= 270) && loc.getPosition().y > 0.1 ) {}
        loc.requestDeltaVerticalSpeed(0.75);
        while (loc.getHeading() > 90 && loc.getPosition().y < 0.75) {}
        loc.requestDeltaVerticalSpeed(-0.25);
        while (loc.getHeading() > 90 ) {}
        loc.requestDeltaAirspeed(-1);
        loc.requestDeltaVerticalSpeed(-0.25);

        loc.requestDeltaVerticalSpeed(-1);
        while(loc.getPosition().y > 1.25)  {}
        loc.requestDeltaVerticalSpeed(1);

        loc.requestDeltaVerticalSpeed(-0.0001);


    }

}