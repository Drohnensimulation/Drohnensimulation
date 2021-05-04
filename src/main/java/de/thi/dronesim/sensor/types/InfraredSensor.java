package de.thi.dronesim.sensor.types;

import de.thi.dronesim.ISimulationChild;
import de.thi.dronesim.Simulation;
import de.thi.dronesim.sensor.ASensor;

public class InfraredSensor extends ASensor implements ISimulationChild {

	//Main simulation
	private Simulation simulation;

	public InfraredSensor() {

	}
	
	@Override
	public String getType() {
		String name = "InfrarotSensor";
		return name;
		
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
