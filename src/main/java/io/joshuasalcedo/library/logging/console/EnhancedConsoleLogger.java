package io.joshuasalcedo.library.logging.console;

import io.joshuasalcedo.library.logging.exception.ContextAwareException;
import io.joshuasalcedo.library.logging.exception.LoggingExceptionFormatter;
import io.joshuasalcedo.library.logging.model.AbstractLogger;
import io.joshuasalcedo.library.logging.model.Log;
import io.joshuasalcedo.library.logging.model.LogLevel;
import io.joshuasalcedo.pretty.core.model.error.EnhancedThrowable;
import io.joshuasalcedo.pretty.core.model.stream.PrettyPrintStream;
import io.joshuasalcedo.pretty.core.theme.TerminalStyle;

import java.util.HashMap;
import java.util.Map;

/**
 * Enhanced console logger implementation with beautiful exception formatting.
 * Uses the LoggingExceptionFormatter to create rich stack traces with colors,
 * improved layout, and better visual organization.
 */
public class EnhancedConsoleLogger extends AbstractLogger {

    private static final PrettyPrintStream out = new PrettyPrintStream(System.out);
    private static final PrettyPrintStream err = new PrettyPrintStream(System.err);
    
    // Cache exception formatters by log level
    private static final Map<LogLevel, LoggingExceptionFormatter> FORMATTERS = new HashMap<>();
    
    // Initialize formatters for each log level
    static {
        for (LogLevel level : LogLevel.values()) {
            FORMATTERS.put(level, LoggingExceptionFormatter.builder()
                .logLevel(level)
                .highlightPackage("io.joshuasalcedo", TerminalStyle.SUCCESS)
                .build());
        }
    }

    /**
     * Handles throwable formatting and printing
     */
    private void printThrowable(PrettyPrintStream stream, Throwable throwable, LogLevel level) {
        // Add some spacing
        stream.println();
        
        // Check throwable type in order of specificity
        if (throwable instanceof ContextAwareException) {
            // Use context-aware throwable's built-in formatting
            ContextAwareException contextThrowable = (ContextAwareException) throwable;
            contextThrowable.withLogLevel(level); // Make sure it uses the right color theme

        } else if (throwable instanceof EnhancedThrowable) {
            // Use the throwable's built-in formatting
            EnhancedThrowable enhancedThrowable = (EnhancedThrowable) throwable;
            enhancedThrowable.printEnhancedStackTrace(stream);
        } else {
            // Use formatter for standard throwables
            LoggingExceptionFormatter formatter = FORMATTERS.get(level);
            String formattedThrowable = formatter.format(throwable);
            stream.println(formattedThrowable);
        }
    }
    
    /**
     * Formats a message with placeholders
     * Example: format("Hello, {}", "World") -> "Hello, World"
     *
     * @param template the message template with {} placeholders
     * @param args the arguments to insert
     * @return the formatted message
     */
    @Override
    public String format(String template, Object... args) {
        if (template == null) {
            return "null";
        }

        if (args == null || args.length == 0) {
            return template;
        }

        StringBuilder result = new StringBuilder();
        int argIndex = 0;
        int i = 0;

        while (i < template.length()) {
            int placeholderIndex = template.indexOf("{}", i);

            if (placeholderIndex == -1) {
                // No more placeholders, append the rest of the template
                result.append(template.substring(i));
                break;
            }

            // Append the text before the placeholder
            result.append(template, i, placeholderIndex);

            // Append the argument if available
            if (argIndex < args.length) {
                result.append(args[argIndex] == null ? "null" : args[argIndex].toString());
                argIndex++;
            } else {
                // No more arguments, keep the placeholder
                result.append("{}");
            }

            // Move past the placeholder
            i = placeholderIndex + 2;
        }

        return result.toString();
    }

    @Override
    protected void doLog(Log logEntry) {
        // Determine which stream to use based on the log level
        PrettyPrintStream stream = logEntry.getLogLevel().isAtLeast(LogLevel.ERROR) ? err : out;

        // Get the log level
        LogLevel level = logEntry.getLogLevel();
        TerminalStyle style = level.getStyle();

        // Get the message (which may contain multiple lines)
        String message = logEntry.message();

        // Format the log line with emoji and level prefix
        String formattedPrefix = level.toLogString();
        
        // Split the message into lines
        String[] lines = message.split("\\r?\\n");

        // Print each line with the level prefix
        for (String line : lines) {
            stream.println(formattedPrefix + " " + line);
        }

        // Handle throwable if present
        Throwable throwable = logEntry.getThrowable();
        if (throwable != null) {
            printThrowable(stream, throwable, level);
        }
    }
    
    /**
     * Prints a detailed exception report with context and suggested solutions
     * 
     * @param level the log level for the exception
     * @param throwable the exception to report
     * @param context additional context information (optional)
     */
    public void logExceptionReport(LogLevel level, Throwable throwable, Map<String, Object> context) {
        if (!isEnabled(level)) {
            return;
        }
        
        // Determine which stream to use based on the log level
        PrettyPrintStream stream = level.isAtLeast(LogLevel.ERROR) ? err : out;
        
        // Log a header line
        String header = level.toLogString() + " Exception encountered:";
        stream.println(header);
        
        // Check throwable type in order of specificity
        if (throwable instanceof ContextAwareException) {
            // Use context-aware throwable's built-in reporting
            ContextAwareException contextThrowable = (ContextAwareException) throwable;
            
            // Add any additional context
            if (context != null) {
                contextThrowable.withContext(context);
            }
            
            // Make sure it uses the right color theme
            contextThrowable.withLogLevel(level);
            
            // Print the full report
            contextThrowable.printExceptionReport(stream);
        } else if (throwable instanceof EnhancedThrowable) {
            // Use the throwable's built-in reporting
            EnhancedThrowable enhancedThrowable = (EnhancedThrowable) throwable;
            
            // Print the full report with context
            enhancedThrowable.printExceptionReport(stream, context);
        } else {
            // Use formatter for standard throwables
            LoggingExceptionFormatter formatter = FORMATTERS.get(level);
            formatter.printExceptionReport(throwable, stream, context);
        }
    }
    
    /**
     * Factory method to create an enhanced console logger
     */
    public static EnhancedConsoleLogger create() {
        return new EnhancedConsoleLogger();
    }
    
    /**
     * Factory method to create an enhanced console logger with a specific source class
     */
    public static EnhancedConsoleLogger create(Class<?> sourceClass) {
        EnhancedConsoleLogger logger = new EnhancedConsoleLogger();
        logger.setSourceClass(sourceClass);
        return logger;
    }
}