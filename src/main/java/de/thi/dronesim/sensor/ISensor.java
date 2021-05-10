package de.thi.dronesim.sensor;

import de.thi.dronesim.persistence.entity.SensorConfig;

public interface ISensor {

	public abstract String getType();
	public abstract String getName();
	
	public void loadFromConfig(SensorConfig config);	
	public SensorConfig saveToConfig();

	public void runMeasurement();
	public SensorResultDto getLastMeasurement();
}
