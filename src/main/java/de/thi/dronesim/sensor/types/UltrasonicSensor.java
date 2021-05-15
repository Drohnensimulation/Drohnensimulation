package de.thi.dronesim.sensor.types;

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
	public int callTimerForSensorValues;
	public Timer callTimerValues = new Timer( );
	public Set<HitMark> values;

	public UltrasonicSensor(float rangeIncreaseVelocity, int callTimerForSensorValues) {
		this.rangeIncreaseVelocity =rangeIncreaseVelocity;
		this.callTimerForSensorValues= callTimerForSensorValues;
		this.startIncrease();

	}
	
	@Override
	public String getType() {
		String name = "UltrasonicSensor";
		return name;
	}

	@Override
	public void runMeasurement() {
		// TODO
	}

	@Override
	public SensorResultDto getLastMeasurement() {
		// TODO
		return null;
	}

	// Saves the start time
	public void startIncrease() {
		this.startIncreaseTime = System.nanoTime();
	}
	
	//calculate traveled time in nanoseconds
	private float traveledTime() {
		float endIncreaseTime = System.nanoTime();
		// traveled time converted into seconds
		float traveledTime = (endIncreaseTime - this.startIncreaseTime) / 1000000000;
		return traveledTime;
	}
	
	// Calculate the current cone height with help the time difference and the rangeIncreaseVelocity. first calculate the current
	// range and add the getOriginToPositionLength()
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
	
	public void startCallSensorValues() {
		
		callTimerValues.scheduleAtFixedRate(new TimerTask() {

		    @Override
		    public void run() {
		    	float traveledTime =  traveledTime();
				values =  getSensorHits(calcOrigin(), getDirectionVector(), getCurrentConeHeight(traveledTime), calcSurfaceVector()); // getCurrentConeHeight()
		    }

		}, 0, callTimerForSensorValues);
	}
	
	public void stopCallingSensorValues() {
		callTimerValues.cancel();
	}
}
