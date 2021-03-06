package de.thi.dronesim.wind;

import de.thi.dronesim.Simulation;
import de.thi.dronesim.drone.Location;
import de.thi.dronesim.persistence.entity.WindConfig;
import org.junit.jupiter.api.*;


import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Lausch, Christopher
 * @author Hupp, Laurence
 * @author Wittschen, Marvin
 */
class WindTest {

    private Wind wind;
    private Simulation simulation;
    private Location location;
    private final List<WindLayer> windLayers = new ArrayList<>();

    @BeforeEach
    void setup() {
        simulation = new Simulation();
        windLayers.clear();
    }

    @AfterEach
    void stop() {
        wind.onSimulationStop();
    }

    private void setupWind() {
        wind = new Wind(windLayers);
        wind.initialize(simulation);
        wind.onSimulationStart();
    }

    @Test
    void sort_timeAlreadySorted() {
        // Case 1
        int[] result = {0 ,1};
        List<WindLayer> layers = new ArrayList<>();
        layers.add(new WindLayer(0, 0, 0, 10, 0, 10, 0));
        layers.add(new WindLayer(0, 0, 20, 30, 0, 10, 0));
        wind = new Wind(new ArrayList<>(layers));

        for (int i = 0; i < wind.getWindLayers().size(); i++) {
            assertEquals(result[i], wind.getWindLayers().indexOf(layers.get(i)));
        }
    }

    @Test
    void sort_timeUnsorted() {
        // Case 1
        int[] result = {1 ,0};
        List<WindLayer> layers = new ArrayList<>();
        layers.add(new WindLayer(0, 0, 20, 30, 0, 10, 0));
        layers.add(new WindLayer(0, 0, 0, 10, 0, 10, 0));
        wind = new Wind(new ArrayList<>(layers));
        wind.initialize(simulation);

        for (int i = 0; i < wind.getWindLayers().size(); i++) {
            assertEquals(result[i], layers.indexOf(wind.getWindLayers().get(i)));
        }
    }

    @Test
    void sort_altitudeAlreadySorted() {
        // Case 1
        int[] result = {0 ,1};
        List<WindLayer> layers = new ArrayList<>();
        layers.add(new WindLayer(0, 0, 0, 10, 0, 10, 0));
        layers.add(new WindLayer(0, 0, 0, 10, 20, 30, 0));
        wind = new Wind(new ArrayList<>(layers));

        for (int i = 0; i < wind.getWindLayers().size(); i++) {
            assertEquals(result[i], wind.getWindLayers().indexOf(layers.get(i)));
        }
    }

    @Test
    void sort_altitudeUnsorted() {
        // Case 1
        int[] result = {1 ,0};
        List<WindLayer> layers = new ArrayList<>();
        layers.add(new WindLayer(0, 0, 0, 10, 20, 30, 0));
        layers.add(new WindLayer(0, 0, 0, 10, 0, 10, 0));
        wind = new Wind(new ArrayList<>(layers));
        wind.initialize(simulation);

        for (int i = 0; i < wind.getWindLayers().size(); i++) {
            assertEquals(result[i], layers.indexOf(wind.getWindLayers().get(i)));
        }
    }

    @Test
    void sort_mixedUnsorted() {
        // Case 1
        int[] result = {2,3,0,1};
        List<WindLayer> layers = new ArrayList<>();
        layers.add(new WindLayer(0, 0, 20, 30, 0, 10, 0));
        layers.add(new WindLayer(0, 0, 20, 30, 20, 30, 0));
        layers.add(new WindLayer(0, 0, 0, 10, 0, 10, 0));
        layers.add(new WindLayer(0, 0, 0, 10, 20, 30, 0));
        wind = new Wind(new ArrayList<>(layers));
        wind.initialize(simulation);

        for (int i = 0; i < wind.getWindLayers().size(); i++) {
            assertEquals(result[i], layers.indexOf(wind.getWindLayers().get(i)));
        }
    }

    @Test
    void sort_mixed2Unsorted() {
        // Case 1
        int[] result = {4, 3, 1, 0, 2};
        List<WindLayer> layers = new ArrayList<>();
        layers.add(new WindLayer(0, 0, 40, 50, 0, 10, 0));
        layers.add(new WindLayer(0, 0, 30, 40, 20, 30, 0));
        layers.add(new WindLayer(0, 0, 40, 50, 40, 50, 0));
        layers.add(new WindLayer(0, 0, 10, 30, 0, 10, 0));
        layers.add(new WindLayer(0, 0, 0, 30, 40, 50, 0));
        wind = new Wind(new ArrayList<>(layers));
        wind.initialize(simulation);

        for (int i = 0; i < wind.getWindLayers().size(); i++) {
            assertEquals(result[i], layers.indexOf(wind.getWindLayers().get(i)));
        }
    }

