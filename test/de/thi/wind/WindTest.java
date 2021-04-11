package de.thi.wind;

import static org.junit.jupiter.api.Assertions.*;


import de.thi.ufo.Location;
import de.thi.ufo.UfoSim;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

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

        Field timeField = UfoSim.getInstance().getClass()
                .getDeclaredField("time");
        timeField.setAccessible(true);
        byte time = 2;
        timeField.setByte(UfoSim.getInstance(), time);

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


    void applyWindGustStart() throws NoSuchFieldException, IllegalAccessException {
        setUpLocation(5,5,5,10,330);

        Field timeField = UfoSim.getInstance().getClass()
                .getDeclaredField("time");
        timeField.setAccessible(true);
        byte time = 1;
        timeField.setByte(UfoSim.getInstance(), time);

        windGust.applyWind(location);
        assertEquals(311, location.getTrack(), 1, "check Track");
        assertEquals(9, location.getGroundSpeed(),1, "check ground speed");
    }


    public void setUpLocation(int  x, int y, int z, int airspeed, int heading){
        location = new Location(x,y,z);
        location.setAirspeed(airspeed);
        location.setHeading(heading);
    }

    public List<WindLayer> createWindLayerList(){
        List<WindLayer> layer = new ArrayList<WindLayer>();
        for(int i = 0; i < amountWindLayer; i++){
            layer.add(createWindLayer(i));
        }
        return layer;
    }

    public List<WindLayer> createWindGustLayerList(){
        List<WindLayer> layer = new ArrayList<WindLayer>();
        for(int i = 0; i < amountWindLayer; i++){
            layer.add(createWindGustLayer(i, 4));
        }
        return layer;
    }

    public WindLayer createWindLayer(int pos){
        pos *= 10;
        return new WindLayer(windSpeed + pos,  gustSpeed + pos,  timeStart + pos,
                timeEnd + pos,  altitudeBottom + pos, altitudeTop + pos,
                windDirection+ pos);
    }

    public WindLayer createWindGustLayer(int pos, double gust){
        pos *= 10;
        WindLayer wind = new WindLayer(windSpeed + pos,  gust + pos,  timeStart + pos,
                timeEnd + pos,  altitudeBottom + pos, altitudeTop + pos,
                windDirection+ pos);
        wind.setNextGustStart(0);
        wind.setNextGustSpeed(gust);
        return wind;
    }



}