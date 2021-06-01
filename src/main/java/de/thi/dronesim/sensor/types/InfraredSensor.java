package de.thi.dronesim.sensor.types;

import de.thi.dronesim.SimulationUpdateEvent;
import de.thi.dronesim.persistence.entity.SensorConfig;
import de.thi.dronesim.sensor.dto.SensorResultDto;

public class InfraredSensor extends DistanceSensor {

	public InfraredSensor(SensorConfig config) {
		super(config);
	}

	@Override
	public String getType() {
		String name = "InfrarotSensor";
		return name;
		
	}

	@Override
	public void runMeasurement(SimulationUpdateEvent event) {
		sensorResultDtoValues = getSensorResult(calcOrigin(), getDirectionVector(), calcConeHeight(), calcSurfaceVector());
	}

	@Override
	public SensorResultDto getLastMeasurement() {
		return sensorResultDtoValues;
	}
}
