
package de.thi.dronesim.sensor.types;

import com.jme3.math.Vector3f;

import de.thi.dronesim.persistence.entity.SensorConfig;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Class to test RotationSensors.
 *
 * @author Moris Breitenborn
 */
public class RotationSensorTest {
	
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
        sensorConfigsA.add(sensorConfigA);
        //Form
        sensorConfigA.setSensorForm("CONE");
        sensorConfigA.setCalcType("AVG");
    }

    /**
     * The traveled arc measure can be tested by giving 
     * a rotation velocity (in this case 1 rotation) and a times that passes ( in this case 4 seconds),
     * There for the result has to be 2PI multiplied by the passed time of 4 seconds. It is not possible
     * to use asserEquals because we always have some time measurement inaccuracy
     * 
     */
    @Test
    void getTraveledArcMeasureTest1() {

    	int spinsPerSeconds = 1; // equals to 2PI
    	int passingTime = 4;
    	sensorConfigA.setSpinsPerSecond(1);
    	
    	RotationSensor rotSen = new RotationSensor(sensorConfigA);
    	// set on one spin per second
    	rotSen.spinsToRotationVelocityConverter(spinsPerSeconds);
    	// let 4 seconds pass

    	double ArcMessuretest = rotSen.getTraveledArcMeasure(passingTime);
    	
    	double low = (float) ((2*Math.PI)*spinsPerSeconds*passingTime);
    	double high = low + 0.3;
    	
    	 assertTrue(low  <= ArcMessuretest, "Error,  ArcMessure is too low");
    	 assertTrue(high >= ArcMessuretest, "Error,  ArcMessure is too high");
    }
    
    @Test
    void getTraveledArcMeasureTest2() {

    	int spinsPerSeconds = 2; // equals to 4PI
    	int passingTime = 3;
    	sensorConfigA.setSpinsPerSecond(2);
    	
    	RotationSensor rotSen = new RotationSensor(sensorConfigA);
    	// set on one spin per second
    	rotSen.spinsToRotationVelocityConverter(spinsPerSeconds);
    	// let 3 seconds pass
    	double ArcMessuretest = rotSen.getTraveledArcMeasure(passingTime);
    	
    	double low = (float) ((2*Math.PI)*spinsPerSeconds*passingTime);
    	double high = low + 0.3;
    	
    	 assertTrue(low  <= ArcMessuretest, "Error,  ArcMessure is too low");
    	 assertTrue(high >= ArcMessuretest, "Error,  ArcMessure is too high");
    }
    
    /**
     * the new origin get calculated by combining the TraveledArcMeasure
     * and the transformation matrix to rotate around the Y-axis.
     * 
     */
    @Test
    void newOrientationTest1() {
    	sensorConfigA.setSpinsPerSecond(1);
    	
    	RotationSensor rotSen = new RotationSensor(sensorConfigA);
    	
    	// one rotation per second equals 2PI
    	double oneRotation = (2*Math.PI)*5;
    	
    	rotSen.setDirection(1, 1, 1);
    	Vector3f origiOrientation = rotSen.getDirectionVector();
    	
    	Vector3f newOrietntation = rotSen.newOrientation(oneRotation);
    	
    	// after one rotation it has to be the same Vector
    	assertEquals(origiOrientation, newOrietntation);
    }
    
    @Test
    void newOrientationTest2() {
    	sensorConfigA.setSpinsPerSecond(1);
    	RotationSensor rotSen = new RotationSensor(sensorConfigA);
    	
    	// one rotation per second equals 2PI
    	double halfRotation = (1*Math.PI);
    	
    	rotSen.setDirection(1, 1, 1);
    	Vector3f newOrietntation = rotSen.newOrientation(halfRotation);
    	
    	//expected vector after a half rotation
    	Vector3f expectedOrientation = new Vector3f(-1,1,-1);
    	
    	// after one rotation it has to be the same Vector
    	assertEquals(expectedOrientation, newOrietntation);
    }
}
