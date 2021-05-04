package de.thi.dronesim.example;

import de.thi.dronesim.persistence.ConfigWriter;
import de.thi.dronesim.persistence.entity.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Class to generate an example for a config file.
 *
 * @author Daniel Stolle
 */
public class ConfigExample {

    /**
     * Generates an example config file.
     *
     * @param args path to the file
     */
    public static void main(String[] args) {
        List<SensorConfig> sensorConfigs = new ArrayList<>();
        SensorConfig sensorConfig = new SensorConfig();

        sensorConfig.setRange(10);
        sensorConfig.setSensorAngle(11);
        sensorConfig.setSensorRadius(13);
        sensorConfig.setMeasurementAccuracy(14);
        sensorConfig.setDirectionX(15);
        sensorConfig.setDirectionY(16);
        sensorConfig.setDirectionZ(17);
        sensorConfig.setPosX(18);
        sensorConfig.setPosY(19);
        sensorConfig.setPosZ(20);

        sensorConfigs.add(sensorConfig);
        sensorConfigs.add(new SensorConfig());
        sensorConfigs.add(new SensorConfig());

        List<WindConfig> windConfigs = new ArrayList<>();
        windConfigs.add(new WindConfig());

        List<ObstacleConfig> obstacleConfigs = new ArrayList<>();
        obstacleConfigs.add(new ObstacleConfig());

        LocationConfig locationConfig = new LocationConfig();
        locationConfig.setX(1);
        locationConfig.setY(2);
        locationConfig.setZ(3);

        SimulationConfig simulationConfig = new SimulationConfig();
        simulationConfig.setSensorConfigList(sensorConfigs);
        simulationConfig.setWindConfigList(windConfigs);
        simulationConfig.setObstacleConfigList(obstacleConfigs);
        simulationConfig.setLocationConfig(locationConfig);

        if (args.length == 1 && args[0] != null) {
            ConfigWriter.writeConfig(simulationConfig, args[0]);
            System.out.println("Created config file: " + args[0]);
        } else {
            System.out.println("Missing filepath.");
        }
    }
}
