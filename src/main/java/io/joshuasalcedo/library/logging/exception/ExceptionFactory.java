package io.joshuasalcedo.library.logging.exception;

import io.joshuasalcedo.library.logging.core.LogLevel;
import io.joshuasalcedo.pretty.core.theme.TerminalStyle;

import java.util.HashMap;
import java.util.Map;

/**
 * Factory for creating context-aware exceptions with consistent styling.
 * This centralizes exception creation patterns and provides convenience methods
 * for common exception types.
 */
public class ExceptionFactory {

    // Default package highlighting for application code
    private static final Map<String, TerminalStyle> DEFAULT_PACKAGE_HIGHLIGHTING = new HashMap<>();
    
    // Initialize with common application packages
    static {
        DEFAULT_PACKAGE_HIGHLIGHTING.put("io.joshuasalcedo", TerminalStyle.SUCCESS);
        DEFAULT_PACKAGE_HIGHLIGHTING.put("io.joshuasalcedo.pretty", TerminalStyle.UI_VALUE);
        DEFAULT_PACKAGE_HIGHLIGHTING.put("io.joshuasalcedo.library.logging", TerminalStyle.INFO);
    }
    
    // Common system context collectors
    private static Map<String, Object> getSystemContext() {
        Map<String, Object> context = new HashMap<>();
        context.put("threadName", Thread.currentThread().getName());
        context.put("threadId", Thread.currentThread().getId());
        context.put("timestamp", System.currentTimeMillis());
        return context;
    }
    
    /**
     * Creates a basic exception with system context.
     *
     * @param message the exception message
     * @param level the log level for styling
     * @return a context-aware exception
     */
    public static ContextAwareException create(String message, LogLevel level) {
        ContextAwareException exception = new ContextAwareException(message)
            .withLogLevel(level)
            .withContext(getSystemContext());
            
        // Add default package highlighting
        for (Map.Entry<String, TerminalStyle> entry : DEFAULT_PACKAGE_HIGHLIGHTING.entrySet()) {
            exception.highlightPackage(entry.getKey(), entry.getValue());
        }
        
        return exception;
    }
    
    /**
     * Creates an exception with a cause and system context.
     *
     * @param message the exception message
     * @param cause the cause of this exception
     * @param level the log level for styling
     * @return a context-aware exception
     */
    public static ContextAwareException create(String message, Throwable cause, LogLevel level) {
        ContextAwareException exception = new ContextAwareException(message, cause)
            .withLogLevel(level)
            .withContext(getSystemContext());
            
        // Add default package highlighting
        for (Map.Entry<String, TerminalStyle> entry : DEFAULT_PACKAGE_HIGHLIGHTING.entrySet()) {
            exception.highlightPackage(entry.getKey(), entry.getValue());
        }
        
        return exception;
    }
    
    /**
     * Creates an exception builder with system context.
     *
     * @param message the exception message
     * @return a builder for creating a context-aware exception
     */
    public static ContextAwareException.Builder builder(String message) {
        ContextAwareException.Builder builder = ContextAwareException.builder(message)
            .context(getSystemContext());
            
        // Add default package highlighting
        for (Map.Entry<String, TerminalStyle> entry : DEFAULT_PACKAGE_HIGHLIGHTING.entrySet()) {
            builder.highlightPackage(entry.getKey(), entry.getValue());
        }
        
        return builder;
    }
    
    /**
     * Convenience method to create an error-level exception.
     *
     * @param message the exception message
     * @return a context-aware exception with ERROR level
     */
    public static ContextAwareException error(String message) {
        return create(message, LogLevel.ERROR);
    }
    
    /**
     * Convenience method to create an error-level exception with a cause.
     *
     * @param message the exception message
     * @param cause the cause of this exception
     * @return a context-aware exception with ERROR level
     */
    public static ContextAwareException error(String message, Throwable cause) {
        return create(message, cause, LogLevel.ERROR);
    }
    
    /**
     * Convenience method to create a warning-level exception.
     *
     * @param message the exception message
     * @return a context-aware exception with WARN level
     */
    public static ContextAwareException warning(String message) {
        return create(message, LogLevel.WARN);
    }
    
    /**
     * Convenience method to create a warning-level exception with a cause.
     *
     * @param message the exception message
     * @param cause the cause of this exception
     * @return a context-aware exception with WARN level
     */
    public static ContextAwareException warning(String message, Throwable cause) {
        return create(message, cause, LogLevel.WARN);
    }
    
    /**
     * Convenience method to create a severe-level exception.
     *
     * @param message the exception message
     * @return a context-aware exception with SEVERE level
     */
    public static ContextAwareException severe(String message) {
        return create(message, LogLevel.SEVERE);
    }
    
