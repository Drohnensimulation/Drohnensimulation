package de.thi.dronesim.wind;

import de.thi.dronesim.Simulation;
import de.thi.dronesim.drone.Location;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class WindTest {

    private Wind wind;
    private Wind windGust;
    final int amountWindLayer = 5;
    final double windSpeed = 3;
    final double gustSpeed = 3;
    final double timeStart = 0;
    final double timeEnd = 10;
    final double altitudeTop = 10;
    final double windDirection = 30;
    final double altitudeBottom = 0;
    final int timeMinDistance = 10;

    private Location location;

    @BeforeEach
    public void setUpWind() throws Exception{
        wind = new Wind(createWindLayerList());
        wind.setSimulation(new Simulation());
        windGust = new Wind(createWindGustLayerList());
    }


    @Test
    void sort_timeAlreadySorted() {
        // Case 1
        int[] result = {0 ,1};
        List<WindLayer> layers = new ArrayList<>();
        layers.add(new WindLayer(0, 0, 0, 10, 0, 10, 0));
        layers.add(new WindLayer(0, 0, 20, 30, 0, 10, 0));
        Wind wind = new Wind(new ArrayList<>(layers));

        for (int i = 0; i < wind.getWindLayers().size() -1; i++) {
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
        Wind wind = new Wind(new ArrayList<>(layers));

        for (int i = 0; i < wind.getWindLayers().size() -1; i++) {
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
        Wind wind = new Wind(new ArrayList<>(layers));

        for (int i = 0; i < wind.getWindLayers().size() -1; i++) {
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
        Wind wind = new Wind(new ArrayList<>(layers));

        for (int i = 0; i < wind.getWindLayers().size() -1; i++) {
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
        Wind wind = new Wind(new ArrayList<>(layers));

        for (int i = 0; i < wind.getWindLayers().size() -1; i++) {
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
        Wind wind = new Wind(new ArrayList<>(layers));

        for (int i = 0; i < wind.getWindLayers().size() -1; i++) {
            assertEquals(result[i], layers.indexOf(wind.getWindLayers().get(i)));
        }
    }

    @Test
    void applyWindInSameDirection() {
        setSimulationTime(15);
        setUpLocation(5,5,5,10,210);
        wind.applyWind(location);
        assertEquals(13, location.getGroundSpeed(), "Wind in same direction as drone");
    }

    @Test
    void getWindAt() {
        setSimulationTime(15);
        setUpLocation(5, 5, 5, 10, 210);
        wind.applyWind(location);
        Wind.CurrentWind wind = Wind.getWindAt(location);
        assertEquals(3, wind.getWindSpeed(), "");
        assertEquals(30, wind.getWindDirection(), "");
    }

    @Test
    void applyWindInOppositeDirection() {
        setUpLocation(5,5,5,10,30);
        wind.applyWind(location);
        assertEquals(7, location.getGroundSpeed(), "Wind in opposite direction as drone");
    }

    @Test
    void applyWindFromSide() {
        setUpLocation(5,5,5,10,180);
        wind.applyWind(location);
        assertEquals(187, location.getTrack(), 1, "check Track");
        assertEquals(12, location.getGroundSpeed(),1, "check ground speed");
    }

    @Test
    void applyWindOtherFromSide() {
        setUpLocation(5,5,5,10,330);
        wind.applyWind(location);
        assertEquals(313, location.getTrack(), 1, "check Track");
        assertEquals(9, location.getGroundSpeed(),1, "check ground speed");
    }

    @Test
    void applyWindGustTop() throws NoSuchFieldException, IllegalAccessException {
        setUpLocation(5,5,5,10,330);
        setSimulationTime(2);
        windGust.applyWind(location);
        assertEquals(310, location.getTrack(), 1, "check Track");
        assertEquals(9, location.getGroundSpeed(),1, "check ground speed");
    }

    @Test
    void applyWindBetweenLayers() {
        setUpLocation(5,5,10,10,330);
        wind.applyWind(location);
        assertEquals(288, location.getTrack(), 1, "check Track");
        assertEquals(11, location.getGroundSpeed(),1, "check ground speed");
    }

    @ Test
    void applyWindGustStart() throws NoSuchFieldException, IllegalAccessException {
        setUpLocation(5,5,5,10,330);
        setSimulationTime(1);
        windGust.applyWind(location);
        assertEquals(311, location.getTrack(), 1, "check Track");
        assertEquals(9, location.getGroundSpeed(),1, "check ground speed");
    }

    @Test
    void applyWindBetweenLayersTimeBasedMid() throws NoSuchFieldException, IllegalAccessException{
        setUpLocation(5,5,10,10,330);
        setSimulationTime(15);
        wind.applyWind(location);
        assertEquals(311, location.getTrack(), 1, "check Track");
        assertEquals(9, location.getGroundSpeed(),1, "check ground speed");
    }

    @Test
    void applyWindBetweenLayersTimeBasedLeft() throws NoSuchFieldException, IllegalAccessException{
        setUpLocation(5,5,10,10,330);
        setSimulationTime(12);
        wind.applyWind(location);
        assertEquals(311, location.getTrack(), 1, "check Track");
        assertEquals(9, location.getGroundSpeed(),1, "check ground speed");
    }

    @Test
    void applyWindBetweenLayersTimeBasedRight() throws NoSuchFieldException, IllegalAccessException{
        setUpLocation(5,5,10,10,330);
        setSimulationTime(17);
        wind.applyWind(location);
        assertEquals(311, location.getTrack(), 1, "check Track");
        assertEquals(9, location.getGroundSpeed(),1, "check ground speed");
    }

    @Test
    void loadWindLayerTest(){
//        wind.load("windlayer.json");
//        List<WindLayer> layers = wind.getWindLayers();
//        boolean result = true;
//        for(int i = 0; i < layers.size(); i++){
//            if(i != layers.size() -1){
//                if (layers.get(i + 1).getTimeStart() - layers.get(i).getTimeEnd() < 10)
//                    result = false;
//            }
//        }
//        assertTrue(result);
    }

    /**
     * method to set the simulation time
     * @param input set the simulation to that time
     */
    void setSimulationTime(int input) {
        try {
            Field timeField = wind.getSimulation().getClass()
                    .getDeclaredField("time");
            timeField.setAccessible(true);
            byte time = (byte) input;
            timeField.setByte(wind.getSimulation(), time);
        } catch (Exception ignored) {};
    }


    /**
     * method to set the location of the drone
     * @param x x coordinate
     * @param y y coordinate
     * @param z z coordinate
     * @param airspeed airspeed of the drone
     * @param heading heading of the drone
     */
    public void setUpLocation(int  x, int y, int z, int airspeed, int heading){
        location = new Location(x,y,z);
        location.setAirspeed(airspeed);
        location.setHeading(heading);
    }

    /**
     * creates amount of windlayers specified in amountWindLayer
     * @return List<WindLayer>
     */
    public List<WindLayer> createWindLayerList(){
        List<WindLayer> layer = new ArrayList<WindLayer>();
        for(int i = 0; i < amountWindLayer; i++){
            layer.add(createWindLayer(i));
        }
        return layer;
    }

    /**
     * creates amount of windlayers with gustwinds specified in amountWindLayer
     * @return List<WindLayer>
     */
    public List<WindLayer> createWindGustLayerList(){
        List<WindLayer> layer = new ArrayList<WindLayer>();
        for(int i = 0; i < amountWindLayer; i++){
            layer.add(createWindGustLayer(i, 4));
        }
        return layer;
    }

    /**
     * method creates a single windlayer
     * @param pos
     * @return Windlayer
     */
    public WindLayer createWindLayer(int pos){
        pos *= 10;

        return new WindLayer(windSpeed + pos,  gustSpeed + pos,  timeStart + (pos + timeMinDistance),
                timeEnd + (pos + timeMinDistance),  altitudeBottom + pos, altitudeTop + pos,
                windDirection+ pos);
    }

    /**
     * method creates windlayer with gustwinds
     * @param pos
     * @param gust gustwind speed
     * @return Windlayer
     */
    public WindLayer createWindGustLayer(int pos, double gust){
        pos *= 10;
        WindLayer wind = new WindLayer(windSpeed + pos,  gust,  timeStart + (pos + timeMinDistance),
                timeEnd + (pos + timeMinDistance),  altitudeBottom + pos, altitudeTop + pos,
                windDirection+ pos);
        wind.setNextGustStart(0);
        wind.setNextGustSpeed(gust);
        return wind;
    }

}