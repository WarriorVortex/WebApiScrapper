package org.example;

import org.example.api.ApiClientExecutor;
import org.example.configuration.Configuration;

// 3 10 weather,tmdb json

public class Main {
    public static void main(String[] args) {
        try {
            Configuration config = Configuration.create(args);
            ApiClientExecutor.execute(config, "output/output");
        } catch (IllegalArgumentException e) {
            System.err.println("Configuration error: " + e.getMessage());
            System.exit(1);
        }
    }
}