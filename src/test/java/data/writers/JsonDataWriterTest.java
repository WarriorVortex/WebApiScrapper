package data.writers;

import org.example.data.DataRecord;
import org.example.data.writers.JsonDataWriter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class JsonDataWriterTest {
    @Test
    void testDataWriting(@TempDir Path tempDir) {
        File file = tempDir.resolve("output.json").toFile();

        try (
                JsonDataWriter writer = new JsonDataWriter(file);
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

            assertEquals(records.size() * (records.getFirst().data().size() + 3), lines.size());

            List<String> actualLines = convertRecordToJsonStrings(records.get(0));
            actualLines.addAll(convertRecordToJsonStrings(records.get(1)));
            List<String> expectedLines = lines.stream()
                    .map(String::trim)
                    .map(s -> s.endsWith(",") ? s.substring(0, s.length() - 1) : s)
                    .toList();
            assertIterableEquals(expectedLines, actualLines);

        } catch (IOException e) {
            fail("IOException thrown: " + e.getMessage());
        }
    }

    private static String wrapByQuotes(String str) {
        return "\"" + str + "\"";
    }

    private static String convertEntryToLine(Map.Entry<String, Object> entry) {
        return formatFieldLine(entry.getKey(), entry.getValue().toString());
    }

    private static String formatFieldLine(String field, String value) {
        return wrapByQuotes(field) + " : " + wrapByQuotes(value);
    }

    private static List<String> convertRecordToJsonStrings(DataRecord record) {
        List<String> result = new ArrayList<>();
        result.add("{");
        result.add(formatFieldLine("source", record.source()));
        result.addAll(
                record.data().entrySet().stream()
                        .map(JsonDataWriterTest::convertEntryToLine)
                        .toList()
        );
        result.add("}");
        return result;
    }
}
