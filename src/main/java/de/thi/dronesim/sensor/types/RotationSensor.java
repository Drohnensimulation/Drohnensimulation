package de.thi.dronesim.sensor.types;

import com.jme3.math.Matrix3f;
import com.jme3.math.Vector3f;
import de.thi.dronesim.Simulation;
import de.thi.dronesim.obstacle.entity.HitMark;
import de.thi.dronesim.sensor.dto.SensorResultDto;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;


public class RotationSensor extends DistanceSensor {
	/**
	 * Imagine the rotation like a police blue light. The sensor rotates around the Y-axis
	 *
	 * Because of the cast from double into float at the Math. function there is always an
	 * inaccuracy of 0.00000x at the x and z values of the newOrientation Vector.
	 * Because of this inaccuracy it can happen that the vector gets a huge x and z difference after a lot of spins.
	 * Consequently the value types got changed into double
	 * 
	 * @author Moris Breitenborn
	 */
	
	public int spinsPerSecond;
	public float rotationVelocity; // 2Pi/s == one spin in one second as radiant
	public float startRotationTime;
	public float endRotationTime;
	public SensorResultDto sensorResultDtoValues;

	//Main simulation
	private Simulation simulation;

	/**
	 * Constructor:
	 * 
	 * @param spinsPerSecond: defines how often the sensor circulate in one second
	 * @param startMeasureTime: defines the time when the Sensor starts to spin. if the value is 5 the rotation starts
	 * after the Simulation is running for 5 seconds. 
	 */
	public RotationSensor(int spinsPerSecond, float startRotationTime) {
		this.spinsPerSecond = spinsPerSecond;
		this.startRotationTime = startRotationTime;
		spinsToRotationVelocityConverter(spinsPerSecond);
	}

	// Why did some one put this Constructor here???????
	public RotationSensor() {
	}

	@Override
	
	public String getType() {
		// TODO Auto-generated method stub
		String name = "RotationSensor";
		return name;
	}
	/**
	 *  This Method converts spins per seconds into radiant. with this value it is possible to calculate the traveled distance
	 */
	public void spinsToRotationVelocityConverter(int spinsPerSecond) {
		this.rotationVelocity = (float) ((2*Math.PI)*this.spinsPerSecond);
	}

	/**
	 *  This Method is resetting the startRotationTime to calculate the next measurement
	 */
	public void startRotation() {
		this.startRotationTime = simulation.getTime();
	}
	
	/**
	 *  This Method calculate the time between this.startRotationTime and endRotationTime.
	 *  If this.startRotationTime is smaller than endRotationTime the rotation did not start yet. There for the traveled 
	 *  time is 0. The return value got converted to seconds.
	 */
	private float traveledTime() {
		endRotationTime = simulation.getTime();
		float traveledTime;
		if(this.startRotationTime<endRotationTime) {
			traveledTime = (endRotationTime - this.startRotationTime) / 1000;
			startRotation();
		}else {
			traveledTime = 0;
		}
		return traveledTime;
	}
	
	/**
	 *  Calculates the arcMeasure by multiply the traveledTime and the given rotationVelocity.
	 *  @param traveledTime.
	 */
	public float getTraveledArcMeasure(float traveledTime) {
		return traveledTime*rotationVelocity;
	}
	
	/**
	 *  Calculate new OrientationVector by rotating the orientation vector around the
	 *  y-axis by the traveledArc value.
	 *  
	 *  @param traveledArc.
	 */
	public Vector3f newOrientation(double traveledArc) {
		
		Vector3f orienataion = getDirectionVector();
		//to reduce inaccuracy we convert the traveledArc <= 1 Rotation
		double oneRotation = (2*Math.PI);
		while (traveledArc > oneRotation) {
			traveledArc = traveledArc - oneRotation;
		}
		// Create a rotation Matrix to rotate around the y-axis 
		double cosPhi=  Math.cos(traveledArc);
		double sinPhi= Math.sin(traveledArc);
		double minSinPhi= (Math.sin(traveledArc)*(-1));
		Matrix3f transformMatrixY = new Matrix3f((float)cosPhi, (float)0, (float)sinPhi, (float)0, (float)1, (float)0, (float)minSinPhi, (float)0, (float)cosPhi);
		// Rotate
		Vector3f newOrientation = transformMatrixY.mult(orienataion);
		
		return newOrientation;	
	}
	
	/**
	 * After resetting the direction vector the current "SensorResultDto" is getting calculated
	 * and set the result to this.sensorResultDtoValues
	 * 
	 */
	@Override
	public void runMeasurement() {
		setDirection(newOrientation(getTraveledArcMeasure(traveledTime())));
		this.sensorResultDtoValues = getSensorResult(calcOrigin(), getDirectionVector(), calcConeHeight(), calcSurfaceVector());
	}

	@Override
	public SensorResultDto getLastMeasurement() {
		return this.sensorResultDtoValues;
	}
}
