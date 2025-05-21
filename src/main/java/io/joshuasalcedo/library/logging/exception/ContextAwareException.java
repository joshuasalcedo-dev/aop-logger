package io.joshuasalcedo.library.logging.exception;

import io.joshuasalcedo.pretty.core.model.error.EnhancedThrowable;
import io.joshuasalcedo.pretty.core.model.error.PrettyException;
import io.joshuasalcedo.pretty.core.model.stream.PrettyPrintStream;
import io.joshuasalcedo.pretty.core.theme.TerminalStyle;
import io.joshuasalcedo.library.logging.core.LogLevel;

import java.io.PrintStream;
import java.util.*;
import java.time.format.DateTimeFormatter;

/**
 * Extends the EnhancedThrowable to add context awareness and log level integration
 * for the logging framework. This extends the original EnhancedThrowable without
 * modifying it since it's already published.
 */
public class ContextAwareException extends PrettyException {

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    // Context information
    private Map<String, Object> context;
    
    // Log level for styling
    private LogLevel logLevel;
    
    // Suggested solution
    private String solution;
    
    /**
     * Constructs a new context-aware throwable with null as its detail message.
     */
    public ContextAwareException() {
        super();
    }
    
    /**
     * Constructs a new context-aware throwable with the specified detail message.
     *
     * @param message the detail message
     */
    public ContextAwareException(String message) {
        super(message);
    }
    
    /**
     * Constructs a new context-aware throwable with the specified detail message and cause.
     *
     * @param message the detail message
     * @param cause the cause
     */
    public ContextAwareException(String message, Throwable cause) {
        super(message, cause);
    }
    
    /**
     * Constructs a new context-aware throwable with the specified cause.
     *
     * @param cause the cause
     */
    public ContextAwareException(Throwable cause) {
        super(cause);
    }
    
    /**
     * Creates a context-aware throwable from any throwable.
     *
     * @param throwable the throwable to convert
     * @return a new context-aware throwable
     */
    public static ContextAwareException from(Throwable throwable) {
        if (throwable == null) {
            return null;
        }
        
        if (throwable instanceof ContextAwareException) {
            return (ContextAwareException) throwable;
        }
        
        return new ContextAwareException(throwable.getMessage(), throwable.getCause());
    }
    
    /**
     * Adds context information to this throwable.
     *
     * @param key the context key
     * @param value the context value
     * @return this throwable instance for method chaining
     */
    public ContextAwareException withContext(String key, Object value) {
        if (context == null) {
            context = new HashMap<>();
        }
        context.put(key, value);
        return this;
    }
    
    /**
     * Adds multiple context entries to this throwable.
     *
     * @param contextMap a map of context entries
     * @return this throwable instance for method chaining
     */
    public ContextAwareException withContext(Map<String, Object> contextMap) {
        if (contextMap == null || contextMap.isEmpty()) {
            return this;
        }
        
        if (context == null) {
            context = new HashMap<>(contextMap);
        } else {
            context.putAll(contextMap);
        }
        
        return this;
    }
    
    /**
     * Adds a suggested solution to fix the issue.
     *
     * @param solution the suggested solution
     * @return this throwable instance for method chaining
     */
    public ContextAwareException withSolution(String solution) {
        this.solution = solution;
        return this;
    }
    
    /**
     * Gets the suggested solution.
     *
     * @return the suggested solution or null if none has been set
     */
    public String getSolution() {
        return solution;
    }
    
    /**
     * Sets the log level for this throwable.
     * This will adjust the colors and styles to match the specified log level.
     *
     * @param level the log level to use
     * @return this throwable instance for method chaining
     */
    public ContextAwareException withLogLevel(LogLevel level) {
        this.logLevel = level;
        
        // Apply styles based on log level
        if (level != null) {
            TerminalStyle style = level.getStyle();
            
            // Use reflection to access the parent class Builder to set styles
            try {
                // Set header style
                java.lang.reflect.Method headerStyleMethod = EnhancedThrowable.class.getDeclaredMethod("setHeaderStyle", TerminalStyle.class);
                headerStyleMethod.setAccessible(true);
                headerStyleMethod.invoke(this, style);
                
                // Set type style
                java.lang.reflect.Method typeStyleMethod = EnhancedThrowable.class.getDeclaredMethod("setTypeStyle", TerminalStyle.class);
                typeStyleMethod.setAccessible(true);
                typeStyleMethod.invoke(this, style);
                
                // Set message style
                java.lang.reflect.Method messageStyleMethod = EnhancedThrowable.class.getDeclaredMethod("setMessageStyle", TerminalStyle.class);
                messageStyleMethod.setAccessible(true);
                messageStyleMethod.invoke(this, style);
                
                // For error levels, set cause style too
                if (level.isAtLeast(LogLevel.ERROR)) {
                    java.lang.reflect.Method causeStyleMethod = EnhancedThrowable.class.getDeclaredMethod("setCauseStyle", TerminalStyle.class);
                    causeStyleMethod.setAccessible(true);
                    causeStyleMethod.invoke(this, style);
                }
            } catch (Exception e) {
                // Fallback if reflection fails - styles won't be changed
                System.err.println("Failed to set styles based on log level: " + e.getMessage());
            }
        }
        
        return this;
    }
    
    /**
     * Gets the context information for this throwable.
     *
     * @return the context map or null if no context has been set
     */
    public Map<String, Object> getContext() {
        return context;
    }
    
