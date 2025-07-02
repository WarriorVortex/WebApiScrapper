package org.example.data;

import org.example.configuration.Format;
import org.example.data.writers.CsvDataWriter;
import org.example.data.writers.JsonDataWriter;

import java.io.File;
import java.io.IOException;

public class DataWriterFactory {
    public static DataWriter create(Format format, File file) throws IOException {
        if (format == null) {
            throw new IllegalArgumentException("Format is null");
        }
        if (file == null) {
            throw new IllegalArgumentException("File is null");
        }
        return switch (format) {
            case Format.JSON -> new JsonDataWriter(file);
            case Format.CSV -> new CsvDataWriter(file);
        };
    }
}
