package de.thi.dronesim.sensor.types;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
	 * Class to test UltrasonicSensors.
	 *
	 * @author Moris Breitenborn
	 */
public class UltrasonicSensorTest {

	    // Velocity of 1 m/s
	    @Test
	    void getCurrentConeHeightTest1() {
	    	
	    	UltrasonicSensor sensor = new UltrasonicSensor(1,1);

	    	//setup to get  OriginToPositionLength = 1
	    	sensor.setSensorAngle(45);
	    	sensor.setSize(1);
	    	
	    	sensor.setRange(4f);
	    	float traveledTime = 10;
	    	float check = sensor.getCurrentConeHeight(traveledTime);
	    	
	    	// after 10 seconds and 1 m/s velocity the distance has to be 10m. subtracting two times 4m results in a current range 
	    	// of 2m. adding the OriginToPositionLength = 1m the result has to be 3 
	    	
	    	assertEquals(check, 3f);
	    	
	    }
	    
	 // Velocity of 2 m/s
	    @Test
	    void getCurrentConeHeightTest2() {
	    	
	    	UltrasonicSensor sensor = new UltrasonicSensor(2,1);

	    	//setup to get  OriginToPositionLength = 1
			sensor.setSensorAngle(45);
	    	sensor.setSize(1);
	    	
	    	sensor.setRange(5f);
	    	float traveledTime = 15;
	    	float check = sensor.getCurrentConeHeight(traveledTime);
	    	
	    	// after 15 seconds and 2 m/s velocity the distance has to be 30m. subtracting 5 times 5m results in a current range 
	    	// of 5m. adding the OriginToPositionLength = 1m the result has to be 6 
	    	
	    	assertEquals(check, 6f);
	    	
	    }
	
}

