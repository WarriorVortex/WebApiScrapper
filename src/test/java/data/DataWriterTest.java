package data;

import org.example.data.DataRecord;
import org.example.data.DataWriter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.abort;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DataWriterTest {

    static class DummyDataWriter extends DataWriter {
        public DummyDataWriter(File file) throws IOException {
            super(file);
        }

        @Override
        public void writeData(List<DataRecord> records, FileWriter fileWriter) {
        }
    }

    @TempDir
    Path tempDir;

    @Mock
    FileWriter mockFileWriter;

    @Test
    void createFileInConstructor() {
        File nestedFile = tempDir.resolve("dir/a_dir/ya_dir/file.txt").toFile();
        try (DataWriter dataWriter = new DummyDataWriter(nestedFile)) {
            assertTrue(nestedFile.getParentFile().exists());
        } catch (IOException e) {
            abort(e.getMessage());
        }
    }

    @Test
    void writeEmptyList() {
        File file = tempDir.resolve("file.txt").toFile();
        try (
                DataWriter dataWriter = new DummyDataWriter(file);
        ) {
            dataWriter.writeData(Collections.emptyList(), mockFileWriter);
        } catch (IOException e) {
            abort(e.getMessage());
        }
    }

    @Test
    void writeRecordDataList() {
        File file = tempDir.resolve("test.txt").toFile();
        try (
                DummyDataWriter writer = spy(new DummyDataWriter(file))
        ) {
            List<DataRecord> records = List.of(new DataRecord("source", Collections.emptyMap()));
            writer.writeData(records, mockFileWriter);
            verify(writer, times(1)).writeData(eq(records), any());
        } catch (IOException e) {
            abort(e.getMessage());
        }
    }

    @Test
    void writeSynchronized() {
        File file = tempDir.resolve("file.txt").toFile();
        try (DummyDataWriter writer = spy(new DummyDataWriter(file))) {
            final int threadCount = 3;
            Thread[] threads = new Thread[threadCount];

            for (int i = 0; i < threadCount; i++) {
                threads[i] = new Thread(() -> {
                    try {
                        writer.write(List.of(new DataRecord("source", Collections.emptyMap())));
                    } catch (IOException e) {
                        fail("IOException in thread");
                    }
                });
            }

            for (Thread thread : threads) {
                thread.start();
            }
            for (Thread thread : threads) {
                thread.join();
            }

            verify(writer, times(threadCount)).writeData(anyList(), any());

        } catch (IOException | InterruptedException e) {
            abort(e.getMessage());
        }
    }
}