    /**
     * Gets the log level for this throwable.
     *
     * @return the log level or null if none has been set
     */
    public LogLevel getLogLevel() {
        return logLevel;
    }
    
    /**
     * Prints the enhanced stack trace to the specified print stream.
     * This method extends the functionality provided by EnhancedThrowable.
     *
     * @param out the print stream to print to
     */
    public void printEnhancedStackTrace(PrettyPrintStream out) {
        // Get the style
        TerminalStyle style = logLevel != null ? logLevel.getStyle() : TerminalStyle.ERROR;
        
        // Print the header
        out.style(style).println(this.getClass().getSimpleName() + ": " + getMessage());
        out.reset();
        
        // Print the stack trace
        printStackTrace(out);
    }
    
    /**
     * Prints a comprehensive exception report with runtime context and suggested solutions.
     * This overrides the parent method to include our context information.
     * 
     * @param out The output stream to print to
     */
    public void printExceptionReport(PrintStream out) {
        Map<String, Object> reportContext = new HashMap<>();
        
        // Add context
        if (context != null) {
            reportContext.putAll(context);
        }
        
        // Add solution if provided
        if (solution != null) {
            reportContext.put("Suggested Solution", solution);
        }
        
        super.printExceptionReport(out, reportContext);
    }
    
    /**
     * Prints a comprehensive exception report with additional runtime context and suggested solutions.
     * 
     * @param out The output stream to print to
     * @param additionalContext Additional context information to include
     */
    @Override
    public void printExceptionReport(PrintStream out, Map<String, Object> additionalContext) {
        // Merge our context with the additional context
        Map<String, Object> mergedContext = new HashMap<>();
        
        if (context != null) {
            mergedContext.putAll(context);
        }
        
        if (additionalContext != null) {
            mergedContext.putAll(additionalContext);
        }
        
        // Add solution if provided
        if (solution != null) {
            mergedContext.put("Suggested Solution", solution);
        }
        
        // Call parent method with merged context
        super.printExceptionReport(out, mergedContext);
    }
    
    /**
     * Builder class for creating ContextAwareException instances with fluent API.
     */
    public static class Builder {
        private String message;
        private Throwable cause;
        private final Map<String, Object> context = new HashMap<>();
        private LogLevel logLevel;
        private final Map<String, TerminalStyle> packageHighlights = new HashMap<>();
        private String solution;
        
        public Builder(String message) {
            this.message = message;
        }
        
        public Builder cause(Throwable cause) {
            this.cause = cause;
            return this;
        }
        
        public Builder context(String key, Object value) {
            this.context.put(key, value);
            return this;
        }
        
        public Builder context(Map<String, Object> contextMap) {
            if (contextMap != null) {
                this.context.putAll(contextMap);
            }
            return this;
        }
        
        public Builder logLevel(LogLevel level) {
            this.logLevel = level;
            return this;
        }
        
        public Builder solution(String solution) {
            this.solution = solution;
            return this;
        }
        
        public Builder highlightPackage(String packagePrefix, TerminalStyle style) {
            this.packageHighlights.put(packagePrefix, style);
            return this;
        }
        
        public ContextAwareException build() {
            ContextAwareException throwable = cause != null ?
                new ContextAwareException(message, cause) :
                new ContextAwareException(message);
                
            // Add context
            if (!context.isEmpty()) {
                throwable.withContext(context);
            }
            
            // Set log level
            if (logLevel != null) {
                throwable.withLogLevel(logLevel);
            }
            
            // Add solution
            if (solution != null) {
                throwable.withSolution(solution);
            }
            
            // Add package highlighting
            for (Map.Entry<String, TerminalStyle> entry : packageHighlights.entrySet()) {
                throwable.highlightPackage(entry.getKey(), entry.getValue());
            }
            
            return throwable;
        }
    }
    
    /**
     * Creates a new builder for fluent creation.
     *
     * @param message the exception message
     * @return a new builder
     */
    public static Builder builder(String message) {
        return new Builder(message);
    }
    
    // Helper methods for reflection (these are attempted to be added to EnhancedThrowable)
    
    private void setHeaderStyle(TerminalStyle style) {
        try {
            java.lang.reflect.Field field = EnhancedThrowable.class.getDeclaredField("headerStyle");
            field.setAccessible(true);
            field.set(this, style);
        } catch (Exception e) {
            // Ignore if reflection fails
        }
    }
    
    private void setTypeStyle(TerminalStyle style) {
        try {
            java.lang.reflect.Field field = EnhancedThrowable.class.getDeclaredField("typeStyle");
            field.setAccessible(true);
            field.set(this, style);
        } catch (Exception e) {
            // Ignore if reflection fails
        }
    }
    
    private void setMessageStyle(TerminalStyle style) {
        try {
            java.lang.reflect.Field field = EnhancedThrowable.class.getDeclaredField("messageStyle");
            field.setAccessible(true);
            field.set(this, style);
        } catch (Exception e) {
            // Ignore if reflection fails
        }
    }
    
    private void setCauseStyle(TerminalStyle style) {
        try {
            java.lang.reflect.Field field = EnhancedThrowable.class.getDeclaredField("causeStyle");
            field.setAccessible(true);
            field.set(this, style);
        } catch (Exception e) {
            // Ignore if reflection fails
        }
    }
}