package io.joshuasalcedo.library.logging.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
class LogLevelTest {

    @Test
    void getValue() {
        assertDoesNotThrow(() -> {
            LogLevel[] logLevels = LogLevel.values();
            for (LogLevel logLevel : logLevels) {
                assertNotNull(logLevel.getValue());

            }
        });
    }

    @Test
    void getEmoji() {
    }

    @Test
    void getDescription() {
    }

    @Test
    void getStyle() {
    }

    @Test
    void testToString() {
    }

    @Test
    void toLogString() {
    }

    @Test
    void formatLogMessage() {
    }

    @Test
    void formatLogMessageWithEmoji() {
    }

    @Test
    void getDetailedDescription() {
    }

    @Test
    void fromValue() {
    }

    @Test
    void fromString() {
    }

    @Test
    void getColor() {
    }

    @Test
    void isAtLeast() {
    }

    @Test
    void values() {
    }

    @Test
    void valueOf() {
    }
}