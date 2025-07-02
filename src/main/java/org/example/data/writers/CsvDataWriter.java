package org.example.data.writers;

import com.fasterxml.jackson.databind.SequenceWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.csv.CsvGenerator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import org.example.data.DataRecord;
import org.example.data.DataWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CsvDataWriter extends DataWriter {
    public CsvDataWriter(File file) throws IOException {
        super(file);
        objectMapper = new CsvMapper()
            .enable(CsvGenerator.Feature.ALWAYS_QUOTE_STRINGS)
            .enable(SerializationFeature.INDENT_OUTPUT);
    }

    @Override
    public void writeData(List<DataRecord> records, FileWriter fileWriter) throws IOException {
        CsvSchema schema = CsvSchema.builder()
                .addColumn("source")
                .addColumns(records.getFirst().data().keySet(), CsvSchema.ColumnType.STRING_OR_LITERAL)
                .build()
                .withHeader();

        try (SequenceWriter seqWriter = objectMapper
                .writerFor(Map.class)
                .with(schema)
                .writeValues(objectMapper.getFactory().createGenerator(fileWriter))
        ) {
            for (DataRecord record : records) {
                Map<String, Object> values = new LinkedHashMap<>(record.data());
                values.put("source", record.source());
                seqWriter.write(values);
            }
            fileWriter.write(System.lineSeparator());
        }
    }
}
