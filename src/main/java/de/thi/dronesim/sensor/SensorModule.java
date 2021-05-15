package de.thi.dronesim.sensor;

import de.thi.dronesim.ISimulationChild;
import de.thi.dronesim.Simulation;
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
    private final Map<String, ISensor> sensorMap = new HashMap<>();

    // /////////////////////////////////////////////////////////////////////////////
    // Methods
    // /////////////////////////////////////////////////////////////////////////////

    /**
     * Calculates the current values for all sensors.
     */
    public void runAllMeasurements() {
        sensorMap.values().forEach(ISensor::runMeasurement);
    }

    /**
     * Gets the result from the last calculation of a sensor.
     *
     * @param sensorName the name of the Sensor
     * @return the {@link SensorResultDto} of the last calculation
     */
    public SensorResultDto getResultFromSensor(String sensorName) {
        ISensor sensor = sensorMap.get(sensorName);
        return sensor == null ? null : sensor.getLastMeasurement();
    }

    /**
     * Gets the result from the last calculation of every sensor.
     *
     * @return a List of all results
     */
    public List<SensorResultDto> getResultsFromAllSensors() {
        return sensorMap.values().stream().map(ISensor::getLastMeasurement).collect(Collectors.toList());
    }

    /**
     * Initialize the sensor map with the config from the simulation
     */
    private void init() {
        sensorMap.clear();
        for (SensorConfig config : simulation.getConfig().getSensorConfigList()) {
            if (config.getClassName() == null) {
                throw new IllegalStateException("Missing className");
            }
            if (config.getSensorName() == null) {
                throw new IllegalStateException("Missing sensorName");
            }
            sensorMap.put(config.getSensorName(), createSensor(config));
        }
    }

    /**
     * Creates a sensor from the config object
     *
     * @param config config for the sensor
     * @return the created sensor
     */
    private ISensor createSensor(SensorConfig config) {
        switch (config.getClassName()) {
            case "GpsSensor":
                return new GpsSensor(config.getSensorName());
            case "InfraredSensor":
                return new InfraredSensor();
            case "RotationSensor":
                return new RotationSensor();
            case "UltrasonicSensor":
                return new UltrasonicSensor(config.getRangeIncreaseVelocity(),
                        config.getCallTimerForSensorValues());
            case "WindSensor":
                return new WindSensor(config.getSensorName(),
                        new Vector3d(config.getDirectionX(), config.getDirectionY(), config.getDirectionZ()),
                        new Vector3d(config.getZeroDegreeDirectionX(), config.getZeroDegreeDirectionY(), config.getZeroDegreeDirectionZ()),
                        new Vector3d(config.getNintyDegreeDirectionX(), config.getNintyDegreeDirectionY(), config.getNintyDegreeDirectionZ()));
            default:
                throw new IllegalStateException("No sensor implementation available for value: " + config.getClassName());
        }
    }

    // /////////////////////////////////////////////////////////////////////////////
    // ISimulationChild
    // /////////////////////////////////////////////////////////////////////////////

    @Override
    public void setSimulation(Simulation simulation) {
        this.simulation = simulation;
        init();
    }

    @Override
    public Simulation getSimulation() {
        return simulation;
    }
}
