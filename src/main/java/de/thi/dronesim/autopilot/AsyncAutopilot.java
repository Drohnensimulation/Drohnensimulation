package de.thi.dronesim.autopilot;

import de.thi.dronesim.ISimulationChild;
import de.thi.dronesim.Simulation;
import de.thi.dronesim.SimulationUpdateEvent;
import de.thi.dronesim.SimulationUpdateListener;
import de.thi.dronesim.drone.Location;

public abstract class AsyncAutopilot implements ISimulationChild, SimulationUpdateListener, Runnable {

    private Simulation simulation;
    private final Thread thread;

    protected volatile SimulationUpdateEvent lastEvent;
    private volatile EventExpectation expectation;
    private volatile DeltaRequest deltaRequest;
    private volatile boolean conditionValid = false;

    public AsyncAutopilot() {
        thread = new Thread(this);
    }

    public abstract void autopilot() throws InterruptedException;

    @Override
    public void run() {
        try {
            autopilot();
        } catch (InterruptedException ignored) {}
    }

    @Override
    public void initialize(Simulation simulation) {
        this.simulation = simulation;
    }

    @Override
    public void onSimulationStart() {
        thread.start();
    }

    @Override
    public void onSimulationStop() {
        if (thread != null) {
            thread.interrupt();
        }
    }

    @Override
    public Simulation getSimulation() {
        return simulation;
    }

    @Override
    public synchronized void onUpdate(final SimulationUpdateEvent event) {
        lastEvent = event;

        // CHeck if statement is meat
        if (expectation.evaluate(event)) {
            conditionValid = true;
            this.notifyAll();
        }

        // Apply requested changes
        if (deltaRequest != null) {
            Location location = event.getDrone().getLocation();
            location.requestDeltaAirspeed(deltaRequest.tas);
            location.requestDeltaVerticalSpeed(deltaRequest.vas);
            location.requestDeltaHeading(deltaRequest.hdg);
            deltaRequest = null;
        }
    }

    /**
     * Requests a change in direction and speed
     * @param request Changes to be applied
     */
    public void requestLocationDelta(DeltaRequest request) {
        this.deltaRequest = request;
    }

    /**
     * Wait until a given statement becomes true
     * @param expectation Statement to be checked
     */
    public synchronized void awaitCondition(EventExpectation expectation) throws InterruptedException {
        this.expectation = expectation;
        conditionValid = false;
        while (!conditionValid) {
            this.wait();
       }
    }

    public interface EventExpectation {
        /**
         * Evaluates a statement and returns true if the conditions are meat.
         * @param event Event to be applied on the statement
         * @return Wherever the statement if true or not
         */
        boolean evaluate(SimulationUpdateEvent event);
    }

    public static class DeltaRequest {

        private double vas;
        private double tas;
        private double hdg;

        public DeltaRequest() {}

        public DeltaRequest(double vas, double tas, double hdg) {
            this.vas = vas;
            this.tas = tas;
            this.hdg = hdg;
        }

        /**
         * @see Location#requestDeltaVerticalSpeed(double)
         */
        public DeltaRequest requestDeltaVerticalSpeed(double vas) {
            this.vas = vas;
            return this;
        }

        /**
         * @see Location#requestDeltaAirspeed(double)
         */
        public DeltaRequest requestDeltaAirspeed(double tas) {
            this.tas = tas;
            return this;
        }

        /**
         * @see Location#requestDeltaHeading(double)
         */
        public DeltaRequest requestDeltaHeading(double hdg) {
            this.hdg = hdg;
            return this;
        }

    }

}
