package de.thi.dronesim.sensor.types;

import de.thi.dronesim.Simulation;
import de.thi.dronesim.SimulationUpdateEvent;
import de.thi.dronesim.persistence.entity.SensorConfig;
import de.thi.dronesim.sensor.SensorModule;
import de.thi.dronesim.sensor.dto.SensorResultDto;

public class InfraredSensor extends DistanceSensor {

	public InfraredSensor(SensorConfig config) {
		super(config);
	}

	public InfraredSensor(SensorConfig config, Simulation simulation) {
		super(config,simulation);
	}

	@Override
	public void runMeasurement(SimulationUpdateEvent event, SensorModule sensorModule) {
		updateSensorPositionAndDirection();
		sensorResultDtoValues = getSensorResult(calcOrigin(), getDirectionVector(), calcConeHeight(), calcSurfaceVector(), sensorModule);
	}

	@Override
	public SensorResultDto getLastMeasurement() {
		return sensorResultDtoValues;
	}

	@Override
	public SensorConfig saveToConfig() {
		SensorConfig config = super.saveToConfig();
		config.setClassName(this.getClass().getSimpleName());
		return config;
	}
}
