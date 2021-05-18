package de.thi.dronesim.autopilot;

public class Autopilot extends AsyncAutopilot {

    @Override
    public void autopilot() throws InterruptedException {
        // TODO autopilot stuff here
        // NOTE: Modifications are only using the #requestLocationDelta method
        //       Other modifications might cause a concurrent access violation

        // ---
        // Examples
        // ---
        // Wait for drone to climb above 20m
        awaitCondition((event) -> event.getDrone().getLocation().getY() > 10);
        // Decrease vertical speed by 20m/s
        requestLocationDelta(new DeltaRequest().requestDeltaVerticalSpeed(-20));
        // Turn drone 20 deg to the right
        requestLocationDelta(new DeltaRequest().requestDeltaHeading(lastEvent.getDrone().getLocation().getTrack() + 20));
        // Wait for drone to pass z = 20m and simulation time to be at least 20s
        awaitCondition(event -> event.getDrone().getLocation().getZ() > 20
                && event.getTime() >= 20);

    }

}
