package de.thi.dronesim.persistence;

import de.thi.dronesim.persistence.entity.SimulationConfig;

import java.io.IOException;

/**
 * Class to write the Configuration in a file.
 * This is only a convenience function to create the config-file and is not needed for the simulation.
 *
 * @author Daniel Stolle
 */
public final class ConfigWriter {

    private ConfigWriter() {
        // no-op
    }

    /**
     * Writes the configuration to a File.
     *
     * @param simulationConfig configuration to save
     * @param path path for the file
     */
    public static void writeConfig(SimulationConfig simulationConfig, String path) {
        try {
            FileUtils.writeFile(path, JsonConverter.toJson(simulationConfig));
        } catch (IOException e) {
            throw new RuntimeException("Could not write the config File.", e);
        }
    }
}
