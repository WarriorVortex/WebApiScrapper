package api.clients;

import org.example.api.clients.NytApiClient;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NytApiClientTest {
    @Test
    void testGetName() {
        NytApiClient client = new NytApiClient();
        assertEquals("New York Times", client.getName());
    }

    @Test
    void testGetFieldName() {
        NytApiClient client = new NytApiClient();
        assertEquals("results", client.getFieldName());
    }

    @Test
    void testGetJsonPointerName() {
        NytApiClient client = new NytApiClient();
        assertEquals("", client.getJsonPointerName());
    }

    @Test
    void testGetUri() {
        NytApiClient client = new NytApiClient();
        assertDoesNotThrow(client::getUri);
        assertNotNull(client.getUri());
    }
}
