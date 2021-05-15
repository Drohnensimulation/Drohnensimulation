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
	public int callTimerForSensorValues;
	public Set<HitMark> values;
	public Timer callTimerValues = new Timer( );
	public float startRotationTime;
	public float endRotationTime;

	//Main simulation
	private Simulation simulation;

	public RotationSensor(int spinsPerSecond, int callTimerForSensorValues) {
		this.spinsPerSecond = spinsPerSecond;
		this.callTimerForSensorValues = callTimerForSensorValues;
		spinsToRotationVelocityConverter(spinsPerSecond);
		startRotation();
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
	// converts spins per seconds into radiant. with this value it is possible to calculate the traveled distance
	public void spinsToRotationVelocityConverter(int spinsPerSecond) {
		this.rotationVelocity = (float) ((2*Math.PI)*this.spinsPerSecond);
	}

	// To calculate the rotation we measure the past time in nanoseconds. This method starts the timer.
	public void startRotation() {
		this.startRotationTime = System.nanoTime();
	}
	

	// Calculates the arcMeasure by multiply the traveledTime and the given rotationVelocity
	public double getTraveledArcMeasure() {
		//calculate traveled time in nanoseconds
		endRotationTime = System.nanoTime();
		// traveled time converted into seconds
		float travaledTime = (endRotationTime - this.startRotationTime) / 1000000000;
		//restart rotation
		startRotation();
		//return arc measure
		return travaledTime*rotationVelocity;
	}
	
	// Calculate new OrientationVector with the getTraveledArcMeasure() return as parameter
	public Vector3f newOrientation(double traveledArc) {
		
		Vector3f orienataion = getDirectionVector();

		//to reduce inaccuracy we convert the traveledArc <= 1 Rotation
		double oneRotation = (2*Math.PI);
		while (traveledArc > oneRotation) {
			traveledArc = traveledArc - oneRotation;
		}

		// Create a rotation Matrix to rotate around the y-axses 
		double cosPhi=  Math.cos(traveledArc);
		double sinPhi= Math.sin(traveledArc);
		double minSinPhi= (Math.sin(traveledArc)*(-1));
		Matrix3f transformMatrixY = new Matrix3f((float)cosPhi, (float)0, (float)sinPhi, (float)0, (float)1, (float)0, (float)minSinPhi, (float)0, (float)cosPhi);
		Vector3f newOrientation = transformMatrixY.mult(orienataion);
		
		return newOrientation;	
	}
	
	
	// This Method returns the values from "checkSensorCone" in a given time "callTimerForSensorValues"
	public void startCallSensorValues() {
		
		callTimerValues.scheduleAtFixedRate(new TimerTask() {

		    @Override
		    public void run() {
		    	setDirection(newOrientation(getTraveledArcMeasure()));
				values =  getSensorHits(calcOrigin(), getDirectionVector(), calcConeHeight(), calcSurfaceVector());
		    }
		}, 0, callTimerForSensorValues);
	}

	public void stopCallingSensorValues() {
		callTimerValues.cancel();
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
}
