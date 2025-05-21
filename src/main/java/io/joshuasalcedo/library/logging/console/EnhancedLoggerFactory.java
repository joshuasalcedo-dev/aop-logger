package io.joshuasalcedo.library.logging.console;

import io.joshuasalcedo.library.logging.console.EnhancedConsoleLogger;
import io.joshuasalcedo.library.logging.model.ClassAwareLogger;
import io.joshuasalcedo.library.logging.model.Logger;
import io.joshuasalcedo.library.logging.model.LogLevel;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Factory for creating and managing enhanced logger instances.
 * Provides a convenient way to get class-specific loggers with
 * rich exception formatting capabilities.
 */
public class EnhancedLoggerFactory {
    // Cache of loggers by class name
    private static final Map<String, EnhancedConsoleLogger> loggers = new ConcurrentHashMap<>();
    
    // Global settings
    private static LogLevel defaultThreshold = LogLevel.INFO;
    
    /**
     * Get an enhanced logger for the specified class.
     *
     * @param clazz the class to get a logger for
     * @return an enhanced logger configured for the class
     */
    public static EnhancedConsoleLogger getLogger(Class<?> clazz) {
        return loggers.computeIfAbsent(clazz.getName(), className -> {
            EnhancedConsoleLogger logger = EnhancedConsoleLogger.create();
            
            // Set the source class if supported
            if (logger instanceof ClassAwareLogger) {
                ((ClassAwareLogger) logger).setSourceClass(clazz);
            }
            
            // Set threshold from global setting
            logger.setThreshold(defaultThreshold);
            
            return logger;
        });
    }
    
    /**
     * Set the default log level threshold for all new loggers.
     * This does not affect already created loggers.
     *
     * @param level the new default threshold
     */
    public static void setDefaultThreshold(LogLevel level) {
        defaultThreshold = level;
    }
    
    /**
     * Set the log level threshold for all existing loggers.
     *
     * @param level the new threshold
     */
    public static void setGlobalThreshold(LogLevel level) {
        for (EnhancedConsoleLogger logger : loggers.values()) {
            logger.setThreshold(level);
        }
        defaultThreshold = level;
    }
    
    /**
     * Clear all cached loggers.
     * Useful for reconfiguring the logging system.
     */
    public static void resetLoggers() {
        loggers.clear();
    }
    
    /**
     * Sets the log level for a specific package and its subpackages.
     * Affects all existing loggers for classes in that package.
     *
     * @param packageName the package name, e.g. "com.example"
     * @param level the log level to set
     */
    public static void setPackageLogLevel(String packageName, LogLevel level) {
        for (Map.Entry<String, EnhancedConsoleLogger> entry : loggers.entrySet()) {
            if (entry.getKey().startsWith(packageName)) {
                entry.getValue().setThreshold(level);
            }
        }
    }
    
    /**
     * Enable debug logging for the specified package and its subpackages.
     * This is a convenience method equivalent to setPackageLogLevel(packageName, LogLevel.DEBUG).
     *
     * @param packageName the package name, e.g. "com.example"
     */
    public static void enableDebug(String packageName) {
        setPackageLogLevel(packageName, LogLevel.DEBUG);
    }
    
    /**
     * Disable debug logging for the specified package and its subpackages
     * by setting the log level to INFO.
     *
     * @param packageName the package name, e.g. "com.example"
     */
    public static void disableDebug(String packageName) {
        setPackageLogLevel(packageName, LogLevel.INFO);
    }
    
    /**
     * Enable or disable debug logging globally.
     *
     * @param enable true to enable debug logging, false to disable
     */
    public static void setDebugLogging(boolean enable) {
        setGlobalThreshold(enable ? LogLevel.DEBUG : LogLevel.INFO);
    }
    
    /**
     * Get the current default threshold level.
     *
     * @return the current default threshold
     */
    public static LogLevel getDefaultThreshold() {
        return defaultThreshold;
    }
}