    private void assertEqualsWindLayer(WindLayer expected, WindLayer result) {
        assertEquals(expected.getAltitudeBottom(), result.getAltitudeBottom(),
                "Mismatch in altitude bottom");
        assertEquals(expected.getAltitudeTop(), result.getAltitudeTop(),
                "Mismatch in altitude top");
        assertEquals(expected.getTimeStart(), result.getTimeStart(),
                "Mismatch in time start");
        assertEquals(expected.getTimeEnd(), result.getTimeEnd(),
                "Mismatch in time end");
    }

    private void assertEqualsWindLayerList(List<WindLayer> expected, List<WindLayer> result) {
        // Check list length
        assertEquals(expected.size(), result.size(),"List sizes don't match.");
        // Compare all properties
        for (int i = 0; i < expected.size(); i++) {
            assertEqualsWindLayer(expected.get(i), result.get(i));
        }
    }

    @Test
    void normalize_timeAlreadyNormalized() {
        List<WindLayer> layers = new ArrayList<>();
        List<WindLayer> expected = new ArrayList<>();

        layers.add(new WindLayer(0, 0, 0, 10, 0, 10, 0));
        expected.add(new WindLayer(0, 0, -10, 10, -10, 10, 0));

        layers.add(new WindLayer(0, 0, 20, 30, 0, 10, 0));
        expected.add(new WindLayer(0, 0, 20, 30, -10, 10, 0));

        // Start normalization
        wind = new Wind(layers);
        wind.initialize(simulation);
        // Test result
        assertEqualsWindLayerList(expected, wind.getWindLayers());
    }

    @Test
    void normalize_timeNotNormalized() {
        List<WindLayer> layers = new ArrayList<>();
        List<WindLayer> expected = new ArrayList<>();

        layers.add(new WindLayer(0, 0, 0, 13, 0, 10, 0));
        expected.add(new WindLayer(0, 0, -10, 20, -10, 10, 0));

        layers.add(new WindLayer(0, 0, 17, 30, 0, 10, 0));
        expected.add(new WindLayer(0, 0, 20, 30, -10, 10, 0));

        // Start normalization
        wind = new Wind(layers);
        wind.initialize(simulation);
        // Test result
        assertEqualsWindLayerList(expected, wind.getWindLayers());
    }

    @Test
    void normalize_timeDeleting() {
        List<WindLayer> layers = new ArrayList<>();
        List<WindLayer> expected = new ArrayList<>();

        layers.add(new WindLayer(0, 0, 3, 8, 0, 10, 0));
        // Should be removed

        layers.add(new WindLayer(0, 0, 9, 19, 0, 10, 0));
        expected.add(new WindLayer(0, 0, 10, 20, -10, 10, 0));

        // Start normalization
        wind = new Wind(layers);
        wind.initialize(simulation);
        // Test result
        assertEqualsWindLayerList(expected, wind.getWindLayers());
    }

    @Test
    void normalize_timeOverlapping() {
        List<WindLayer> layers = new ArrayList<>();
        List<WindLayer> expected = new ArrayList<>();

        layers.add(new WindLayer(0, 0, 0, 10, 0, 10, 0));
        expected.add(new WindLayer(0, 0, -10, 10, -10, 10, 0));

        layers.add(new WindLayer(0, 0, 0, 20, 0, 10, 0));
        // Should be removed

        // Start normalization
        wind = new Wind(layers);
        wind.initialize(simulation);
        // Test result
        assertEqualsWindLayerList(expected, wind.getWindLayers());
    }

    @Test
    void normalize_altitudeAlreadyNormalized() {
        List<WindLayer> layers = new ArrayList<>();
        List<WindLayer> expected = new ArrayList<>();

        layers.add(new WindLayer(0, 0, 0, 10, 0, 10, 0));
        expected.add(new WindLayer(0, 0, -10, 10, -10, 10, 0));

        layers.add(new WindLayer(0, 0, 0, 10, 20, 30, 0));
        expected.add(new WindLayer(0, 0, -10, 10, 20, 30, 0));

        // Start normalization
        wind = new Wind(layers);
        wind.initialize(simulation);
        // Test result
        assertEqualsWindLayerList(expected, wind.getWindLayers());
    }

    @Test
    void normalize_AltitudeNotNormalized() {
        List<WindLayer> layers = new ArrayList<>();
        List<WindLayer> expected = new ArrayList<>();

        layers.add(new WindLayer(0, 0, 0, 10, 0, 13, 0));
        expected.add(new WindLayer(0, 0, -10, 10, -10, 20, 0));

        layers.add(new WindLayer(0, 0, 0, 10, 17, 30, 0));
        expected.add(new WindLayer(0, 0, -10, 10, 20, 30, 0));

        // Start normalization
        wind = new Wind(layers);
        wind.initialize(simulation);
        // Test result
        assertEqualsWindLayerList(expected, wind.getWindLayers());
    }

