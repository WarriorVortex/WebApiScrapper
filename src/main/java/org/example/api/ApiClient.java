package org.example.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import io.github.cdimascio.dotenv.Dotenv;
import org.example.data.DataRecord;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public abstract class ApiClient {
    private static final Dotenv DOTENV = Dotenv
            .configure()
            .ignoreIfMalformed()
            .ignoreIfMissing()
            .load();

    private static final String HEADER_NAME = "User-Agent";
    private static final String HEADER_VALUE = "REST API Scrapper";
    private final HttpClient client = HttpClient.newHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();

    public abstract String getName();

    public abstract String getUri();

    public abstract String getFieldName();

    public abstract String getJsonPointerName();

    public final List<DataRecord> fetchRecords() throws IOException, InterruptedException {
        try {
            HttpRequest request = HttpRequest.newBuilder(URI.create(getUri()))
                    .header(HEADER_NAME, HEADER_VALUE)
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            JsonNode root = mapper.readTree(response.body());
            JsonNode resultsNode = root.at(getJsonPointerName()).get(getFieldName());
            if (!resultsNode.isArray()) {
                throw new IOException("Unexpected " + getName() + " API response: results are not an array");
            }
            ArrayNode results = (ArrayNode) resultsNode;
            return parseArrayNode(results);
        } catch (NullPointerException e) {
            throw new IOException("Unexpected " + getName() + " API response: results are missed");
        }
    }

    protected static String getEnvVariable(String name) {
        String value = DOTENV.get(name);
        if (value == null || value.isBlank()) {
            throw new IllegalStateException("Env var '" + name + "' is not set in .env or system environment");
        }
        return value;
    }

    private List<DataRecord> parseArrayNode(ArrayNode array) {
        if (array == null || array.isEmpty()) {
            return Collections.emptyList();
        }
        return StreamSupport.stream(array.spliterator(), false)
                .filter(JsonNode::isObject)
                .map(node -> {
                    Map<String, Object> data = node.propertyStream()
                            .collect(Collectors.toMap(
                                    Map.Entry::getKey,
                                    entry -> entry.getValue().asText())
                            );
                    return new DataRecord(getName(), data);
                })
                .toList();
    }
}
