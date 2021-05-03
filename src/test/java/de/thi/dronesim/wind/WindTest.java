package de.thi.dronesim.wind;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.thi.dronesim.drone.Location;
import de.thi.dronesim.drone.UfoSim;

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
        windGust = new Wind(createWindGustLayerList());
    }

    @Test
    void applyWindInSameDirection() {
        setUpLocation(5,5,5,10,210);
        wind.applyWind(location);
        assertEquals(13, location.getGroundSpeed(), "Wind in same direction as drone");
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
        wind.load('windlayer.json');
        List<WindLayer> layers = wind.getWindLayers();
        boolean result = true;
        for(int i = 0; i < layers.size(); i++){
            if(i != layers.size() -1){
                if (layers.get(i + 1).getTimeStart() - layers.get(i).getTimeEnd() < 10)
                    result = false;
            }
        }
        assertTrue(result);
    }

    /**
     * method to set the simulation time
     * @param input set the simulation to that time
     * @throws NoSuchFieldException throws if time is not accessible
     * @throws IllegalAccessException throws if illegal expression
     */
    void setSimulationTime(int input) throws NoSuchFieldException, IllegalAccessException{
        Field timeField = UfoSim.getInstance().getClass()
                .getDeclaredField("time");
        timeField.setAccessible(true);
        byte time = (byte) input;
        timeField.setByte(UfoSim.getInstance(), time);
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