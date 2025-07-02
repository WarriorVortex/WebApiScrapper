package org.example.data;

import java.util.Map;

public record DataRecord(String source, Map<String, Object> data) {
    @Override
    public String toString() {
        return "DataRecord[source='" + source + "; data=" + data + ']';
    }
}

