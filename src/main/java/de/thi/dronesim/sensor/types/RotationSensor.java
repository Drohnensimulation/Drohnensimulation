package de.thi.dronesim.sensor.types;

import de.thi.dronesim.ISimulationChild;
import de.thi.dronesim.Simulation;
import de.thi.dronesim.obstacle.entity.HitMark;
import de.thi.dronesim.sensor.ASensor;
import de.thi.dronesim.obstacle.UfoObjs;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import com.jme3.math.Matrix3f;
import com.jme3.math.Vector3f;

//import com.jme3.math.Vector3f;


public class RotationSensor extends ASensor implements ISimulationChild {
	/**
	 * Man kan sich die Rotation wie die Bewegung wie die Bewegung eines Blaulichts vorstellen
	 * 
	 * @author Moris Breitenborn
	 */
	
	public float rotationVelocity; // Winkelgeschwindigkeit 2Pi/2s == Eine Umdrehung in 2s Sekunden
	public int callTimerForSensorValues;
	public Set<HitMark> values;
	public Timer callTimerValues = new Timer( );
	public Timer repositoinTimer = new Timer( );
	public float startRotationTime;
	public float endRotationTime;

	//Main simulation
	private Simulation simulation;

	public RotationSensor(float rotationVelcity, int callTimerForSensorValues) {
		this.rotationVelocity = rotationVelcity;
		this.callTimerForSensorValues = callTimerForSensorValues;
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
	
	// To calculate the rotation we measure the past time. This method starts the timer. 
	public void startRotation() {
		startRotationTime = System.currentTimeMillis();
	}
	
	public void stopCallingSensorValues() {
		callTimerValues.cancel();
	}
	// Calculates the arcMeasure by multiply the traveledTime and the given rotationVelocity
	public float getTraveledArcMeasure() {
		//calculate traveled time
		endRotationTime = System.currentTimeMillis();
		float travaledTime = endRotationTime - startRotationTime;
		//restart rotation
		startRotation();
		//return arc measure
		return travaledTime*rotationVelocity;
	}
	
	// Calculate new OrientationVector
	public Vector3f newOrientation() {
		
		Vector3f orienataion = getOrientation();
		// get the arc measure 
		float traveledArc = getTraveledArcMeasure();
		// Create a rotation Matrix to rotate around the y-axses 
		float cosPhi= (float) Math.cos(traveledArc);
		float sinPhi= (float) Math.sin(traveledArc);
		float minSinPhi= (float) (Math.sin(traveledArc)*(-1));
		Matrix3f transformMatrixY = new Matrix3f(cosPhi, 0, sinPhi, 0, 1, 0, minSinPhi, 0, cosPhi);
		Vector3f newOrientation = transformMatrixY.mult(orienataion);
		
		return newOrientation;	
	}
	
	
	// This Method returns the values from "checkSensorCone" in a given time "callTimerForSensorValues"
	public void callSensorValues() {
		
		callTimerValues.scheduleAtFixedRate(new TimerTask() {

		    @Override
		    public void run() {
		    	setOrientation(newOrientation());
				values =  getSensorHits(getOrigin(), getOrientation(), getConeHeight(), getVectorAngel());
		    }
		}, 0, callTimerForSensorValues);
	}
	
	
	@Override
	public void initialize(Simulation simulation) {
		this.simulation = simulation;
	}

	@Override
	public Simulation getSimulation() {
		return this.simulation;
	}

	

	
}
