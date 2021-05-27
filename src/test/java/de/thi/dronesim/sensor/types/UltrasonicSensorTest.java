package de.thi.dronesim.sensor.types;

import org.junit.jupiter.api.Test;

import de.thi.dronesim.persistence.entity.SensorConfig;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;

/**
	 * Class to test UltrasonicSensors.
	 *
	 * @author Moris Breitenborn
	 */
public class UltrasonicSensorTest {

	//All values
	SensorConfig sensorConfigA = new SensorConfig();
    @BeforeEach
    public void init() {
    	//All
    	List<SensorConfig> sensorConfigsA = new ArrayList<>();
    	sensorConfigA.setRange(10);
    	sensorConfigA.setSensorAngle(11);
    	sensorConfigA.setSensorRadius(13);
        sensorConfigA.setMeasurementAccuracy(14);
        sensorConfigA.setDirectionX(15);
        sensorConfigA.setDirectionY(16);
        sensorConfigA.setDirectionZ(17);
        sensorConfigA.setPosX(18);
        sensorConfigA.setPosY(19);
        sensorConfigA.setPosZ(20);
        //UltrasonicSensor
        sensorConfigA.setRangeIncreaseVelocity(4);
        sensorConfigA.setStartIncreaseTime(4);
        //RotationSensor
        sensorConfigA.setSpinsPerSecond(1);
        sensorConfigA.setStartRotationTime(4);
        //Form
        sensorConfigA.setSensorForm("CONE");
        sensorConfigA.setCalcType("AVG");
    }
	
	    // Velocity of 1 m/s
	    @Test
	    void getCurrentConeHeightTest1() {
	    	sensorConfigA.setRangeIncreaseVelocity(1);
	        sensorConfigA.setStartIncreaseTime(1);
	    	UltrasonicSensor sensor = new UltrasonicSensor(sensorConfigA);

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
	    	sensorConfigA.setRangeIncreaseVelocity(2);
	        sensorConfigA.setStartIncreaseTime(1);
	    	UltrasonicSensor sensor = new UltrasonicSensor(sensorConfigA);

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

