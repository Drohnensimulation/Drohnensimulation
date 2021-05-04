package de.thi.dronesim.example;// Importiere die Simulation
import de.thi.dronesim.drone.UfoSim;

// Das Programmierbeispiel in der Klasse de.thi.dronesim.example.UfoExample
public class UfoExample {

    // Es spielt sich alles im Hauptprogramm ab.
    public static void main(String[] args) {
        // In der folgenden Zeile definieren wir eine Referenz auf die Simulation.
        UfoSim sim = UfoSim.getInstance();

        // Oeffnen einer View
        sim.openViewWindow();

        // Doppelte Simulationsgeschwindigkeit
        sim.setSpeedup(2);

        // Meldung auf die View-Konsole ausgeben und auf den Trigger warten
        System.out.println("Press any key to start...");
        while (!sim.getTrigger());   

        // Der folgende Code fliegt das Ufo zur Position (20, 20, 0).

        // Das Ufo fliegt senkrecht nach oben mit 10 km/h.
        // Funktion getTimeAndPosition siehe unten
        System.out.println(formatFlightData(sim) + "takeoff with 10 km/h to alt 10 m...");
        sim.setI(90);
        sim.requestDeltaV(10);

        // Wenn die Hoehe 8m erreicht ist, bremst das Ufo auf 1 km/h.
        while (sim.getZ() < 8);
        System.out.println(formatFlightData(sim) + "...slow down to 1 km/h... ");
        sim.requestDeltaV(-9);

        // Wenn die Hoehe 9.95m erreicht ist, stoppt das Ufo und richtet sich horizontal aus.
        while (sim.getZ() < 9.95);
        System.out.println(formatFlightData(sim) + "...stop and turn horizontal");
        sim.requestDeltaV(-1);
        sim.setI(0); 
 
        // Weiter geht es in Richtung 45 Grad. Die zu fliegende Distanz ist dist.
        sim.setD(45);
        double dist = sim.getDist() + Math.sqrt(20 * 20 + 20 * 20);

        // Das Ufo beschleunigt auf 15 km/h.
        System.out.println(formatFlightData(sim) + "go " + 45 + " deg with 15 km/h...");
        sim.requestDeltaV(15);

        // Wenn der Abstand zum Ziel 4m ist, bremst das Ufo auf 1 km/h.
        while (dist - sim.getDist() > 4);
        System.out.println(formatFlightData(sim) + "...slow down to 1 km/h... ");
        sim.requestDeltaV(-14);

        // Wenn der Abstand zum Ziel 0.05m ist, stoppt das Ufo.
        while (dist - sim.getDist() > 0.05);
        System.out.println(formatFlightData(sim) + "...stop");
        sim.requestDeltaV(-1);

        // Das Ufo fliegt senkrecht nach unten mit 10 km/h.
        System.out.println(formatFlightData(sim) + "landing with 10 km/h");
        sim.setI(-90);
        sim.requestDeltaV(10); 

        // Wenn die Hoehe 3m erreicht, bremst das Ufo auf 1 km/h.
        while (sim.getZ() > 3);
        System.out.println(formatFlightData(sim) + "...slow down to 1 km/h...");
        sim.requestDeltaV(-9);

        // Das Ufo ist gelandet, wenn die He kleiner gleich 0 ist.
        while (sim.getZ() > 0);
        System.out.println(formatFlightData(sim) + "...happily landed");

        // Meldung auf die View-Konsole ausgeben und auf den Trigger warten
        System.out.println("Press any key to exit...");
        while (!sim.getTrigger()); 

        // Da im Hintergrund die Simulation laeuft, beenden wir das Programm explizit.
        System.exit(1);
    } 

    // Hilfsfunktion zur Formatierung der Zeit und der Koordinaten des Ufos
    private static String formatFlightData(UfoSim sim) {
        return String.format("%5.1f", sim.getTime()) + "s: " + "[" +
               String.format("%6.1f", sim.getX()) + " " +
               String.format("%6.1f", sim.getY()) + " " +
               String.format("%5.1f", sim.getZ()) + "] ";
    } 

} 