    @Test
    void normalize_AltitudeDeleting() {
        List<WindLayer> layers = new ArrayList<>();
        List<WindLayer> expected = new ArrayList<>();

        layers.add(new WindLayer(0, 0, 0, 10, 3, 8, 0));
        // Should be removed

        layers.add(new WindLayer(0, 0, 0, 10, 9, 19, 0));
        expected.add(new WindLayer(0, 0, -10, 10, 10, 20, 0));

        // Start normalization
        wind = new Wind(layers);
        wind.initialize(simulation);
        // Test result
        assertEqualsWindLayerList(expected, wind.getWindLayers());
    }

    @Test
    void normalize_AltitudeOverlapping() {
        List<WindLayer> layers = new ArrayList<>();
        List<WindLayer> expected = new ArrayList<>();

        layers.add(new WindLayer(0, 0, 0, 10, 0, 10, 0));
        expected.add(new WindLayer(0, 0, -10, 10, -10, 10, 0));

        layers.add(new WindLayer(0, 0, 0, 10, 0, 20, 0));
        // Should be removed

        // Start normalization
        wind = new Wind(layers);
        wind.initialize(simulation);
        // Test result
        assertEqualsWindLayerList(expected, wind.getWindLayers());
    }

    @Test
    void normalize_mixed() {
        List<WindLayer> layers = new ArrayList<>();
        List<WindLayer> expected = new ArrayList<>();

        layers.add(new WindLayer(0, 0, 0, 15, 0, 13, 0));
        expected.add(new WindLayer(0, 0, -10, 20, -10, 20, 0));

        layers.add(new WindLayer(0, 0, 0, 16, 23, 33, 0));
        expected.add(new WindLayer(0, 0, -10, 20, 30, 40, 0));

        // Start normalization
        wind = new Wind(layers);
        wind.initialize(simulation);
        // Test result
        assertEqualsWindLayerList(expected, wind.getWindLayers());
    }

    @Test
    void normalize_closeLayers() {
        List<WindLayer> layers = new ArrayList<>();
        List<WindLayer> expected = new ArrayList<>();

        layers.add(new WindLayer(0, 0, 0, 10, 0, 10, 0));
        expected.add(new WindLayer(0, 0, -10, 10, -10, 10, 0));

        layers.add(new WindLayer(0, 0, 10, 20, 0, 10, 0));
        expected.add(new WindLayer(0, 0, 10, 20, -10, 10, 0));

        // Start normalization
        wind = new Wind(layers);
        wind.initialize(simulation);
        // Test result
        assertEqualsWindLayerList(expected, wind.getWindLayers());
    }

    @Test
    void normalize_mixedOverlapping() {
        List<WindLayer> layers = new ArrayList<>();
        List<WindLayer> expected = new ArrayList<>();

        layers.add(new WindLayer(0, 0, 0, 20, 0, 20, 0));
        expected.add(new WindLayer(0, 0, -10, 20, -10, 20, 0));

        layers.add(new WindLayer(0, 0, 10, 30, 10, 50, 0));
        // Should be removed

        // Start normalization
        wind = new Wind(layers);
        wind.initialize(simulation);
        // Test result
        assertEqualsWindLayerList(expected, wind.getWindLayers());
    }

    @Test
    void normalize_isValid() {
        List<WindLayer> layers = new ArrayList<>();
        List<WindLayer> expected = new ArrayList<>();

        layers.add(new WindLayer(0, 0, 10, 0, 0, 20, 0));
        // Should be removed

        layers.add(new WindLayer(0, 0, 10, 30, 10, 5, 0));
        // Should be removed

        // Start normalization
        wind = new Wind(layers);
        wind.initialize(simulation);
        // Test result
        assertEqualsWindLayerList(expected, wind.getWindLayers());
    }

    @Test
    void findWindLayer_timeSeries() {
        createLocation(20,10,210);
        windLayers.add(new WindLayer(10, 10, 0, 20, 0, 100, 30));
        windLayers.add(new WindLayer(11, 11, 20, 40, 0, 100, 30));
        windLayers.add(new WindLayer(12, 12, 40, 60, 0, 100, 30));
        windLayers.add(new WindLayer(13, 13, 60, 80, 0, 100, 30));
        windLayers.add(new WindLayer(14, 14, 80, 100, 0, 100, 30));
        setupWind();

        wind.applyWind(location, 30000);
        assertEquals(21, location.getGroundSpeed(), 0.5,  "Wrong layer selected for t=30!");

        wind.applyWind(location, 50000);
        assertEquals(22, location.getGroundSpeed(), 0.5,  "Wrong layer selected for t=50!");

        wind.applyWind(location, 90000);
        assertEquals(24, location.getGroundSpeed(), 0.5,  "Wrong layer selected for t=90!");
    }

