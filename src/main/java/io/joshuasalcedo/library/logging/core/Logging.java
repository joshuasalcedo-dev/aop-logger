package io.joshuasalcedo.library.logging.core;

import io.joshuasalcedo.library.logging.factory.LoggerFactory;

/**
 * Facade class for the logging framework that provides a simple entry point
 * for client code to access logging functionality.
 */
public final class Logging {
    
    private Logging() {
        // Private constructor to prevent instantiation
    }
    
    /**
     * Get a logger for the specified class.
     *
     * @param clazz the class to get a logger for
     * @return a logger configured for the class
     */
    public static Logger getLogger(Class<?> clazz) {
        return LoggerFactory.getLogger(clazz);
    }
    
    /**
     * Set the default log level threshold for all new loggers.
     *
     * @param level the new default threshold
     */
    public static void setDefaultThreshold(LogLevel level) {
        LoggerFactory.setDefaultThreshold(level);
    }
    
    /**
     * Set the log level threshold for all existing loggers.
     *
     * @param level the new threshold
     */
    public static void setGlobalThreshold(LogLevel level) {
        LoggerFactory.setGlobalThreshold(level);
    }
    
    /**
     * Sets the log level for a specific package and its subpackages.
     *
     * @param packageName the package name, e.g. "com.example"
     * @param level the log level to set
     */
    public static void setPackageLogLevel(String packageName, LogLevel level) {
        LoggerFactory.setPackageLogLevel(packageName, level);
    }
    
    /**
     * Enable debug logging for the specified package and its subpackages.
     *
     * @param packageName the package name, e.g. "com.example"
     */
    public static void enableDebug(String packageName) {
        LoggerFactory.enableDebug(packageName);
    }
    
    /**
     * Disable debug logging for the specified package and its subpackages.
     *
     * @param packageName the package name, e.g. "com.example"
     */
    public static void disableDebug(String packageName) {
        LoggerFactory.disableDebug(packageName);
    }
    
    /**
     * Enable or disable debug logging globally.
     *
     * @param enable true to enable debug logging, false to disable
     */
    public static void setDebugLogging(boolean enable) {
        LoggerFactory.setDebugLogging(enable);
    }
    
    /**
     * Set whether to use enhanced formatting with emojis or classic formatting.
     *
     * @param useEnhanced true for enhanced formatting, false for classic
     */
    public static void setEnhancedFormatting(boolean useEnhanced) {
        LoggerFactory.setEnhancedFormatting(useEnhanced);
    }
    
    /**
     * Reset the logging system, clearing all cached loggers.
     */
    public static void reset() {
        LoggerFactory.resetLoggers();
    }
}