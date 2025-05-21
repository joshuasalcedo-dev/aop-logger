package io.joshuasalcedo.library.logging.factory;

import io.joshuasalcedo.library.logging.core.ClassAwareLogger;
import io.joshuasalcedo.library.logging.core.Logger;
import io.joshuasalcedo.library.logging.core.LogLevel;
import io.joshuasalcedo.library.logging.output.console.ConsoleLogger;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Unified factory for creating and managing logger instances.
 * Provides a convenient way to get class-specific loggers with
 * control over formatting and log levels.
 */
public class LoggerFactory {
    // Cache of loggers by class name
    private static final Map<String, Logger> loggers = new ConcurrentHashMap<>();
    
    // The default logger type to create
    private static Class<? extends Logger> defaultLoggerType = ConsoleLogger.class;
    
    // Global settings
    private static LogLevel defaultThreshold = LogLevel.INFO;
    
    // Default formatting settings
    private static boolean useEnhancedFormatting = true;
    
    /**
     * Get a logger for the specified class.
     *
     * @param clazz the class to get a logger for
     * @return a logger configured for the class
     */
    public static Logger getLogger(Class<?> clazz) {
        return loggers.computeIfAbsent(clazz.getName(), className -> {
            Logger logger = createLogger();
            
            // Set the source class if supported
            if (logger instanceof ClassAwareLogger) {
                ((ClassAwareLogger) logger).setSourceClass(clazz);
            }
            
            // Set threshold from global setting
            logger.setThreshold(defaultThreshold);
            
            // Configure formatting if it's a ConsoleLogger
            if (logger instanceof ConsoleLogger) {
                ((ConsoleLogger) logger).withEnhancedFormatting(useEnhancedFormatting);
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
        for (Logger logger : loggers.values()) {
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
        for (Map.Entry<String, Logger> entry : loggers.entrySet()) {
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
     * Set whether to use enhanced formatting with emojis (true) or 
     * classic Maven-style bracket formatting (false) for all new loggers.
     * Only affects ConsoleLogger instances.
     *
     * @param useEnhanced true for enhanced formatting, false for classic
     */
    public static void setEnhancedFormatting(boolean useEnhanced) {
        useEnhancedFormatting = useEnhanced;
        
        // Update existing loggers
        for (Logger logger : loggers.values()) {
            if (logger instanceof ConsoleLogger) {
                ((ConsoleLogger) logger).withEnhancedFormatting(useEnhanced);
            }
        }
    }
    
    /**
     * Get the current default threshold level.
     *
     * @return the current default threshold
     */
    public static LogLevel getDefaultThreshold() {
        return defaultThreshold;
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