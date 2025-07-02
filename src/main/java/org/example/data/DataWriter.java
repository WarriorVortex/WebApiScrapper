package org.example.data;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public abstract class DataWriter implements AutoCloseable {
    protected ObjectMapper objectMapper;
    protected final FileWriter fileWriter;

    public DataWriter(File file) throws IOException {
        createDirectory(file);
        fileWriter = new FileWriter(file, true);
    }

    private static void createDirectory(File file) {
        File parentDir = file.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            parentDir.mkdirs();
        }
    }

    public synchronized void write(List<DataRecord> records) throws IOException {
        if (records.isEmpty()) return;
        writeData(records, fileWriter);
    }

    public abstract void writeData(List<DataRecord> records, FileWriter fileWriter) throws IOException;

    public void close() throws IOException {
        fileWriter.close();
    }
}
