package de.thi.dronesim.sensor.types;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.junit.jupiter.api.Test;

import com.jme3.math.Vector3f;

import de.thi.dronesim.SimulationUpdateEvent;
import de.thi.dronesim.drone.Drone;
import de.thi.dronesim.drone.Location;
import de.thi.dronesim.drone.Vector3;
import de.thi.dronesim.persistence.entity.SensorConfig;

public class WindSensorTest {

	private int maxFractionDigits = 6;

	private double roundValue(float value, int places) {
	    BigDecimal bd = BigDecimal.valueOf(value);
	    bd = bd.setScale(places, RoundingMode.HALF_UP);
	    return bd.doubleValue();
	}
	
	private Vector3 roundVector(Vector3f vec, int places) {
		return new Vector3(roundValue(vec.x, places), roundValue(vec.y, places), roundValue(vec.z, places));
	}
	
	/**
	 * Checks wether the absolute sensor direction is calculated correctly.
	 */
	@Test
	public void rotationTest1() {
		SensorConfig conf = new SensorConfig();
		conf.setDirectionX(1f);
		conf.setDirectionY(0f);
		conf.setDirectionZ(0f);
		conf.setZeroDegreeDirectionX(0f);
		conf.setZeroDegreeDirectionY(1f);
		conf.setZeroDegreeDirectionZ(0f);
		conf.setNintyDegreeDirectionX(0f);
		conf.setNintyDegreeDirectionY(0f);
		conf.setNintyDegreeDirectionZ(-1f);
		
		Drone drone = new Drone();
		Location location = drone.getLocation();
		location.setHeading(0);
		SimulationUpdateEvent event = new SimulationUpdateEvent(drone, 0, 0);
		
		WindSensor sensor = new WindSensor(conf);
		sensor.runMeasurement(event);
		
		assertEquals(new Vector3(0,0,1), roundVector(sensor.getLastAbsSensorDirection(), maxFractionDigits));
	}
	
	/**
	 * Checks wether the absolute sensor direction is calculated correctly.
	 */
	@Test
	public void rotationTest2() {
		SensorConfig conf = new SensorConfig();
		conf.setDirectionX(1f);
		conf.setDirectionY(0f);
		conf.setDirectionZ(0f);
		conf.setZeroDegreeDirectionX(0f);
		conf.setZeroDegreeDirectionY(1f);
		conf.setZeroDegreeDirectionZ(0f);
		conf.setNintyDegreeDirectionX(0f);
		conf.setNintyDegreeDirectionY(0f);
		conf.setNintyDegreeDirectionZ(-1f);
		
		Drone drone = new Drone();
		Location location = drone.getLocation();
		location.setHeading(270);
		SimulationUpdateEvent event = new SimulationUpdateEvent(drone, 0, 0);
		
		WindSensor sensor = new WindSensor(conf);
		sensor.runMeasurement(event);
		
		assertEquals(new Vector3(1,0,0), roundVector(sensor.getLastAbsSensorDirection(), maxFractionDigits));
	}
	
	/**
	 * Checks wether the absolute sensor direction is calculated correctly.
	 */
	@Test
	public void rotationTest3() {
		SensorConfig conf = new SensorConfig();
		conf.setDirectionX(0f);
		conf.setDirectionY(-1f);
		conf.setDirectionZ(0f);
		conf.setZeroDegreeDirectionX(1f);
		conf.setZeroDegreeDirectionY(0f);
		conf.setZeroDegreeDirectionZ(0f);
		conf.setNintyDegreeDirectionX(0f);
		conf.setNintyDegreeDirectionY(0f);
		conf.setNintyDegreeDirectionZ(1f);
		
		Drone drone = new Drone();
		Location location = drone.getLocation();
		location.setHeading(265);
		SimulationUpdateEvent event = new SimulationUpdateEvent(drone, 0, 0);
		
		WindSensor sensor = new WindSensor(conf);
		sensor.runMeasurement(event);
		
		assertEquals(new Vector3(0,-1,0), roundVector(sensor.getLastAbsSensorDirection(), maxFractionDigits));
	}
	
	/**
	 * Checks wether the absolute sensor direction is calculated correctly.
	 */
	@Test
	public void rotationTest4() {
		SensorConfig conf = new SensorConfig();
		conf.setDirectionX(-1f);
		conf.setDirectionY(-1f);
		conf.setDirectionZ(0f);
		conf.setZeroDegreeDirectionX(-1f);
		conf.setZeroDegreeDirectionY(1f);
		conf.setZeroDegreeDirectionZ(0f);
		conf.setNintyDegreeDirectionX(0f);
		conf.setNintyDegreeDirectionY(0f);
		conf.setNintyDegreeDirectionZ(1f);
		
		Drone drone = new Drone();
		Location location = drone.getLocation();
		location.setHeading(225);
		SimulationUpdateEvent event = new SimulationUpdateEvent(drone, 0, 0);
		
		WindSensor sensor = new WindSensor(conf);
		sensor.runMeasurement(event);
		
		assertEquals(new Vector3(roundValue((float)(-Math.sqrt(2)*0.5), maxFractionDigits),-1,roundValue((float)(Math.sqrt(2)*0.5), maxFractionDigits)), roundVector(sensor.getLastAbsSensorDirection(), maxFractionDigits));
	}
	