    @Test
    void applyWind_tailWind() {
        createLocation(20,10,210);
        windLayers.add(new WindLayer(10, 10, 0, 100, 0, 100, 30));
        setupWind();
        wind.applyWind(location, 20000);
        assertEquals(20, location.getGroundSpeed(), 0.5,  "Wrong ground speed");
        assertEquals(210, location.getTrack(), 0.5,  "Wrong track");
    }

    @Test
    void applyWind_tailWindEast() {
        createLocation(20,10,90);
        windLayers.add(new WindLayer(10, 10, 0, 100, 0, 100, 270));
        setupWind();
        wind.applyWind(location, 20000);
        assertEquals(20, location.getGroundSpeed(), 0.5,  "Wrong ground speed");
        assertEquals(90, location.getTrack(), 0.5,  "Wrong track");
    }

    @Test
    void applyWind_tailWindWest() {
        createLocation(20,10,270);
        windLayers.add(new WindLayer(10, 10, 0, 100, 0, 100, 90));
        setupWind();
        wind.applyWind(location, 20000);
        assertEquals(20, location.getGroundSpeed(), 0.5,  "Wrong ground speed");
        assertEquals(270, location.getTrack(), 0.5,  "Wrong track");
    }

    @Test
    void applyWind_headWind() {
        createLocation(20,5,210);
        windLayers.add(new WindLayer(3, 3, 0, 100, 0, 100, 210));
        setupWind();
        wind.applyWind(location, 20000);
        assertEquals(2, location.getGroundSpeed(), 0.5,  "Wrong ground speed");
        assertEquals(210, location.getTrack(), 0.5, "Wrong track");
    }

    @Test
    void applyWind_headWindStrong() {
        createLocation(20,2,210);
        windLayers.add(new WindLayer(10, 10, 0, 100, 0, 100, 210));
        setupWind();
        wind.applyWind(location, 20000);
        assertEquals(8, location.getGroundSpeed(), 0.5,"Wrong ground speed");
        assertEquals(30, location.getTrack(), 0.5, "Wrong track");
    }

    @Test
    void applyWind_headLeft() {
        createLocation(20,10,0);
        windLayers.add(new WindLayer(5, 5, 0, 100, 0, 100, 315));
        setupWind();
        wind.applyWind(location, 20000);
        assertEquals(7, location.getGroundSpeed(), 0.5,  "Wrong ground speed");
        assertEquals(29, location.getTrack(), 0.5,  "Wrong track");
    }

    @Test
    void applyWind_headLeftStrong() {
        createLocation(20,10,0);
        windLayers.add(new WindLayer(30, 30, 0, 100, 0, 100, 315));
        setupWind();
        wind.applyWind(location, 20000);
        assertEquals(24, location.getGroundSpeed(), 0.5, "Wrong ground speed");
        assertEquals(118, location.getTrack(), 0.5,  "Wrong track");
    }

    @Test
    void applyWind_headRight() {
        createLocation(20,10,0);
        windLayers.add(new WindLayer(5, 5, 0, 100, 0, 100, 45));
        setupWind();
        wind.applyWind(location, 20000);
        assertEquals(7, location.getGroundSpeed(), 0.5, "Wrong ground speed");
        assertEquals(331, location.getTrack(), 0.5, "Wrong track");
    }

    @Test
    void applyWind_tailLeft() {
        createLocation(20,10,0);
        windLayers.add(new WindLayer(5, 5, 0, 100, 0, 100, 225));
        setupWind();
        wind.applyWind(location, 20000);
        assertEquals(14, location.getGroundSpeed(), 0.5, "Wrong ground speed");
        assertEquals(15, location.getTrack(),0.5, "Wrong track");
    }

    @Test
    void applyWind_tailRight() {
        createLocation(20,10,0);
        windLayers.add(new WindLayer(5, 5, 0, 100, 0, 100, 135));
        setupWind();
        wind.applyWind(location, 20000);
        assertEquals(14, location.getGroundSpeed(), 0.5, "Wrong ground speed");
        assertEquals(345, location.getTrack(), 0.5, "Wrong track");
    }

    @Test
    void applyWind_left() {
        createLocation(20,10,0);
        windLayers.add(new WindLayer(5, 5, 0, 100, 0, 100, 90));
        setupWind();
        wind.applyWind(location, 20000);
        assertEquals(11, location.getGroundSpeed(), 0.5,  "Wrong ground speed");
        assertEquals(333, location.getTrack(), 0.5,  "Wrong track");
    }

