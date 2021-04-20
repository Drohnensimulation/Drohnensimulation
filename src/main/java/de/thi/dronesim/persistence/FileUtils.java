package de.thi.dronesim.persistence;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Class to read/write Files.
 */
final class FileUtils {

    private FileUtils() {
        // no-op
    }

    /**
     * Reads a file.
     *
     * @param filePath path to the File
     * @return the content of the File as a String
     * @throws IOException if an I/O error occurs reading from the file or a malformed or unmappable byte sequence is read
     */
    protected static String readFile(String filePath) throws IOException {
        Path path = Paths.get(filePath);
        return Files.readString(path);
    }

    /**
     * Writes a file.
     *
     * @param filePath path to the File
     * @param content  the content to write
     * @throws IOException if an I/O error occurs reading from the file or a malformed or unmappable byte sequence is read
     */
    protected static void writeFile(String filePath, String content) throws IOException {
        Path path = Paths.get(filePath);
        Files.writeString(path, content);
    }
}
