package org.example.data.writers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.example.data.DataRecord;
import org.example.data.DataWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class JsonDataWriter extends DataWriter {
    public JsonDataWriter(File file) throws IOException {
        super(file);
        objectMapper = new ObjectMapper()
            .disable(JsonGenerator.Feature.AUTO_CLOSE_TARGET)
            .enable(SerializationFeature.INDENT_OUTPUT);
    }

    @Override
    public void writeData(List<DataRecord> records, FileWriter fileWriter) throws IOException {
        for (DataRecord record : records) {
            Map<String, Object> objectMap = new LinkedHashMap<>();
            objectMap.put("source", record.source());
            objectMap.putAll(record.data());
            objectMapper.writeValue(fileWriter, objectMap);
            fileWriter.write(System.lineSeparator());
        }
        fileWriter.write(System.lineSeparator());
    }
}
