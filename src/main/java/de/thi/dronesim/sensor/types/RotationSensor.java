package de.thi.dronesim.sensor.types;

import de.thi.dronesim.ISimulationChild;
import de.thi.dronesim.Simulation;
import de.thi.dronesim.obstacle.entity.HitMark;
import de.thi.dronesim.sensor.ASensor;
import de.thi.dronesim.obstacle.UfoObjs;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

//import com.jme3.math.Vector3f;


public class RotationSensor extends ASensor implements ISimulationChild {
	/**
	 * Man kan sich die Rotation wie die Bewegung wie die Bewegung eines Blaulichts vorstellen
	 * 
	 */
	
	public double rotationVelocity; // Winkelgeschwindigkeit 2Pi/2s == Eine Umdrehung in 2s Sekunden
	public int callTimerForSensorValues;
	public Set<HitMark> values;
	public Timer callTimerValues = new Timer( );
	public Timer repositoinTimer = new Timer( );
	public long startRotationTime;
	public long endRotationTime;

	//Main simulation
	private Simulation simulation;
	
	public RotationSensor(double rotationVelcity, int callTimerForSensorValues) {
		this.rotationVelocity = rotationVelcity;
		this.callTimerForSensorValues = callTimerForSensorValues;
	}

	public RotationSensor() {

	}
	
	@Override
	
	public String getType() {
		// TODO Auto-generated method stub
		String name = "RotationSensor";
		return name;
	}
	
	public void startRotation() {
		startRotationTime = System.currentTimeMillis();
	}
	
	public void getAktualSensorPosition() {
		//calculate traveled time
		endRotationTime = System.currentTimeMillis();
		long travaledTime = endRotationTime - startRotationTime;
		//restart rotation
		startRotation();
		//Bogenmaß
		double arcMeasure = travaledTime*rotationVelocity;
		double degree = (180/Math.PI)*arcMeasure;
		while(degree > 360.0) {
			degree -= 360;
		}
		
		
		//setPosition(x, y, z);
	}
	
	public void callSensorValues() {
		
		UfoObjs cone = new UfoObjs();
		callTimerValues.scheduleAtFixedRate(new TimerTask() {

		    @Override
		    public void run() {
		    	getAktualSensorPosition();
		    	//values =  cone.pruefeSensorCone(origin, getOrientation(), range, getVectorAngel());
		    }
		}, 0, callTimerForSensorValues);
	}
	
	public void stopCallingSensorValues() {
		callTimerValues.cancel();
	}


	@Override
	public void setSimulation(Simulation simulation) {
		this.simulation = simulation;
	}

	@Override
	public Simulation getSimulation() {
		return this.simulation;
	}
	
	

	
}
