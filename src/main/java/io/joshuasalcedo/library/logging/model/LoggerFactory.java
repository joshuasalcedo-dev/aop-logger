package io.joshuasalcedo.library.logging.model;

import io.joshuasalcedo.library.logging.console.ConsoleLogger;


import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Factory for creating and managing logger instances.
 * Provides a convenient way to get class-specific loggers.
 */
public class LoggerFactory {
    private static final Map<String, Logger> loggers = new ConcurrentHashMap<>();

    // The default logger type to create
    private static Class<? extends Logger> defaultLoggerType = ConsoleLogger.class;

    /**
     * Get a logger for the specified class.
     *
     * @param clazz the class to get a logger for
     * @return a logger configured for the class
     */
    public static Logger getLogger(Class<?> clazz) {
        return loggers.computeIfAbsent(clazz.getName(), className -> {
            Logger logger = createLogger();
            if (logger instanceof ClassAwareLogger) {
                ((ClassAwareLogger) logger).setSourceClass(clazz);
            }
            return logger;
        });
    }

    /**
     * Set the default logger type to create.
     *
     * @param loggerType the logger class to use as default
     */
    public static void setDefaultLoggerType(Class<? extends Logger> loggerType) {
        defaultLoggerType = loggerType;
    }

    /**
     * Clear all cached loggers.
     * Useful for reconfiguring the logging system.
     */
    public static void resetLoggers() {
        loggers.clear();
    }

    /**
     * Create a new logger instance of the default type.
     *
     * @return a new logger instance
     */
    private static Logger createLogger() {
        try {
            return defaultLoggerType.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            // Fall back to ConsoleLogger if the default type can't be instantiated
            System.err.println("Failed to create logger of type " + defaultLoggerType.getName() +
                    ", falling back to ConsoleLogger");
            return new ConsoleLogger();
        }
    }
}