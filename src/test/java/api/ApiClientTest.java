package api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.example.api.ApiClient;
import org.example.data.DataRecord;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ApiClientTest {
    private DummyApiClient client;

    @Mock
    private HttpClient mockHttpClient;

    @Mock
    private HttpResponse<String> mockResponse;

    private static class DummyApiClient extends ApiClient {
        private final String uri;

        DummyApiClient(String uri) {
            this.uri = uri;
        }

        @Override
        public String getName() {
            return "dummy";
        }

        @Override
        public String getUri() {
            return uri;
        }

        @Override
        public String getFieldName() {
            return "items";
        }

        @Override
        public String getJsonPointerName() {
            return "";
        }

        public String callGetEnvVariable(String name) {
            return getEnvVariable(name);
        }
    }

    private void injectMockHttpClient(ApiClient apiClient) {
        try {
            Field clientField = ApiClient.class.getDeclaredField("client");
            clientField.setAccessible(true);
            clientField.set(apiClient, mockHttpClient);
        } catch (Exception e) {
            throw new RuntimeException("Failed to inject mock HttpClient", e);
        }
    }

    private void setupMockHttpResponse(String json) {
        try {
            when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                    .thenReturn(mockResponse);
            when(mockResponse.body()).thenReturn(json);
        } catch (Exception e) {
            throw new RuntimeException("Failed to setup mock response", e);
        }
    }

    private List<DataRecord> fetchWithMockResponse(ApiClient apiClient, String json) {
        injectMockHttpClient(apiClient);
        setupMockHttpResponse(json);
        try {
            return apiClient.fetchRecords();
        } catch (Exception e) {
            throw new RuntimeException("Exception during fetchRecords", e);
        }
    }

    private static String generateJson() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode root = mapper.createObjectNode();
        ArrayNode array = JsonNodeFactory.instance.arrayNode();
        array.add(mapper.createObjectNode().put("a", "1"));
        array.add(mapper.createObjectNode().put("a", "2"));
        root.set("items", array);
        return mapper.writeValueAsString(root);
    }

    @Test
    void getNameReturnsDummy() {
        client = new DummyApiClient("");
        assertEquals("dummy", client.getName());
    }

    @Test
    void getFieldNameReturnsItems() {
        client = new DummyApiClient("");
        assertEquals("items", client.getFieldName());
    }

    @Test
    void getJsonPointerNameReturnsEmpty() {
        client = new DummyApiClient("");
        assertEquals("", client.getJsonPointerName());
    }

    @Test
    void fetchRecordsParsesJsonArray() {
        client = new DummyApiClient("http://dummy");
        try {
            List<DataRecord> records = fetchWithMockResponse(client, generateJson());

            assertEquals(2, records.size());

            DataRecord first = records.get(0);
            assertEquals("dummy", first.source());
            assertEquals("1", first.data().get("a"));

            DataRecord second = records.get(1);
            assertEquals("2", second.data().get("a"));
        } catch (JsonProcessingException e) {
            fail(e.getMessage());
        }
    }

    @Test
    void fetchRecordsIfRootIsNull() {
        client = new DummyApiClient("http://dummy");
        injectMockHttpClient(client);
        setupMockHttpResponse("null");

        assertThrows(IOException.class, () -> client.fetchRecords());
    }

    @Test
    void fetchRecordsIfResultsNodeMissing() {
        client = new DummyApiClient("http://dummy");
        injectMockHttpClient(client);
        setupMockHttpResponse("{\"other\": []}");

        assertThrows(IOException.class, () -> client.fetchRecords());
    }

    @Test
    void fetchRecordsIfResultsNodeNotArray() {
        client = new DummyApiClient("http://dummy");
        injectMockHttpClient(client);
        setupMockHttpResponse("{\"items\": {}}");

        assertThrows(IOException.class, () -> client.fetchRecords());
    }

    @Test
    void fetchRecordsWithEmptyList() {
        client = new DummyApiClient("http://dummy");
        List<DataRecord> records = fetchWithMockResponse(client, "{\"items\": []}");

        assertNotNull(records);
        assertTrue(records.isEmpty());
    }

    @Test
    void getEnvVariableThrowsWhenEnvVarMissing() {
        DummyApiClient dummy = new DummyApiClient("http://dummy");
        assertThrows(IllegalStateException.class, () -> dummy.callGetEnvVariable("NON_EXISTENT_ENV_VAR_FOR_TEST"));
    }
}
