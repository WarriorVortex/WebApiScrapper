package org.example.api.clients;

import org.example.api.ApiClient;

public class WeatherApiClient extends ApiClient {
    private static final String LATITUDE = getEnvVariable("LATITUDE");
    private static final String LONGITUDE = getEnvVariable("LONGITUDE");

    @Override
    public String getName() {
        return "Weather Government";
    }

    @Override
    public String getUri() {
        return "https://api.weather.gov/gridpoints/MPX/" + LATITUDE + "," + LONGITUDE + "/forecast";
    }

    @Override
    public String getFieldName() {
        return "periods";
    }

    @Override
    public String getJsonPointerName() {
        return "/properties";
    }
}
