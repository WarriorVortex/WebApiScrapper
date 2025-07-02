package org.example.api.clients;

import org.example.api.ApiClient;

public class TmdbApiClient extends ApiClient {
    private static final String API_KEY = getEnvVariable("TMDB_API_KEY");

    @Override
    public String getName() {
        return "TMDB";
    }

    @Override
    public String getUri() {
        return "https://api.themoviedb.org/3/trending/movie/day?api_key=" + API_KEY;
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
