package io.joshuasalcedo.library.logging.output.console;

import io.joshuasalcedo.library.logging.core.AbstractLogger;
import io.joshuasalcedo.library.logging.core.Log;
import io.joshuasalcedo.library.logging.core.LogLevel;
import io.joshuasalcedo.library.logging.exception.ContextAwareException;
import io.joshuasalcedo.library.logging.exception.LoggingExceptionFormatter;
import io.joshuasalcedo.pretty.core.model.error.EnhancedThrowable;
import io.joshuasalcedo.pretty.core.model.stream.PrettyPrintStream;
import io.joshuasalcedo.pretty.core.theme.TerminalStyle;

import java.util.HashMap;
import java.util.Map;

/**
 * Abstract base class for console logger implementations.
 * Provides common functionality for console output.
 */
public abstract class AbstractConsoleLogger extends AbstractLogger {

    protected static final PrettyPrintStream out = new PrettyPrintStream(System.out);
    protected static final PrettyPrintStream err = new PrettyPrintStream(System.err);
    
    // Map to store terminal styles for each log level
    protected static final Map<LogLevel, TerminalStyle> LEVEL_STYLES = new HashMap<>();
    
    // Cache exception formatters by log level
    protected static final Map<LogLevel, LoggingExceptionFormatter> FORMATTERS = new HashMap<>();
    
    static {
        // Initialize styles for each log level
        for (LogLevel level : LogLevel.values()) {
            LEVEL_STYLES.put(level, level.getStyle());
            FORMATTERS.put(level, LoggingExceptionFormatter.builder()
                .logLevel(level)
                .highlightPackage("io.joshuasalcedo", TerminalStyle.SUCCESS)
                .build());
        }
    }
    
    /**
     * Handles throwable formatting and printing
     * @param stream the output stream
     * @param throwable the throwable to print
     * @param level the log level
     */
    protected void printThrowable(PrettyPrintStream stream, Throwable throwable, LogLevel level) {
        // Add some spacing
        stream.println();
        
        // Check throwable type in order of specificity
        if (throwable instanceof ContextAwareException) {
            // Use context-aware throwable's built-in formatting
            ContextAwareException contextThrowable = (ContextAwareException) throwable;
            contextThrowable.withLogLevel(level);
            
            // Use the appropriate formatting style
            if (isEnhancedFormatting()) {
                contextThrowable.printEnhancedStackTrace(stream);
            } else {
                contextThrowable.printStackTrace(stream);
            }
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
        String header = isEnhancedFormatting() ? 
                level.toLogString() + " Exception encountered:" :
                level.formatLogMessage("Exception encountered:");
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
     * Determines whether to use enhanced formatting with emojis (true) or 
     * classic Maven-style bracket formatting (false)
     * 
     * @return true for enhanced formatting, false for classic
     */
    protected abstract boolean isEnhancedFormatting();
}