	/**
	 * Checks wether the absolute sensor direction is calculated correctly.
	 */
	@Test
	public void rotationTest5() {
		SensorConfig conf = new SensorConfig();
		conf.setDirectionX(-1f);
		conf.setDirectionY(-1f);
		conf.setDirectionZ(2f);
		conf.setZeroDegreeDirectionX(-1f);
		conf.setZeroDegreeDirectionY(1f);
		conf.setZeroDegreeDirectionZ(0f);
		conf.setNintyDegreeDirectionX(-2f);
		conf.setNintyDegreeDirectionY(-2f);
		conf.setNintyDegreeDirectionZ(-2f);
		
		Drone drone = new Drone();
		Location location = drone.getLocation();
		location.setHeading(135);
		SimulationUpdateEvent event = new SimulationUpdateEvent(drone, 0, 0);
		
		WindSensor sensor = new WindSensor(conf);
		sensor.runMeasurement(event);
		
		double rotFromDefaultPos = Math.toRadians(135);
		assertEquals(new Vector3(
				roundValue((float)(Math.cos(rotFromDefaultPos)*conf.getDirectionX()+Math.sin(rotFromDefaultPos)*conf.getDirectionZ()), maxFractionDigits),
				conf.getDirectionY(),
				roundValue((float)(-Math.sin(rotFromDefaultPos)*conf.getDirectionX()+Math.cos(rotFromDefaultPos)*conf.getDirectionZ()), maxFractionDigits)), 
		roundVector(sensor.getLastAbsSensorDirection(), maxFractionDigits));
	}
	
	/**
	 * Checks if the wind is calculated correctly if there is just the "flight wind" and no additional wind
	 */
	@Test
	public void calmWindTest1() {
		SensorConfig conf = new SensorConfig();
		conf.setDirectionX(1f);
		conf.setDirectionY(0f);
		conf.setDirectionZ(0f);
		conf.setZeroDegreeDirectionX(0f);
		conf.setZeroDegreeDirectionY(1f);
		conf.setZeroDegreeDirectionZ(0f);
		conf.setNintyDegreeDirectionX(0f);
		conf.setNintyDegreeDirectionY(0f);
		conf.setNintyDegreeDirectionZ(-1f);
		
		Drone drone = new Drone();
		Location location = drone.getLocation();
		location.setHeading(0);
		SimulationUpdateEvent event = new SimulationUpdateEvent(drone, 0, 0);
		
		WindSensor sensor = new WindSensor(conf);
		sensor.runMeasurement(event);
		
		assertEquals(Float.NaN, sensor.getLastMeasurement().getValues().get(0));
		assertEquals(0f, sensor.getLastMeasurement().getValues().get(1));
	}
	
	/**
	 * Checks if the wind is calculated correctly if there is just the "flight wind" and no additional wind
	 */
	@Test
	public void calmWindTest2() {
		SensorConfig conf = new SensorConfig();
		conf.setDirectionX(0f);
		conf.setDirectionY(-1f);
		conf.setDirectionZ(0f);
		conf.setZeroDegreeDirectionX(1f);
		conf.setZeroDegreeDirectionY(0f);
		conf.setZeroDegreeDirectionZ(0f);
		conf.setNintyDegreeDirectionX(0f);
		conf.setNintyDegreeDirectionY(0f);
		conf.setNintyDegreeDirectionZ(1f);
		
		Drone drone = new Drone();
		Location location = drone.getLocation();
		location.setHeading(270);
		location.setTrack(270);
		location.setGroundSpeed(10);
		location.setAirspeed(10);
		location.setVerticalSpeed(0);
		location.updatePosition(60);
		SimulationUpdateEvent event = new SimulationUpdateEvent(drone, 0, 0);
		
		WindSensor sensor = new WindSensor(conf);
		sensor.runMeasurement(event);
		
		assertEquals(180, sensor.getLastMeasurement().getValues().get(0));
		assertEquals(10, sensor.getLastMeasurement().getValues().get(1));
	}
	
