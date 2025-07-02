package api.clients;

import org.example.api.clients.TmdbApiClient;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TmdbApiClientTest {
    @Test
    void testGetName() {
        TmdbApiClient client = new TmdbApiClient();
        assertEquals("TMDB", client.getName());
    }

    @Test
    void testGetFieldName() {
        TmdbApiClient client = new TmdbApiClient();
        assertEquals("results", client.getFieldName());
    }

    @Test
    void testGetJsonPointerName() {
        TmdbApiClient client = new TmdbApiClient();
        assertEquals("", client.getJsonPointerName());
    }

    @Test
    void testGetUri() {
        TmdbApiClient client = new TmdbApiClient();
        assertDoesNotThrow(client::getUri);
        assertNotNull(client.getUri());
    }
}
