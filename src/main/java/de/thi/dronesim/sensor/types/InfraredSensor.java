package de.thi.dronesim.sensor.types;

import de.thi.dronesim.sensor.dto.SensorResultDto;

public class InfraredSensor extends ASensor {

	public InfraredSensor() {

	}

	@Override
	public String getType() {
		String name = "InfrarotSensor";
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
}
