package data;

import org.example.configuration.Format;
import org.example.data.DataWriter;
import org.example.data.DataWriterFactory;
import org.example.data.writers.CsvDataWriter;
import org.example.data.writers.JsonDataWriter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.*;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

public class DataWriterFactoryTest {
    @TempDir
    private Path tempDir;

    @Test
    void createJsonDataWriter() {
        File file = tempDir.resolve("test.json").toFile();
        try (DataWriter dataWriter = DataWriterFactory.create(Format.JSON, file)) {
            assertInstanceOf(JsonDataWriter.class, dataWriter);
        } catch (IOException e) {
            fail(e.getMessage());
        }
    }

    @Test
    void createCsvDataWriter() {
        File file = tempDir.resolve("test.csv").toFile();
        try (DataWriter dataWriter = DataWriterFactory.create(Format.CSV, file)) {
            assertInstanceOf(CsvDataWriter.class, dataWriter);
        } catch (IOException e) {
            fail(e.getMessage());
        }
    }

    @Test
    void createJsonDataWriterWithWrongFile() {
        File file = new File("!:/test.json");
        assertThrows(IOException.class, () -> DataWriterFactory.create(Format.JSON, file));
    }

    @Test
    void createCsvDataWriterWithWrongFile() {
        File file = new File("!:/test.json");
        assertThrows(IOException.class, () -> DataWriterFactory.create(Format.CSV, file));
    }

    @Test
    void createDataWriterWithNullFile() {
        assertThrows(IllegalArgumentException.class, () -> DataWriterFactory.create(Format.JSON, null));
    }

    @Test
    void createDataWriterWithNullFormat() {
        File file = new File("test.json");
        assertThrows(IllegalArgumentException.class, () -> DataWriterFactory.create(null, file));
    }
}