    /**
     * Convenience method to create a severe-level exception with a cause.
     *
     * @param message the exception message
     * @param cause the cause of this exception
     * @return a context-aware exception with SEVERE level
     */
    public static ContextAwareException severe(String message, Throwable cause) {
        return create(message, cause, LogLevel.SEVERE);
    }
    
    /**
     * Creates a validation exception with appropriate styling.
     *
     * @param message the validation error message
     * @return a validation exception
     */
    public static ValidationException validationError(String message) {
        ValidationException exception = new ValidationException(message);
        exception.withContext(getSystemContext());
        return exception;
    }
    
    /**
     * Creates a configuration exception with appropriate styling.
     *
     * @param message the configuration error message
     * @return a configuration exception
     */
    public static ConfigurationException configError(String message) {
        ConfigurationException exception = new ConfigurationException(message);
        exception.withContext(getSystemContext());
        return exception;
    }
    
    /**
     * Creates a database exception with appropriate styling.
     *
     * @param message the database error message
     * @param cause the cause of the database error
     * @return a database exception
     */
    public static DatabaseException dbError(String message, Throwable cause) {
        DatabaseException exception = new DatabaseException(message, cause);
        exception.withContext(getSystemContext());
        return exception;
    }
    
    /**
     * Wraps any exception in a context-aware exception if it isn't already.
     * This is useful for adding context to exceptions from third-party libraries.
     *
     * @param exception the exception to wrap
     * @param level the log level to use
     * @return a context-aware exception
     */
    public static ContextAwareException wrap(Throwable exception, LogLevel level) {
        if (exception instanceof ContextAwareException) {
            return (ContextAwareException) exception;
        }
        
        return create(exception.getMessage(), exception.getCause(), level);
    }
    
    /**
     * Wraps any exception in a context-aware exception with additional context.
     *
     * @param exception the exception to wrap
     * @param level the log level to use
     * @param context additional context to add
     * @return a context-aware exception
     */
    public static ContextAwareException wrap(Throwable exception, LogLevel level, Map<String, Object> context) {
        ContextAwareException wrapped = wrap(exception, level);
        
        if (context != null) {
            wrapped.withContext(context);
        }
        
        return wrapped;
    }
    
    /**
     * Validation exception for input validation errors.
     */
    public static class ValidationException extends ContextAwareException {
        public ValidationException(String message) {
            super(message);
            withLogLevel(LogLevel.ERROR);
            highlightPackage("io.joshuasalcedo.validation", TerminalStyle.VALIDATION_ERROR);
        }
        
        public ValidationException(String message, Throwable cause) {
            super(message, cause);
            withLogLevel(LogLevel.ERROR);
            highlightPackage("io.joshuasalcedo.validation", TerminalStyle.VALIDATION_ERROR);
        }
        
        public ValidationException withField(String fieldName) {
            return (ValidationException) withContext("field", fieldName);
        }
        
        public ValidationException withValue(Object invalidValue) {
            return (ValidationException) withContext("value", invalidValue);
        }
        
        public ValidationException withConstraint(String constraintName, Object constraintValue) {
            return (ValidationException) withContext("constraint." + constraintName, constraintValue);
        }
    }
    
    /**
     * Configuration exception for configuration errors.
     */
    public static class ConfigurationException extends ContextAwareException {
        public ConfigurationException(String message) {
            super(message);
            withLogLevel(LogLevel.ERROR);
            highlightPackage("io.joshuasalcedo.config", TerminalStyle.CONFIG_ERROR);
        }
        
        public ConfigurationException(String message, Throwable cause) {
            super(message, cause);
            withLogLevel(LogLevel.ERROR);
            highlightPackage("io.joshuasalcedo.config", TerminalStyle.CONFIG_ERROR);
        }
        
        public ConfigurationException withProperty(String property) {
            return (ConfigurationException) withContext("property", property);
        }
        
        public ConfigurationException withSource(String source) {
            return (ConfigurationException) withContext("source", source);
        }
    }
    
    /**
     * Database exception for database-related errors.
     */
    public static class DatabaseException extends ContextAwareException {
        public DatabaseException(String message) {
            super(message);
            withLogLevel(LogLevel.ERROR);
            highlightPackage("io.joshuasalcedo.db", TerminalStyle.DB_ERROR);
        }
        
        public DatabaseException(String message, Throwable cause) {
            super(message, cause);
            withLogLevel(LogLevel.ERROR);
            highlightPackage("io.joshuasalcedo.db", TerminalStyle.DB_ERROR);
        }
        
        public DatabaseException withQuery(String query) {
            return (DatabaseException) withContext("query", query);
        }
        
        public DatabaseException withParameters(Object... params) {
            return (DatabaseException) withContext("parameters", params);
        }
        
        public DatabaseException withDatabase(String database) {
            return (DatabaseException) withContext("database", database);
        }
    }
}