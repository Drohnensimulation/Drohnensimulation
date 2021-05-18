package de.thi.dronesim.sensor.types;

import de.thi.dronesim.sensor.dto.SensorResultDto;

public class InfraredSensor extends DistanceSensor {

	public InfraredSensor() {

	}

	@Override
	public String getType() {
		String name = "InfrarotSensor";
		return name;
		
	}

	@Override
	public void runMeasurement() {
		sensorResultDtoValues = getSensorResult(calcOrigin(), getDirectionVector(), calcConeHeight(), calcSurfaceVector());
	}

	@Override
	public SensorResultDto getLastMeasurement() {
		return sensorResultDtoValues;
	}
}