    @Test
    void applyWind_right() {
        createLocation(20,10,0);
        windLayers.add(new WindLayer(5, 5, 0, 100, 0, 100, 270));
        setupWind();
        wind.applyWind(location, 20000);
        assertEquals(11, location.getGroundSpeed(), 0.5,  "Wrong ground speed");
        assertEquals(27, location.getTrack(), 0.5,  "Wrong track");
    }

    @RepeatedTest(100)
    void applyWind_gustGeneration() {
        WindLayer gustLayer = new WindLayer(5, 10, 0, 100, 0, 100, 135);
        windLayers.add(gustLayer);

        createLocation(5,10,330);
        setupWind();
        wind.applyWind(location, 20000);

        assertTrue(gustLayer.isValid(),"Layer is invalid");
        assertTrue(gustLayer.getNextGustStart() > 0, "Start time was not generated");
        assertTrue(gustLayer.getNextGustSpeed() >= gustLayer.getWindSpeed(), "Gust speed is smaller " +
                "than wind speed");
    }

    @Test
    void applyWind_gustCenter() {
        WindLayer gustLayer = new WindLayer(5, 20, 0, 100, 0, 100, 180);
        gustLayer.setNextGustStart(20);
        gustLayer.setNextGustSpeed(20);
        gustLayer.setNextGustDuration(6);
        windLayers.add(gustLayer);

        createLocation(5,10,0);
        setupWind();
        wind.applyWind(location, 23000);

        assertEquals(30, location.getGroundSpeed(), 0.5);
    }

    @Test
    void applyWind_gustRise() {
        WindLayer gustLayer = new WindLayer(5, 20, 0, 100, 0, 100, 180);
        gustLayer.setNextGustStart(20);
        gustLayer.setNextGustSpeed(20);
        windLayers.add(gustLayer);

        createLocation(5,10,0);
        setupWind();
        wind.applyWind(location, 21000);

        assertEquals(22.5, location.getGroundSpeed(), 0.5);
    }

    @Test
    void applyWind_gustFall() {
        WindLayer gustLayer = new WindLayer(5, 20, 0, 100, 0, 100, 180);
        gustLayer.setNextGustStart(20);
        gustLayer.setNextGustSpeed(20);
        windLayers.add(gustLayer);

        createLocation(5,10,0);
        setupWind();
        wind.applyWind(location, 23000);

        assertEquals(22.5, location.getGroundSpeed(), 0.5);
    }

    @Test
    void interpolation_leftOutside() {
        createLocation(2, 20, 0);
        windLayers.add(new WindLayer(10, 10, 10, 20, 0, 10, 90));

        setupWind();
        wind.applyWind(location, 8000);

        assertEquals(20.6, location.getGroundSpeed(),0.5, "Wrong ground speed");
        assertEquals(351.9, location.getTrack(),0.5, "Wrong track");
    }

    @Test
    void interpolation_rightInside() {
        createLocation(2, 20, 0);
        windLayers.add(new WindLayer(10, 10, 0, 20, 0, 10, 90));
        setupWind();
        wind.applyWind(location, 18000);

        assertEquals(21.4, location.getGroundSpeed(),0.5, "Wrong ground speed");
        assertEquals(341.1, location.getTrack(),0.5, "Wrong track");
    }

    @Test
    void interpolation_above() {
        createLocation(12, 20, 0);
        windLayers.add(new WindLayer(10, 10, 0, 20, 0, 10, 90));
        setupWind();
        wind.applyWind(location, 10000);

        assertEquals(20.6, location.getGroundSpeed(),0.5, "Wrong ground speed");
        assertEquals(351.9, location.getTrack(),0.5, "Wrong track");
    }

    @Test
    void interpolation_underTop() {
        createLocation(8, 20, 0);
        windLayers.add(new WindLayer(10, 10, 0, 20, 0, 10, 90));
        setupWind();
        wind.applyWind(location, 10000);

        assertEquals(21.4, location.getGroundSpeed(),0.5, "Wrong ground speed");
        assertEquals(341.1, location.getTrack(),0.5, "Wrong track");
    }

    @Test
    void interpolation_outsideOfRange() {
        createLocation(2, 20, 0);
        windLayers.add(new WindLayer(10, 10, 10, 20, 0, 10, 0));
        setupWind();
        wind.applyWind(location, 1000);

        assertEquals(20, location.getGroundSpeed(), 0.5, "Wrong ground speed");
        assertEquals(0, location.getTrack(),0.5, "Wrong track");
    }

