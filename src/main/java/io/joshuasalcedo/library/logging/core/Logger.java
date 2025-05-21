package io.joshuasalcedo.library.logging.core;

/**
 * Interface for logger implementations
 * Provides methods for logging at different levels
 */
public interface Logger {
    /**
     * Get the current threshold level for this logger
     * Messages below this level will not be logged
     *
     * @return the current threshold level
     */
    LogLevel getThreshold();
    
    /**
     * Set the threshold level for this logger
     * Messages below this level will not be logged
     *
     * @param threshold the new threshold level
     */
    void setThreshold(LogLevel threshold);
    
    /**
     * Check if a given log level is enabled for this logger
     *
     * @param level the level to check
     * @return true if the level is enabled
     */
    boolean isEnabled(LogLevel level);
    
    /**
     * Log a message with the given log level
     *
     * @param level the log level
     * @param message the message to log
     */
    void log(LogLevel level, String message);
    
    /**
     * Log a message with the given log level and throwable
     *
     * @param level the log level
     * @param message the message to log
     * @param throwable the throwable to log
     */
    void log(LogLevel level, String message, Throwable throwable);
    
    /**
     * Log a pre-constructed Log object
     *
     * @param logEntry the log entry to log
     */
    void log(Log logEntry);
    
    /**
     * Log a detailed tracing message
     *
     * @param message the message to log
     */
    void trace(String message);
    
    /**
     * Log a debugging message
     *
     * @param message the message to log
     */
    void debug(String message);
    
    /**
     * Log a general information message
     *
     * @param message the message to log
     */
    void info(String message);
    
    /**
     * Log a success message
     *
     * @param message the message to log
     */
    void success(String message);
    
    /**
     * Log a notice message
     *
     * @param message the message to log
     */
    void notice(String message);
    
    /**
     * Log an important message
     *
     * @param message the message to log
     */
    void important(String message);
    
    /**
     * Log a warning message
     *
     * @param message the message to log
     */
    void warn(String message);
    
    /**
     * Log a warning message with throwable
     *
     * @param message the message to log
     * @param throwable the throwable to log
     */
    void warn(String message, Throwable throwable);
    
    /**
     * Log an error message
     *
     * @param message the message to log
     */
    void error(String message);
    
    /**
     * Log an error message with throwable
     *
     * @param message the message to log
     * @param throwable the throwable to log
     */
    void error(String message, Throwable throwable);
    
    /**
     * Log a severe error message
     *
     * @param message the message to log
     */
    void severe(String message);
    
    /**
     * Log a severe error message with throwable
     *
     * @param message the message to log
     * @param throwable the throwable to log
     */
    void severe(String message, Throwable throwable);
    
    /**
     * Log a fatal error message
     *
     * @param message the message to log
     */
    void fatal(String message);
    
    /**
     * Log a fatal error message with throwable
     *
     * @param message the message to log
     * @param throwable the throwable to log
     */
    void fatal(String message, Throwable throwable);
    
    /**
     * Log a stub message for code under development
     *
     * @param message the message to log
     */
    void stub(String message);
    
    /**
     * Format a message with placeholders
     * Example: format("Hello, {}", "World") -> "Hello, World"
     *
     * @param template the message template with {} placeholders
     * @param args the arguments to insert
     * @return the formatted message
     */
    String format(String template, Object... args);
}