package de.thi.dronesim.sensor.types;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import de.thi.dronesim.SimulationUpdateEvent;
import de.thi.dronesim.drone.Drone;
import de.thi.dronesim.drone.Location;
import de.thi.dronesim.persistence.entity.SensorConfig;
import de.thi.dronesim.sensor.dto.SensorResultDto;

public class GpsSensorTest {

	/**
	 * This method should have the same implementation as the Method in the GpsSensor
	 */
	private float addNoise(float val, int maxNoise) {
		return (int)(val / maxNoise + (val > 0 ? 0.5 : -0.5)) * (float)maxNoise;
	}
	
	/**
	 * Checks whether the position is delayed correctly
	 */
	@Test
	public void positionTest1() {
		GpsSensor sensor = new GpsSensor(new SensorConfig());
		
		SensorResultDto result = null;
		
		Drone drone = new Drone();
		Location location = drone.getLocation();
		location.setX(10);
		location.setY(0);
		location.setZ(0);
		sensor.runMeasurement(new SimulationUpdateEvent(drone, 0, 0), null);
		
		result = sensor.getLastMeasurement();
		assertEquals(Float.NaN, result.getValues().get(0));
		assertEquals(Float.NaN, result.getValues().get(1));
		assertEquals(Float.NaN, result.getValues().get(2));
		
		sensor.runMeasurement(new SimulationUpdateEvent(drone, (sensor.getDelay()-1), 0), null);
		
		result = sensor.getLastMeasurement();
		assertEquals(Float.NaN, result.getValues().get(0));
		assertEquals(Float.NaN, result.getValues().get(1));
		assertEquals(Float.NaN, result.getValues().get(2));
		
		sensor.runMeasurement(new SimulationUpdateEvent(drone, sensor.getDelay(), 0), null);
		
		result = sensor.getLastMeasurement();
		assertEquals(addNoise(10f, sensor.getHorizontalNoise()), result.getValues().get(0));
		assertEquals(addNoise(0f, sensor.getVerticalNoise()), result.getValues().get(1));
		assertEquals(addNoise(0f,sensor.getHorizontalNoise()), result.getValues().get(2));
		
		location.setX(23);
		location.setY(46);
		location.setZ(-283);
		sensor.runMeasurement(new SimulationUpdateEvent(drone, 2*sensor.getDelay(), 0), null);
		result = sensor.getLastMeasurement();
		
		assertEquals(addNoise(10f, sensor.getHorizontalNoise()), result.getValues().get(0));
		assertEquals(addNoise(0f, sensor.getVerticalNoise()), result.getValues().get(1));
		assertEquals(addNoise(0f,sensor.getHorizontalNoise()), result.getValues().get(2));
		
		sensor.runMeasurement(new SimulationUpdateEvent(drone, 2.5*sensor.getDelay(), 0), null);
		result = sensor.getLastMeasurement();
		
		assertEquals(addNoise(10f, sensor.getHorizontalNoise()), result.getValues().get(0));
		assertEquals(addNoise(0f, sensor.getVerticalNoise()), result.getValues().get(1));
		assertEquals(addNoise(0f,sensor.getHorizontalNoise()), result.getValues().get(2));
		
		sensor.runMeasurement(new SimulationUpdateEvent(drone, 30*sensor.getDelay(), 0), null);
		result = sensor.getLastMeasurement();
		
		assertEquals(addNoise(23f, sensor.getHorizontalNoise()), result.getValues().get(0));
		assertEquals(addNoise(46f, sensor.getVerticalNoise()), result.getValues().get(1));
		assertEquals(addNoise(-283f,sensor.getHorizontalNoise()), result.getValues().get(2));
		
		sensor.runMeasurement(new SimulationUpdateEvent(drone, (30*sensor.getDelay()+1), 0), null);
		result = sensor.getLastMeasurement();
		
		assertEquals(addNoise(23f, sensor.getHorizontalNoise()), result.getValues().get(0));
		assertEquals(addNoise(46f, sensor.getVerticalNoise()), result.getValues().get(1));
		assertEquals(addNoise(-283f,sensor.getHorizontalNoise()), result.getValues().get(2));
	}
	
	/**
	 * Checks whether the horizontal speed is approximated correctly
	 */
	@Test
	public void horizontalSpeedTest1() {
		GpsSensor sensor = new GpsSensor(new SensorConfig());
		
		SensorResultDto result = null;
		
		Drone drone = new Drone();
		Location location = drone.getLocation();
		location.setX(0);
		location.setZ(0);
		sensor.runMeasurement(new SimulationUpdateEvent(drone, 0, 0), null);
		
		result = sensor.getLastMeasurement();
		assertEquals(Float.NaN, result.getValues().get(3));
		
		location.setX(400);
		location.setZ(-300);
		sensor.runMeasurement(new SimulationUpdateEvent(drone, 0.5*sensor.getHSpeedObservedTime(), 0), null);
		
		result = sensor.getLastMeasurement();
		assertEquals(Float.NaN, result.getValues().get(3));
		
		location.setX(400);
		location.setZ(0);
		sensor.runMeasurement(new SimulationUpdateEvent(drone, sensor.getHSpeedObservedTime(), 0), null);
		
		result = sensor.getLastMeasurement();
		assertEquals(Float.NaN, result.getValues().get(3));
		
		sensor.runMeasurement(new SimulationUpdateEvent(drone, 1.5*sensor.getHSpeedObservedTime(), 0), null);
		result = sensor.getLastMeasurement();
		assertEquals(Float.NaN, result.getValues().get(3));
		
		sensor.runMeasurement(new SimulationUpdateEvent(drone, 2*sensor.getHSpeedObservedTime(), 0), null);
		result = sensor.getLastMeasurement();
		assertEquals((float)((int)(800*1000/(double)sensor.getHSpeedObservedTime() + 0.5)), result.getValues().get(3));
	}
	
	/**
	 * Checks whether the vertical speed is approximated correctly
	 */
	@Test
	public void verticalSpeedTest1() {
		GpsSensor sensor = new GpsSensor(new SensorConfig());
		
		SensorResultDto result = null;
		
		Drone drone = new Drone();
		Location location = drone.getLocation();
		location.setY(0);
		sensor.runMeasurement(new SimulationUpdateEvent(drone, 0, 0), null);
		
		result = sensor.getLastMeasurement();
		assertEquals(Float.NaN, result.getValues().get(4));
		
		location.setY(-100);
		sensor.runMeasurement(new SimulationUpdateEvent(drone, 0.5*sensor.getVSpeedObservedTime(), 0), null);
		
		result = sensor.getLastMeasurement();
		assertEquals(Float.NaN, result.getValues().get(4));
		
		location.setY(800);
		sensor.runMeasurement(new SimulationUpdateEvent(drone, sensor.getVSpeedObservedTime(), 0), null);
		
		result = sensor.getLastMeasurement();
		assertEquals(Float.NaN, result.getValues().get(4));
		
		sensor.runMeasurement(new SimulationUpdateEvent(drone, 1.5*sensor.getVSpeedObservedTime(), 0), null);
		result = sensor.getLastMeasurement();
		assertEquals(Float.NaN, result.getValues().get(4));
		
		sensor.runMeasurement(new SimulationUpdateEvent(drone, 2*sensor.getVSpeedObservedTime(), 0), null);
		result = sensor.getLastMeasurement();
		assertEquals((float)((int)(1000*1000/(double)sensor.getVSpeedObservedTime() + 0.5)), result.getValues().get(4));
	}
}
