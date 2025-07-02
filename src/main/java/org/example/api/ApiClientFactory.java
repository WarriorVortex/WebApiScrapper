package org.example.api;

import org.example.api.clients.NytApiClient;
import org.example.api.clients.TmdbApiClient;
import org.example.api.clients.WeatherApiClient;
import org.example.configuration.Service;

public class ApiClientFactory {
    public static ApiClient create(Service service) {
        if (service == null) {
            throw new IllegalArgumentException("Service is null");
        }
        return switch (service) {
            case Service.NYT -> new NytApiClient();
            case Service.TMDB -> new TmdbApiClient();
            case Service.WEATHER -> new WeatherApiClient();
        };
    }
}