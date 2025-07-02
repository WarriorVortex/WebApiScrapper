package org.example.api.clients;

import org.example.api.ApiClient;

public class NytApiClient extends ApiClient {
    private static final String API_KEY = getEnvVariable("NYT_API_KEY");

    @Override
    public String getName() {
        return "New York Times";
    }

    @Override
    public String getUri() {
        return "https://api.nytimes.com/svc/topstories/v2/home.json?api-key=" + API_KEY;
    }

    @Override
    public String getFieldName() {
        return "results";
    }

    @Override
    public String getJsonPointerName() {
        return "";
    }
}
