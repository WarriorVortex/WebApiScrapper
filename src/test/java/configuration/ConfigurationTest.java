package configuration;

import org.example.configuration.Configuration;
import org.example.configuration.Format;
import org.example.configuration.Service;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Arrays;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class ConfigurationTest {
    @Test
    void createValidConfiguration() {
        String[] args = {"5", "100", "tmdb,nyt,weather", "json"};
        Configuration config = Configuration.create(args);

        assertAll(
                () -> assertEquals(Integer.parseInt(args[0]), config.threadNumber()),
                () -> assertEquals(Integer.parseInt(args[1]), config.pollingInterval()),
                () -> assertTrue(Arrays.stream(Service.values()).toList().containsAll(config.services())),
                () -> assertEquals(Format.JSON, config.format())
        );
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 2, 3, 5})
    void createConfigurationWithInvalidArgsNumber(int size) {
        String[] invalidArgs = new String[size];
        Arrays.fill(invalidArgs, "dummy");
        assertThrows(IllegalArgumentException.class, () -> Configuration.create(invalidArgs));
    }

    @ParameterizedTest
    @MethodSource("provideInvalidNumbers")
    void createConfigurationWithInvalidNumbers(String threadNum, String pollInt) {
        String[] args = {threadNum, pollInt, "nyt", "xml"};
        assertThrows(IllegalArgumentException.class, () -> Configuration.create(args));
    }

    @Test
    void createConfigurationWithNonPositiveNumbers() {
        String[] args = {"-1", "-5", "weather", "json"};
        assertThrows(IllegalArgumentException.class, () -> Configuration.create(args));
    }

    private static Stream<Arguments> provideInvalidNumbers() {
        return Stream.of(
                Arguments.of("abc", "300"),
                Arguments.of("10", "xyz"),
                Arguments.of("1.5", "200"),
                Arguments.of("5", "3.14")
        );
    }
}
