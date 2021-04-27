package de.thi.dronesim.persistence;

import de.thi.dronesim.persistence.entity.SimulationConfig;

import java.io.IOException;

/**
 * Class to read the configuration from a file.
 *
 * @author Daniel Stolle
 */
public final class ConfigReader {

    private ConfigReader() {
        // no-op
    }

    /**
     * Reads the configuration from a file.
     *
     * @param path the path to the file
     * @return the read configuration
     */
    public static SimulationConfig readConfig(String path) {
        try {
            return JsonConverter.fromJson(FileUtils.readFile(path));
        } catch (IOException e) {
            throw new RuntimeException("Could not read the config File.", e);
        }
    }
}