    @Test
    void interpolation_insideLayer() {
        createLocation(2, 20, 0);
        windLayers.add(new WindLayer(10, 10, 0, 20, 0, 10, 0));
        setupWind();
        wind.applyWind(location, 10000);

        assertEquals(10, location.getGroundSpeed(),0.5, "Wrong ground speed");
        assertEquals(0, location.getTrack(),0.5, "Wrong track");
    }

    @Test
    void interpolation_twoLayersLeft() {
        createLocation(2, 20, 0);
        windLayers.add(new WindLayer(10, 10, 0, 30, 0, 10, 120));
        windLayers.add(new WindLayer(10, 10, 30, 60, 0, 10, 240));
        setupWind();
        wind.applyWind(location, 27000);

        assertEquals(25.42, location.getGroundSpeed(),0.5, "Wrong ground speed");
        assertEquals(348.26, location.getTrack(),0.5, "Wrong track");
    }

    @Test
    void interpolation_twoLayersRight() {
        createLocation(2, 20, 0);
        windLayers.add(new WindLayer(10, 10, 0, 30, 0, 10, 120));
        windLayers.add(new WindLayer(10, 10, 30, 60, 0, 10, 240));
        setupWind();
        wind.applyWind(location, 32000);

        assertEquals(25.24, location.getGroundSpeed(),0.5, "Wrong ground speed");
        assertEquals(7.89, location.getTrack(),0.5, "Wrong track");
    }

    @Test
    void interpolation_twoLayersCenter() {
        createLocation(2, 20, 0);
        windLayers.add(new WindLayer(10, 10, 0, 30, 0, 10, 120));
        windLayers.add(new WindLayer(10, 10, 30, 60, 0, 10, 240));
        setupWind();
        wind.applyWind(location, 30000);

        assertEquals(25, location.getGroundSpeed(),0.5, "Wrong ground speed");
        assertEquals(0, location.getTrack(),0.5, "Wrong track");
    }

    @Test
    void interpolation_twoLayersBottom() {
        createLocation(9, 20, 0);
        windLayers.add(new WindLayer(10, 10, 0, 30, 0, 10, 0));
        windLayers.add(new WindLayer(10, 10, 0, 30, 10, 20, 180));
        setupWind();
        wind.applyWind(location, 20000);

        assertEquals(18, location.getGroundSpeed(),0.5, "Wrong ground speed");
        assertEquals(0, location.getTrack(),0.5, "Wrong track");
    }

    @Test
    void interpolation_twoLayersTop() {
        createLocation(11, 20, 0);
        windLayers.add(new WindLayer(10, 10, 0, 30, 0, 10, 0));
        windLayers.add(new WindLayer(10, 10, 0, 30, 10, 20, 180));
        setupWind();
        wind.applyWind(location, 20000);

        assertEquals(22, location.getGroundSpeed(),0.5, "Wrong ground speed");
        assertEquals(0, location.getTrack(),0.5, "Wrong track");
    }

    @Test
    void interpolation_twoLayersAbove() {
        createLocation(8, 20, 0);
        windLayers.add(new WindLayer(10, 10, 0, 20, 10, 20, 90));
        windLayers.add(new WindLayer(10, 10, 20, 40, 10, 20, 270));
        setupWind();
        wind.applyWind(location, 19000);

        assertEquals(20.01, location.getGroundSpeed(),0.5, "Wrong ground speed");
        assertEquals(358.28, location.getTrack(),0.5, "Wrong track");
    }

    @Test
    void interpolation_threeLayers() {
        createLocation(19, 20, 0);

        windLayers.add(new WindLayer(10, 10, 0, 20, 20, 40, 90));
        windLayers.add(new WindLayer(10, 10, 20, 40, 0, 20, 180));
        windLayers.add(new WindLayer(10, 10, 20, 40, 20, 40, 270));
        setupWind();
        wind.applyWind(location, 18000);



        assertEquals(21.86, location.getGroundSpeed(),0.5, "Wrong ground speed");
        assertEquals(355.8, location.getTrack(),0.5, "Wrong track");
    }

    @Test
    void interpolation_fourLayers() {
        createLocation(19, 20, 0);
        windLayers.add(new WindLayer(10, 10, 0, 20, 0, 20, 30));
        windLayers.add(new WindLayer(10, 10, 0, 20, 20, 40, 60));
        windLayers.add(new WindLayer(10, 10, 20, 40, 0, 20, 90));
        windLayers.add(new WindLayer(10, 10, 20, 40, 20, 40, 120));
        setupWind();
        wind.applyWind(location, 18000);

        assertEquals(17.22, location.getGroundSpeed(),0.5, "Wrong ground speed");
        assertEquals(334.68, location.getTrack(),0.5, "Wrong track");
    }

