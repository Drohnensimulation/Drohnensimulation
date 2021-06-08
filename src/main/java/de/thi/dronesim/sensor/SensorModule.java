package de.thi.dronesim.sensor;

import de.thi.dronesim.ISimulationChild;
import de.thi.dronesim.Simulation;
import de.thi.dronesim.SimulationUpdateEvent;
import de.thi.dronesim.persistence.entity.SensorConfig;
import de.thi.dronesim.sensor.dto.SensorResultDto;
import de.thi.dronesim.sensor.types.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Class to manage all sensors for the Simulation.
 *
 * @author Daniel Stolle
 */
public class SensorModule implements ISimulationChild {

    // /////////////////////////////////////////////////////////////////////////////
    // Fields
    // /////////////////////////////////////////////////////////////////////////////

    private Simulation simulation;
    private final Map<Integer, ISensor> sensorMap = new HashMap<>();

    // /////////////////////////////////////////////////////////////////////////////
    // Methods
    // /////////////////////////////////////////////////////////////////////////////

    /**
     * Calculates the current values for all sensors.
     */
    public void runAllMeasurements(SimulationUpdateEvent event) {
        sensorMap.values().forEach(sensor -> sensor.runMeasurement(event, this));
    }

    /**
     * Gets the result from the last calculation of a sensor.
     *
     * @param sensorId the ID of the Sensor
     * @return the {@link SensorResultDto} of the last calculation
     */
    public SensorResultDto getResultFromSensor(Integer sensorId) {
        ISensor sensor = sensorMap.get(sensorId);
        return sensor == null ? null : sensor.getLastMeasurement();
    }

    /**
     * Gets the result from the last calculation of every sensor.
     *
     * @return a List of all results
     */
    public List<SensorResultDto> getResultsFromAllSensors() {
        return sensorMap.values().stream()
                .map(ISensor::getLastMeasurement)
                .collect(Collectors.toList());
    }

    /**
     * Initialize the sensor map with the config from the simulation
     */
    public void loadConfig(List<SensorConfig> sensorConfigList) {
        sensorMap.clear();
        if (sensorConfigList == null) {
            return;
        }
        for (SensorConfig config : sensorConfigList) {
            if (config.getClassName() == null) {
                throw new IllegalArgumentException("Missing className");
            }
            ISensor res = sensorMap.put(config.getSensorId(), createSensor(config));
            if (res != null) {
                throw new IllegalArgumentException("Duplicate sensor ID");
            }
        }
    }

    /**
     * Creates a sensor from the config object
     *
     * @param config config for the sensor
     * @return the created sensor
     */
    ISensor createSensor(SensorConfig config) {
        switch (config.getClassName()) {
            case "GpsSensor":
                return new GpsSensor(config);
            case "InfraredSensor":
                return new InfraredSensor(config);
            case "RotationSensor":
                return new RotationSensor(config);
            case "UltrasonicSensor":
                return new UltrasonicSensor(config);
            case "WindSensor":
                return new WindSensor(config);
            default:
                throw new IllegalArgumentException("No sensor implementation available for value: " + config.getClassName());
        }
    }

    // /////////////////////////////////////////////////////////////////////////////
    // ISimulationChild
    // /////////////////////////////////////////////////////////////////////////////

    @Override
    public void initialize(Simulation simulation) {
        this.simulation = simulation;
        simulation.registerUpdateListener(this::runAllMeasurements);
    }

    @Override
    public Simulation getSimulation() {
        return simulation;
    }
}
