package de.thi.dronesim.persistence;

import de.thi.dronesim.persistence.entity.SimulationConfig;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test for {@link ConfigWriter}
 */
public class ConfigWriterTest {

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
    void writeEmptyConfig() throws IOException {
        SimulationConfig config = new SimulationConfig();

        ConfigWriter.writeConfig(config, temp.toAbsolutePath().toString());

        String content = FileUtils.readFile(temp.toAbsolutePath().toString());
        String expectedContent = "" +
                "{\n" +
                "  \"locationConfig\": null,\n" +
                "  \"droneRadius\": 0.0,\n" +
                "  \"obstacleConfigList\": null,\n" +
                "  \"sensorConfigList\": null,\n" +
                "  \"windConfigList\": null\n" +
                "}";

        assertEquals(expectedContent, content);
    }
}