    @Test
    void interpolation_fourLayersAltitudeFirst() {
        createLocation(13, 20, 0);
        windLayers.add(new WindLayer(10, 10, 0, 20, 0, 20, 30));
       // windLayers.add(new WindLayer(10, 10, 0, 20, 20, 40, 60));
        windLayers.add(new WindLayer(10, 10, 20, 40, 0, 10, 90));
        windLayers.add(new WindLayer(10, 10, 20, 40, 10, 40, 120));
        setupWind();
        wind.applyWind(location, 18000);

        assertEquals(16.35, location.getGroundSpeed(),0.5, "Wrong ground speed");
        assertEquals(337.8, location.getTrack(),0.5, "Wrong track");
    }

    @Test
    void interpolation_fourLayersTimeFirst_1(){
        createLocation(19, 20, 0);
        windLayers.add(new WindLayer(10, 10, 0, 30, 0, 20, 30));
        windLayers.add(new WindLayer(10, 10, 0, 20, 20, 40, 60));
        windLayers.add(new WindLayer(10, 10, 20, 40, 20, 40, 90));
        windLayers.add(new WindLayer(10, 10, 30, 40, 0, 20, 120));
        setupWind();
        wind.applyWind(location, 18000);

        assertEquals(14.95, location.getGroundSpeed(),0.5, "Wrong ground speed");
        assertEquals(333.7, location.getTrack(),0.5, "Wrong track");
    }

    @Test
    void interpolation_fourLayersTimeFirst_2() {
        createLocation(19, 20, 0);
        windLayers.add(new WindLayer(10, 10, 0, 30, 0, 20, 30));
        windLayers.add(new WindLayer(10, 10, 0, 20, 20, 40, 60));
        windLayers.add(new WindLayer(10, 10, 20, 40, 20, 40, 90));
        windLayers.add(new WindLayer(10, 10, 30, 40, 0, 20, 120));
        setupWind();
        wind.applyWind(location, 32000);

        assertEquals(22.24, location.getGroundSpeed(),0.5, "Wrong ground speed");
        assertEquals(337.43, location.getTrack(),0.5, "Wrong track");
    }

    @Test
    void load() {
        simulation = new Simulation("src/test/resources/de/thi/dronesim/wind/WindConfigTest.json");
        wind = new Wind();
        wind.initialize(simulation);

        List<WindConfig> windConfigList = simulation.getConfig().getWindConfigList();
        assertNotNull(windConfigList);

        List<WindLayer> expected = new ArrayList<>();
        expected.add(new WindLayer(1.0, 1.0, -10, 10, -10, 10, 1));
        expected.add(new WindLayer(2.0, 2.0, 10, 20, -10, 10, 2));
        expected.add(new WindLayer(3.0, 3.0, 20, 30, -10, 10, 3));
        expected.add(new WindLayer(4.0, 4.0, 30, 40, -10, 10, 4));
        expected.add(new WindLayer(5.0, 5.0, 40, 50, -10, 10, 5));
        assertEquals(expected.size(), wind.getWindLayers().size());

        // Compare with config
        for (int i = 0; i < expected.size(); i++) {
            assertConfig(expected.get(i), wind.getWindLayers().get(i));
        }
    }

    private void assertConfig(WindLayer expected, WindLayer actual) {
        assertEquals(expected.getTimeStart(), actual.getTimeStart());
        assertEquals(expected.getTimeEnd(), actual.getTimeEnd());
        assertEquals(expected.getWindSpeed(), actual.getWindSpeed());
        assertEquals(expected.getGustSpeed(), actual.getGustSpeed());
        assertEquals(expected.getWindDirection(), actual.getWindDirection());
        assertEquals(expected.getAltitudeTop(), actual.getAltitudeTop());
        assertEquals(expected.getAltitudeBottom(), actual.getAltitudeBottom());
    }

    /**
     * method to set the location of the drone
     * @param y Y-coordinate
     * @param airspeed airspeed of the drone
     * @param heading heading of the drone
     */
    public void createLocation(int y, int airspeed, int heading) {
        location = new Location(0, y, 0);
        location.setAirspeed(airspeed);
        location.setHeading(heading);
    }

    @Test
    void getWindAt_noWind() {
        createLocation(20,10,210);
        setupWind();
        wind.applyWind(location, 20000);
        Wind.CurrentWind wind = Wind.getWindAt(location);
        assertEquals(0, wind.getWindSpeed(), 1, "Wrong wind speed");
        assertEquals(210, wind.getWindDirection(), 1, "Wrong wind direction");
    }

