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
 * Unified console logger implementation that combines features from both
 * the original ConsoleLogger and EnhancedConsoleLogger for optimal console output.
 */
public class ConsoleLogger extends AbstractLogger {

    private static final PrettyPrintStream out = new PrettyPrintStream(System.out);
    private static final PrettyPrintStream err = new PrettyPrintStream(System.err);
    
    // Map to store terminal styles for each log level
    private static final Map<LogLevel, TerminalStyle> LEVEL_STYLES = new HashMap<>();
    
    // Cache exception formatters by log level
    private static final Map<LogLevel, LoggingExceptionFormatter> FORMATTERS = new HashMap<>();
    
    // Configurable formatting style
    private boolean useEnhancedFormatting = true;
    
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
     * Set whether to use enhanced formatting with emojis (true) or 
     * classic Maven-style bracket formatting (false)
     */
    public ConsoleLogger withEnhancedFormatting(boolean useEnhanced) {
        this.useEnhancedFormatting = useEnhanced;
        return this;
    }

    @Override
    protected void doLog(Log logEntry) {
        // Determine which stream to use based on the log level
        PrettyPrintStream stream = logEntry.getLogLevel().isAtLeast(LogLevel.ERROR) ? err : out;
        
        // Get the log level
        LogLevel level = logEntry.getLogLevel();
        
        // Get the message (which may contain multiple lines)
        String message = logEntry.message();
        
        // Split the message into lines
        String[] lines = message.split("\\r?\\n");
        
        // Use the appropriate formatting style
        if (useEnhancedFormatting) {
            // Enhanced formatting with emoji
            String formattedPrefix = level.toLogString();
            
            // Print each line with the enhanced prefix
            for (String line : lines) {
                stream.println(formattedPrefix + " " + line);
            }
        } else {
            // Classic Maven-style formatting
            TerminalStyle style = LEVEL_STYLES.getOrDefault(level, TerminalStyle.PLAIN);
            String levelPrefix = "[" + level.name() + "]";
            
            // Print each line with the classic prefix
            for (String line : lines) {
                stream.style(style).print(levelPrefix + " ");
                stream.reset().println(line);
            }
        }
        
        // Handle throwable if present
        Throwable throwable = logEntry.getThrowable();
        if (throwable != null) {
            printThrowable(stream, throwable, level);
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
            contextThrowable.withLogLevel(level); 
            
            if (useEnhancedFormatting) {
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
        String header = useEnhancedFormatting ? 
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
     * Enhanced exception report with troubleshooting tips
     * 
     * @param level the log level for the exception
     * @param throwable the exception to report
     * @param context additional context information
     * @param troubleshootingTips optional tips to help resolve the issue
     */
    public void logEnhancedExceptionReport(LogLevel level, Throwable throwable, 
                                        Map<String, Object> context, String... troubleshootingTips) {
        if (!isEnabled(level)) {
            return;
        }
        
        // Use the standard report as a base
        logExceptionReport(level, throwable, context);
        
        // Add troubleshooting tips if provided
        if (troubleshootingTips != null && troubleshootingTips.length > 0) {
            PrettyPrintStream stream = level.isAtLeast(LogLevel.ERROR) ? err : out;
            
            stream.println();
            stream.style(LEVEL_STYLES.get(LogLevel.NOTICE))
                  .println("💡 Troubleshooting Tips:");
            stream.reset();
            
            for (int i = 0; i < troubleshootingTips.length; i++) {
                stream.println("  " + (i + 1) + ". " + troubleshootingTips[i]);
            }
        }
    }
    
    /**
     * Factory method to create a console logger
     */
    public static ConsoleLogger create() {
        return new ConsoleLogger();
    }
    
    /**
     * Factory method to create a console logger with a specific source class
     */
    public static ConsoleLogger create(Class<?> sourceClass) {
        ConsoleLogger logger = new ConsoleLogger();
        logger.setSourceClass(sourceClass);
        return logger;
    }
    
    /**
     * Factory method to create a classic Maven-style console logger (no emojis)
     */
    public static ConsoleLogger createClassic() {
        return new ConsoleLogger().withEnhancedFormatting(false);
    }
    
    /**
     * Factory method to create a classic Maven-style console logger with source class
     */
    public static ConsoleLogger createClassic(Class<?> sourceClass) {
        ConsoleLogger logger = new ConsoleLogger();
        logger.setSourceClass(sourceClass);
        logger.withEnhancedFormatting(false);
        return logger;
    }
}