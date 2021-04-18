package de.thi.dronesim.persistence;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.fail;

/**
 * Class to create and delete a TempFile for Tests
 */
final class TempFileUtils {

    /**
     * Create a temporary File.
     *
     * @return path to the File
     */
    protected static Path createTempFile() {
        try {
            return Files.createTempFile("temp", ".json");
        } catch (IOException e) {
            fail("Could not create TempFile.");
            return null;
        }
    }

    /**
     * Delete a file.
     *
     * @param path path to the file
     */
    protected static void deleteTempFile(Path path) {
        try {
            Files.delete(path);
        } catch (IOException e) {
            fail("Could not delete TempFile.");
        }
    }
}
