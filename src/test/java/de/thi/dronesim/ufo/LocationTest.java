package de.thi.dronesim.ufo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LocationTest {

    private Location location;

    @BeforeEach
    public void setup() {
        location = new Location(0, 0, 0);
    }

    @Test
    void updatePosition() {
        // Test x and z calculation
        double gs = 17;
        location.setGroundSpeed(gs);

        double[] tracks = {0, 20, 73, 90, 180, 241, 270, 322};
        double[] resultX = {0, 5.814, 16.257, 17, 0, -14.869, -17, -10.466};
        double[] resultZ = {17, 15.975, 4.9703, 0, -17, -8.242, 0, 13.396};
        for (int t = 0; t < tracks.length; t++) {
            location.setX(0);
            location.setY(0);
            location.setZ(0);

            location.setTrack(tracks[t]);

            // Simulate one second
            location.updatePosition(1);

            // Test if vector length matches speed
            assertEquals(gs, Math.sqrt( Math.pow(location.getX(),2) + Math.pow(location.getY(),2)), 0.001);
            // Test if x y z matches
            assertEquals(resultX[t], location.getX(), 0.001,"Wrong X for track "+tracks[t]);
            assertEquals(resultZ[t], location.getY(), 0.001,"Wrong Z for track "+tracks[t]);
        }

        // Test total x any z
        location.setX(0);
        location.setY(0);
        location.setZ(0);
        double x = 0;
        double z = 0;
        for (int t = 0; t < tracks.length; t++) {
            location.setTrack(tracks[t]);
            location.updatePosition(1);
            x += resultX[t];
            z += resultZ[t];
        }
        assertEquals(x, location.getX(), 0.001, "Failed test for total x");
        assertEquals(z, location.getY(), 0.001, "Failed test for total z");

        // Test y
        double[] speeds = {0, 1.2, 2.5, -2.7};
        double y = 0;
        location.setVerticalSpeed(0);
        location.setY(0);
        for (double v : speeds) {
            location.setVerticalSpeed(v);
            location.updatePosition(1);
            y += v;
        }
        assertEquals(y, location.getZ(), 0.001, "Failed test for y");
    }

    @Test
    void requestDeltaHeading() {
        double[] requests = {90, 12.5, -41.2, 200};
        double[] expected = {90, 12.5, 318.8, 100}; // Expected result after 100 ticks

        for (int t = 0; t < requests.length; t++) {
            location.reset();
            location.requestDeltaHeading(requests[t]);
            // Simulate executor threat
            for (int i = 0; i < 100; i++) {
                location.updateDelta(10);
                location.updatePosition(10);
            }
            assertEquals(expected[t], location.getHeading(),  0.001,
                    "Request of delta heading failed for heading "+requests[t]);
        }

        // Test delta change
        location.reset();
        location.requestDeltaHeading(90);
        for (int i = 0; i < 50; i++) {
            location.updateDelta(10);
            location.updatePosition(10);
        }
        location.requestDeltaHeading(-50);
        for (int i = 0; i < 50; i++) {
            location.updateDelta(10);
            location.updatePosition(10);
        }
        assertEquals(40, location.getHeading(),  0.001,
                "Request of delta heading change failed!");

    }

    @Test
    void requestDeltaAirspeed() {
        double[] requests = {10, 12.5, -10, 50};
        double[] expected = {10, 12.5, 0, 13.889}; // Expected result after 100 ticks

        for (int t = 0; t < requests.length; t++) {
            location.reset();
            location.requestDeltaAirspeed(requests[t]);
            // Simulate executor threat for 10 seconds
            for (int i = 0; i < 1000; i++) {
                location.updateDelta(10);
            }
            assertEquals(expected[t], location.getAirspeed(),  0.001,
                    "Request of delta airspeed failed for airspeed "+requests[t]);
        }

        // Stop test
        location.reset();
        location.setAirspeed(10);
        location.requestDeltaAirspeed(-20);
        for (int i = 0; i < 100; i++) {
            location.updateDelta(10);
        }
        assertEquals(0, location.getAirspeed(), 0.001, "TAS stop test failed!");

        // Test delta change
        location.reset();
        location.requestDeltaAirspeed(10);
        for (int i = 0; i < 50; i++) {
            location.updateDelta(10);
        }
        location.requestDeltaAirspeed(-5.2);
        for (int i = 0; i < 100; i++) {
            location.updateDelta(10);
        }
        assertEquals(4.8, location.getAirspeed(),  0.001,
                "Request of delta airspeed change failed!");
    }

    @Test
    void requestDeltaVerticalSpeed() {
        double[] requests = {10, 12.5, -10, 50};
        double[] expected = {10, 12.5, -10, 13.889}; // Expected result after 100 ticks

        for (int t = 0; t < requests.length; t++) {
            location.reset();
            location.requestDeltaVerticalSpeed(requests[t]);
            // Simulate executor threat for 10 seconds
            for (int i = 0; i < 1000; i++) {
                location.updateDelta(10);
            }
            assertEquals(expected[t], location.getVerticalSpeed(),  0.001,
                    "Request of delta vertical speed failed for vertical speed "+requests[t]);
        }

        // Test delta change
        location.reset();
        location.requestDeltaVerticalSpeed(10);
        for (int i = 0; i < 50; i++) {
            location.updateDelta(10);
        }
        location.requestDeltaVerticalSpeed(-5.2);
        for (int i = 0; i < 100; i++) {
            location.updateDelta(10);
        }
        assertEquals(4.8, location.getVerticalSpeed(),  0.001,
                "Request of delta airspeed change failed!");

    }
}