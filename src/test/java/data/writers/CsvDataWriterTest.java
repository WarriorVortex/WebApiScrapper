package data.writers;

import org.example.data.DataRecord;
import org.example.data.writers.CsvDataWriter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class CsvDataWriterTest {
    @Test
    void testDataWriting(@TempDir Path tempDir) {
        File file = tempDir.resolve("output.csv").toFile();
        try (
                CsvDataWriter writer = new CsvDataWriter(file);
                FileWriter fileWriter = new FileWriter(file)
        ) {
            Map<String, Object> data1 = new LinkedHashMap<>();
            data1.put("a", "1");
            data1.put("b", "x");

            Map<String, Object> data2 = new LinkedHashMap<>();
            data2.put("a", "2");
            data2.put("b", "y");

            List<DataRecord> records = List.of(
                    new DataRecord("source1", data1),
                    new DataRecord("source2", data2)
            );

            writer.writeData(records, fileWriter);

            List<String> lines = Files.readAllLines(file.toPath()).stream().filter(s -> !s.isEmpty()).toList();

            assertEquals(records.size() + 1, lines.size());
            List<String> expectedLines = convertRecordsToCsvLines(records);

            assertIterableEquals(expectedLines, lines);
        } catch (IOException e) {
            fail(e.getMessage());
        }
    }

    private static String wrapByQuotes(String str) {
        return "\"" + str + "\"";
    }

    private static String convertRecordToLine(DataRecord record) {
        StringJoiner joiner = new StringJoiner(",");
        joiner.add(wrapByQuotes(record.source()));
        record.data().values().forEach(value -> joiner.add(wrapByQuotes(value.toString())));
        return joiner.toString();
    }

    private static List<String> convertRecordsToCsvLines(List<DataRecord> records) {
        List<String> result = new ArrayList<>();
        String fieldsAsString = records.getFirst().data().keySet().stream()
                .map(CsvDataWriterTest::wrapByQuotes)
                .collect(Collectors.joining(","));
        result.add(wrapByQuotes("source") + "," + fieldsAsString);
        result.addAll(
                records.stream().map(CsvDataWriterTest::convertRecordToLine).toList()
        );
        return result;
    }
}

