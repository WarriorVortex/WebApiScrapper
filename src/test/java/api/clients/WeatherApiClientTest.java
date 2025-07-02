package api.clients;

import org.example.api.clients.WeatherApiClient;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class WeatherApiClientTest {
    @Test
    void testGetName() {
        WeatherApiClient client = new WeatherApiClient();
        assertEquals("Weather Government", client.getName());
    }

    @Test
    void testGetFieldName() {
        WeatherApiClient client = new WeatherApiClient();
        assertEquals("periods", client.getFieldName());
    }

    @Test
    void testGetJsonPointerName() {
        WeatherApiClient client = new WeatherApiClient();
        assertEquals("/properties", client.getJsonPointerName());
    }

    @Test
    void testGetUri() {
        WeatherApiClient client = new WeatherApiClient();
        assertDoesNotThrow(client::getUri);
        assertNotNull(client.getUri());
    }
}
