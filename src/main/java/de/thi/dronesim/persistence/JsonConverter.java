package de.thi.dronesim.persistence;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import de.thi.dronesim.persistence.entity.SimulationConfig;

/**
 * Class for Conversion of a {@link SimulationConfig} to/from a Json-String.
 */
final class JsonConverter {

    private JsonConverter() {
        // no-op
    }

    /**
     * Converts the {@link SimulationConfig} to a Json-String.
     *
     * @param simulationConfig the Object to convert
     * @return a Json-String with null Fields and pretty Formatting
     */
    protected static String toJson(SimulationConfig simulationConfig) {
        Gson gson = new GsonBuilder()
                .serializeNulls()
                .setPrettyPrinting()
                .create();
        return gson.toJson(simulationConfig);
    }

    /**
     * Converts a Json-String to a {@link SimulationConfig}
     *
     * @param json the Json-String to convert
     * @return the converted Object
     */
    protected static SimulationConfig fromJson(String json) {
        Gson gson = new Gson();
        return gson.fromJson(json, SimulationConfig.class);
    }
}
