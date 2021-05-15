package de.thi.dronesim.sensor;

import de.thi.dronesim.persistence.entity.SensorConfig;
import de.thi.dronesim.sensor.dto.SensorResultDto;

/**
 * Interface for a sensor
 *
 * @author Dominik Bartl
 */
public interface ISensor {

	String getType();

	String getName();

	SensorConfig saveToConfig();

	void runMeasurement();

	SensorResultDto getLastMeasurement();
}
