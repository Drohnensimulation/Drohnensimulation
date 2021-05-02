package de.thi.dronesim.persistence;

import de.thi.dronesim.persistence.entity.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Test for {@link ConfigReader}
 */
public class ConfigReaderTest {

    private Path temp;

    // /////////////////////////////////////////////////////////////////////////////
    // Init
    // /////////////////////////////////////////////////////////////////////////////

    @BeforeEach
    void createTempFile() {
        temp = TempFileUtils.createTempFile();
    }

    @AfterEach
    void deleteTempFile() {
        TempFileUtils.deleteTempFile(temp);
    }

    // /////////////////////////////////////////////////////////////////////////////
    // Tests
    // /////////////////////////////////////////////////////////////////////////////

    @Test
    void readEmptyConfig() throws IOException {
        String content = "" +
                "{\n" +
                "  \"locationConfig\": null,\n" +
                "  \"obstacleConfiList\": null,\n" +
                "  \"sensorConfigList\": null,\n" +
                "  \"windConfiList\": null\n" +
                "}";

        FileUtils.writeFile(temp.toAbsolutePath().toString(), content);

        SimulationConfig config = ConfigReader.readConfig(temp.toAbsolutePath().toString());

        assertNull(config.getLocationConfig());
        assertNull(config.getObstacleConfigList());
        assertNull(config.getSensorConfigList());
        assertNull(config.getWindConfigList());
    }

    @Test
    void readFullConfig() {
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


        ConfigWriter.writeConfig(simulationConfig, temp.toAbsolutePath().toString());

        SimulationConfig readConfig = ConfigReader.readConfig(temp.toAbsolutePath().toString());

        assertEquals(simulationConfig, readConfig);
    }
}
