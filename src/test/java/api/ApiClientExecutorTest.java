package api;

import org.example.api.ApiClient;
import org.example.api.ApiClientExecutor;
import org.example.api.ApiClientFactory;
import org.example.configuration.Configuration;
import org.example.configuration.Service;
import org.example.data.DataRecord;
import org.example.data.DataWriter;
import org.example.data.DataWriterFactory;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.CleanupMode;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.ConnectException;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.abort;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.*;

class ApiClientExecutorTest {
    private static Method taskMethod;

    private ApiClient client;
    private Service service;

    @TempDir(cleanup = CleanupMode.ALWAYS)
    private static Path tempDir;

    @BeforeAll
    static void openTaskMethodAccessible() {
        try {
            taskMethod = ApiClientExecutor.class.getDeclaredMethod(
                    "createApiClientTask", ApiClient.class, Service.class, DataWriter.class
            );
            taskMethod.setAccessible(true);
        } catch (NoSuchMethodException e) {
            abort("Task method not found");
        }
    }

    @BeforeEach
    void initializeClientAndServiceMocks() {
        client = mock(ApiClient.class);
        service = mock(Service.class);
    }

    @AfterAll
    static void closeTaskMethodAccessible() {
        if (taskMethod != null) {
            taskMethod.setAccessible(false);
        }
    }

    private Runnable createTask(ApiClient client, Service service, DataWriter writer) throws Exception {
        return (Runnable) taskMethod.invoke(null, client, service, writer);
    }

    @Test
    void testConnectExceptionIsHandled() {
        try (DataWriter writer = mock(DataWriter.class)) {
            when(client.fetchRecords()).thenThrow(new ConnectException("No connection"));

            Runnable task = createTask(client, service, writer);
            task.run();

            verify(writer, never()).write(any());
        } catch (Exception e) {
            fail("Unexpected exception: " + e.getMessage());
        }
    }

    @Test
    void testIOExceptionIsHandled() {
        try (DataWriter writer = mock(DataWriter.class)) {
            when(client.fetchRecords()).thenThrow(new IOException("File system error"));

            Runnable task = createTask(client, service, writer);
            task.run();

            verify(writer, never()).write(any());
        } catch (Exception e) {
            fail("Unexpected exception: " + e.getMessage());
        }
    }

    @Test
    void testInterruptedExceptionIsHandled() {
        try (DataWriter writer = mock(DataWriter.class)) {
            when(client.fetchRecords()).thenThrow(new InterruptedException("Thread interrupted"));

            Runnable task = createTask(client, service, writer);
            task.run();

            verify(writer, never()).write(any());
        } catch (Exception e) {
            fail("Unexpected exception: " + e.getMessage());
        }
    }

    @Test
    void testWriteThrowsIOException() {
        try (DataWriter writer = mock(DataWriter.class)) {
            List<DataRecord> data = List.of(new DataRecord("x", Collections.emptyMap()));

            when(client.fetchRecords()).thenReturn(data);
            doThrow(new IOException("Write error")).when(writer).write(data);

            Runnable task = createTask(client, service, writer);
            task.run();

            verify(writer).write(data);
        } catch (Exception e) {
            fail("Unexpected exception: " + e.getMessage());
        }
    }

    @Test
    void testSuccessfulWrite() {
        try (DataWriter writer = mock(DataWriter.class)) {
            List<DataRecord> data = List.of(new DataRecord("1", Collections.emptyMap()));

            when(client.fetchRecords()).thenReturn(data);

            Runnable task = createTask(client, service, writer);
            task.run();

            verify(writer).write(data);
        } catch (Exception e) {
            fail("Unexpected exception: " + e.getMessage());
        }
    }

    @ParameterizedTest
    @MethodSource("provideRunConfiguration")
    void runExecutorWithSuccess(Configuration config) {
        executeAsMocked(config);
    }

    private static void executeAsMocked(Configuration config) {
        DataWriter mockWriter = mock(DataWriter.class);
        ApiClient mockClient = mock(ApiClient.class);

        try (
                MockedStatic<DataWriterFactory> dataWriterFactoryMock = mockStatic(DataWriterFactory.class);
                MockedStatic<ApiClientFactory> apiClientFactoryMock = Mockito.mockStatic(ApiClientFactory.class)
        ) {
            dataWriterFactoryMock.when(() -> DataWriterFactory.create(any(), any(File.class))).thenReturn(mockWriter);
            apiClientFactoryMock.when(() -> ApiClientFactory.create(any(Service.class))).thenReturn(mockClient);

            when(mockClient.fetchRecords()).thenReturn(List.of());

            String fileName = tempDir.resolve("test").toFile().getAbsolutePath();
            Thread thread = new Thread(() -> ApiClientExecutor.execute(config, fileName));
            thread.setDaemon(true);
            thread.start();

            Thread.sleep(1000);

            assertTrue(thread.isAlive(), "Executor thread should be running");

            thread.interrupt();
        } catch (InterruptedException | IOException e) {
            fail(e.getMessage());
        }
    }

    private static Stream<Arguments> provideRunConfiguration() {
        return Stream.of(
                "3 1 weather,tmdb json",
                "2 3 nyt csv",
                "1 2 tmdb,nyt csv"
        )
                .map(s -> s.split(" "))
                .map(Configuration::create)
                .map(Arguments::of);
    }
}
