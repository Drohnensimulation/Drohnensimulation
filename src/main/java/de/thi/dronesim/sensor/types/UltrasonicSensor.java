package de.thi.dronesim.sensor.types;

import de.thi.dronesim.Simulation;
import de.thi.dronesim.obstacle.entity.HitMark;
import de.thi.dronesim.sensor.dto.SensorResultDto;

import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

public class UltrasonicSensor extends DistanceSensor {
	
	/**
	 * To simulate a ultrasonic sensor the range get increased with a certain velocity up to the given max range.
	 * 
	 * @author Moris Breitenborn
	 */

	public float rangeIncreaseVelocity; // meters per second
	public float startIncreaseTime;
	public float endIncreaseTime;
	public SensorResultDto sensorResultDtovalues;
	//Main simulation
	private Simulation simulation;
	
	/**
	 * Constructor:
	 * 
	 * @param rangeIncreaseVelocity: defines how fast the sensor increase in one second
	 * @param startIncreaseTime: defines the time when the Sensor starts to increase. if the value is 5 the increase starts
	 * after the Simulation is running for 5 seconds. 
	 */
	public UltrasonicSensor(float rangeIncreaseVelocity, int startIncreaseTime) {
		this.rangeIncreaseVelocity =rangeIncreaseVelocity;
		this.startIncreaseTime= startIncreaseTime;
	}
	
	@Override
	public String getType() {
		String name = "UltrasonicSensor";
		return name;
	}


	/**
	 *  This Method is resetting the startIncreaseTime to calculate the next measurement
	 */
	public void startIncrease() {
		this.startIncreaseTime = simulation.getTime();
	}
	
	/**
	 *  This Method calculate the time between this.startIncreaseTime and endIncreaseTime.
	 *  If this.startIncreaseTime is smaller than endIncreaseTime the increase did not start yet. There for the traveled 
	 *  time is 0. The return value got converted to seconds.
	 */
	private float traveledTime() {
		float endIncreaseTime = simulation.getTime();
		float traveledTime;
		if(this.startIncreaseTime<endIncreaseTime) {
			traveledTime = (endIncreaseTime - this.startIncreaseTime) / 1000;
			startIncrease();
		}else {
			traveledTime = 0;
		}
		return traveledTime;
	}
	
	/**
	 * Calculate the current cone height with help the time difference and the rangeIncreaseVelocity. first calculate the current
	 * range and add the getOriginToPositionLength()
	 * 
	 * @param traveledTime
	 */
	public float getCurrentConeHeight(float traveledTime) {
		// Multiply the Time with the velocity to get the distance
		float travaledDistance = traveledTime*this.rangeIncreaseVelocity;
		// if the travaledDistance is bigger then the actual Range subtract the range unless the travaledDistance <= Range.
		// the resulting length equals the current range
		while(travaledDistance>getRange()) {
			travaledDistance -= getRange();
		}
		// return the cone height by adding the OriginToPositionLength to travaledDistance;
		return (float) travaledDistance+ calcOriginToPositionLength();
	}
	
	/**
	 * Calculate the current cone height with help the time difference and the rangeIncreaseVelocity an passes these parameters to
	 * getSensorResult(); The result get saved into this.sensorResultDtovalues 
	 * 
	 * @param traveledTime
	 */
	@Override
	public void runMeasurement() {
		float traveledTime =  traveledTime();
		this.sensorResultDtovalues = getSensorResult(calcOrigin(), getDirectionVector(), getCurrentConeHeight(traveledTime), calcSurfaceVector()); // getCurrentConeHeight()
	}

	@Override
	public SensorResultDto getLastMeasurement() {
		return this.sensorResultDtovalues;
	}
}