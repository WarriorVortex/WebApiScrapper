package org.example.configuration;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public record Configuration(int threadNumber, long pollingInterval, Set<Service> services, Format format) {
    public static Configuration create(String[] args) {
        if (args.length != 4) {
            throw new IllegalArgumentException("Invalid number of arguments");
        }

        int threadNumber;
        long pollingInterval;
        try {
            threadNumber = Integer.parseInt(args[0]);
            pollingInterval = Long.parseLong(args[1]);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Thread number and poll interval must be integers");
        }
        if (threadNumber < 0 || pollingInterval < 0) {
            throw new IllegalArgumentException("Thread number and poll interval must be positive integers");
        }

        Set<Service> usedServices = Arrays.stream(args[2].toUpperCase().split(","))
                .map(Service::valueOf)
                .collect(Collectors.toSet());

        Format usedFormat = Format.valueOf(args[3].toUpperCase());

        return new Configuration(threadNumber, pollingInterval, usedServices, usedFormat);
    }
}
