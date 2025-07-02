package org.example.api;

import org.example.configuration.Configuration;
import org.example.configuration.Service;
import org.example.data.DataRecord;
import org.example.data.DataWriter;
import org.example.data.DataWriterFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.ConnectException;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ApiClientExecutor {
    private static final Logger logger = LoggerFactory.getLogger(ApiClientExecutor.class);

    private static Runnable createApiClientTask(ApiClient client, Service service, DataWriter dataWriter) {
        return () -> {
            try {
                List<DataRecord> records = client.fetchRecords();
                logger.info("Records fetched from {}: {}", service, records.size());
                dataWriter.write(records);
            } catch (ConnectException e) {
                System.err.println("Cannot connect service " + service);
                logger.error("Cannot connect service {}", service);
            } catch (IOException e) {
                logger.error("Error fetching from {}: {}", service, e.getMessage());
            } catch (InterruptedException e) {
                logger.warn("Thread interrupted: {}", e.getMessage());
            }
        };
    }

    public static void execute(Configuration config, String fileNameWithoutExtension) {
        String fileName = fileNameWithoutExtension + '.' + config.format().name().toLowerCase();
        File file = new File(fileName);
        try (
                ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(config.threadNumber());
                DataWriter dataWriter = DataWriterFactory.create(config.format(), file)
        ) {
            for (Service service : config.services()) {
                ApiClient client = ApiClientFactory.create(service);
                Runnable task = createApiClientTask(client, service, dataWriter);
                scheduler.scheduleWithFixedDelay(task, 0, config.pollingInterval(), TimeUnit.SECONDS);
            }

            scheduler.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);

        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }
}
