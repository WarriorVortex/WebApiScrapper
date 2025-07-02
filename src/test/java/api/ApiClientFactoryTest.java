package api;

import org.example.api.ApiClient;
import org.example.api.ApiClientFactory;
import org.example.api.clients.NytApiClient;
import org.example.api.clients.TmdbApiClient;
import org.example.api.clients.WeatherApiClient;
import org.example.configuration.Service;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ApiClientFactoryTest {
    @Test
    void createNytClient() {
        ApiClient client = ApiClientFactory.create(Service.NYT);
        assertNotNull(client);
        assertInstanceOf(NytApiClient.class, client);
    }

    @Test
    void createTmdbClient() {
        ApiClient client = ApiClientFactory.create(Service.TMDB);
        assertNotNull(client);
        assertInstanceOf(TmdbApiClient.class, client);
    }

    @Test
    void createWeatherClient() {
        ApiClient client = ApiClientFactory.create(Service.WEATHER);
        assertNotNull(client);
        assertInstanceOf(WeatherApiClient.class, client);
    }

    @Test
    void createApiClientWithNullService() {
        assertThrows(IllegalArgumentException.class, () -> ApiClientFactory.create(null));
    }
}