    @Test
    void getWindAt_tailWind() {
        createLocation(20,10,210);
        windLayers.add(new WindLayer(10, 10, 0, 100, 0, 100, 30));
        setupWind();
        wind.applyWind(location, 20000);
        Wind.CurrentWind wind = Wind.getWindAt(location);
        assertEquals(10, wind.getWindSpeed(), 1, "Wrong wind speed");
        assertEquals(30, wind.getWindDirection(), 1, "Wrong wind direction");
    }

    @Test
    void getWindAt_headWind() {
        createLocation(20,5,210);
        windLayers.add(new WindLayer(3, 3, 0, 100, 0, 100, 210));
        setupWind();
        wind.applyWind(location, 20000);
        Wind.CurrentWind wind = Wind.getWindAt(location);
        assertEquals(3, wind.getWindSpeed(), 1, "Wrong wind speed");
        assertEquals(210, wind.getWindDirection(), 1, "Wrong wind direction");
    }

    @Test
    void getWindAt_headWindStrong() {
        createLocation(20,2,210);
        windLayers.add(new WindLayer(10, 10, 0, 100, 0, 100, 210));
        setupWind();
        wind.applyWind(location, 20000);
        Wind.CurrentWind wind = Wind.getWindAt(location);
        assertEquals(10, wind.getWindSpeed(), 1, "Wrong wind speed");
        assertEquals(210, wind.getWindDirection(), 1, "Wrong wind direction");
    }

    @Test
    void getWindAt_headLeft() {
        createLocation(20,10,0);
        windLayers.add(new WindLayer(5, 5, 0, 100, 0, 100, 315));
        setupWind();
        wind.applyWind(location, 20000);
        Wind.CurrentWind wind = Wind.getWindAt(location);
        assertEquals(5, wind.getWindSpeed(),1, "Wrong wind speed");
        assertEquals(315, wind.getWindDirection(), 1,"Wrong wind direction");
    }

    @Test
    void getWindAt_headLeftStrong() {
        createLocation(20,10,0);
        windLayers.add(new WindLayer(30, 30, 0, 100, 0, 100, 315));
        setupWind();
        wind.applyWind(location, 20000);
        Wind.CurrentWind wind = Wind.getWindAt(location);
        assertEquals(30, wind.getWindSpeed(), 1, "Wrong wind speed");
        assertEquals(315, wind.getWindDirection(), 1, "Wrong wind direction");
    }

    @Test
    void getWindAt_headRight() {
        createLocation(20,10,0);
        windLayers.add(new WindLayer(5, 5, 0, 100, 0, 100, 45));
        setupWind();
        wind.applyWind(location, 20000);
        Wind.CurrentWind wind = Wind.getWindAt(location);
        assertEquals(5, wind.getWindSpeed(), 1,  "Wrong wind speed");
        assertEquals(45, wind.getWindDirection(), 1,  "Wrong wind direction");
    }

    @Test
    void getWindAt_tailLeft() {
        createLocation(20,10,0);
        windLayers.add(new WindLayer(5, 5, 0, 100, 0, 100, 225));
        setupWind();
        wind.applyWind(location, 20000);
        Wind.CurrentWind wind = Wind.getWindAt(location);
        assertEquals(5, wind.getWindSpeed(), 1,  "Wrong wind speed");
        assertEquals(225, wind.getWindDirection(), 1, "Wrong wind direction");
    }

    @Test
    void getWindAt_tailRight() {
        createLocation(20,10,0);
        windLayers.add(new WindLayer(5, 5, 0, 100, 0, 100, 135));
        setupWind();
        wind.applyWind(location, 20000);
        Wind.CurrentWind wind = Wind.getWindAt(location);
        assertEquals(5, wind.getWindSpeed(), 1,  "Wrong wind speed");
        assertEquals(135, wind.getWindDirection(), 1,  "Wrong wind direction");
    }

    @Test
    void getWindAt_left() {
        createLocation(20,10,0);
        windLayers.add(new WindLayer(5, 5, 0, 100, 0, 100, 90));
        setupWind();
        wind.applyWind(location, 20000);
        Wind.CurrentWind wind = Wind.getWindAt(location);
        assertEquals(5, wind.getWindSpeed(), 1, "Wrong wind speed");
        assertEquals(90, wind.getWindDirection(), 1, "Wrong wind direction");
    }

    @Test
    void getWindAt_right() {
        createLocation(20,10,0);
        windLayers.add(new WindLayer(5, 5, 0, 100, 0, 100, 270));
        setupWind();
        wind.applyWind(location, 20000);
        Wind.CurrentWind wind = Wind.getWindAt(location);
        assertEquals(5, wind.getWindSpeed(), 1, "Wrong wind speed");
        assertEquals(270, wind.getWindDirection(), 1, "Wrong wind direction");
    }

}