	/**
	 * Checks if the wind is calculated correctly if there is just the "flight wind" and no additional wind
	 */
	@Test
	public void calmWindTest3() {
		SensorConfig conf = new SensorConfig();
		conf.setDirectionX(0f);
		conf.setDirectionY(-1f);
		conf.setDirectionZ(0f);
		conf.setZeroDegreeDirectionX(1f);
		conf.setZeroDegreeDirectionY(0f);
		conf.setZeroDegreeDirectionZ(0f);
		conf.setNintyDegreeDirectionX(0f);
		conf.setNintyDegreeDirectionY(0f);
		conf.setNintyDegreeDirectionZ(1f);
		
		Drone drone = new Drone();
		Location location = drone.getLocation();
		location.setHeading(270);
		location.setTrack(180);
		location.setGroundSpeed(10);
		location.setAirspeed(10);
		location.updatePosition(60);
		SimulationUpdateEvent event = new SimulationUpdateEvent(drone, 0, 0);
		
		WindSensor sensor = new WindSensor(conf);
		sensor.runMeasurement(event);
		
		assertEquals(90, sensor.getLastMeasurement().getValues().get(0));
		assertEquals(10, sensor.getLastMeasurement().getValues().get(1));
	}
	
	/**
	 * Checks if the wind is calculated correctly if there is just the "flight wind" and no additional wind
	 */
	@Test
	public void calmWindTest4() {
		SensorConfig conf = new SensorConfig();
		conf.setDirectionX(1f);
		conf.setDirectionY(1f);
		conf.setDirectionZ(0f);
		conf.setZeroDegreeDirectionX(1f);
		conf.setZeroDegreeDirectionY(-1f);
		conf.setZeroDegreeDirectionZ(0f);
		conf.setNintyDegreeDirectionX(0f);
		conf.setNintyDegreeDirectionY(0f);
		conf.setNintyDegreeDirectionZ(-1f);
		
		Drone drone = new Drone();
		Location location = drone.getLocation();
		location.setHeading(270);
		location.setTrack(270);
		location.setGroundSpeed(10);
		location.setAirspeed(10);
		location.updatePosition(60);
		SimulationUpdateEvent event = new SimulationUpdateEvent(drone, 0, 0);
		
		WindSensor sensor = new WindSensor(conf);
		sensor.runMeasurement(event);
		
		assertEquals(180, sensor.getLastMeasurement().getValues().get(0));
		assertEquals(roundValue((float)(Math.sqrt(2)*10), maxFractionDigits), roundValue((float)(sensor.getLastMeasurement().getValues().get(1)), maxFractionDigits));
	}
	
	/**
	 * Checks if the wind is calculated correctly if there is just the "flight wind" and no additional wind
	 */
	@Test
	public void calmWindTest5() {
		SensorConfig conf = new SensorConfig();
		conf.setDirectionX(1f);
		conf.setDirectionY(0f);
		conf.setDirectionZ(0f);
		conf.setZeroDegreeDirectionX(0f);
		conf.setZeroDegreeDirectionY(1f);
		conf.setZeroDegreeDirectionZ(0f);
		conf.setNintyDegreeDirectionX(0f);
		conf.setNintyDegreeDirectionY(0f);
		conf.setNintyDegreeDirectionZ(-1f);
		
		Drone drone = new Drone();
		Location location = drone.getLocation();
		location.setHeading(270);
		location.setTrack(270);
		location.setGroundSpeed(10);
		location.setAirspeed(10);
		location.setVerticalSpeed(50);
		location.updatePosition(60);
		SimulationUpdateEvent event = new SimulationUpdateEvent(drone, 0, 0);
		
		WindSensor sensor = new WindSensor(conf);
		sensor.runMeasurement(event);
		
		assertEquals(180, sensor.getLastMeasurement().getValues().get(0));
		assertEquals(50, sensor.getLastMeasurement().getValues().get(1));
	}
	
	/**
	 * Checks if the wind is calculated correctly if there is just the "flight wind" and no additional wind
	 */
	@Test
	public void calmWindTest6() {
		SensorConfig conf = new SensorConfig();
		conf.setDirectionX(1f);
		conf.setDirectionY(0f);
		conf.setDirectionZ(0f);
		conf.setZeroDegreeDirectionX(0f);
		conf.setZeroDegreeDirectionY(1f);
		conf.setZeroDegreeDirectionZ(0f);
		conf.setNintyDegreeDirectionX(0f);
		conf.setNintyDegreeDirectionY(0f);
		conf.setNintyDegreeDirectionZ(-1f);
		
		Drone drone = new Drone();
		Location location = drone.getLocation();
		location.setHeading(270);
		location.setTrack(270);
		location.setGroundSpeed(10);
		location.setAirspeed(10);
		location.setVerticalSpeed(-50);
		location.updatePosition(60);
		SimulationUpdateEvent event = new SimulationUpdateEvent(drone, 0, 0);
		
		WindSensor sensor = new WindSensor(conf);
		sensor.runMeasurement(event);
		
		assertEquals(0, sensor.getLastMeasurement().getValues().get(0));
		assertEquals(50, sensor.getLastMeasurement().getValues().get(1));
	}
}
