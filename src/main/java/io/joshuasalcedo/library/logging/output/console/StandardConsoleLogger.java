package io.joshuasalcedo.library.logging.output.console;

import io.joshuasalcedo.library.logging.core.Log;
import io.joshuasalcedo.library.logging.core.LogLevel;
import io.joshuasalcedo.pretty.core.model.stream.PrettyPrintStream;
import io.joshuasalcedo.pretty.core.theme.TerminalStyle;

/**
 * Standard console logger implementation with configurable formatting.
 * Uses classic Maven-style formatting by default.
 */
public class StandardConsoleLogger extends AbstractConsoleLogger {
    
    // Configurable formatting style
    private boolean useEnhancedFormatting = false;
    
    /**
     * Set whether to use enhanced formatting with emojis (true) or 
     * classic Maven-style bracket formatting (false)
     */
    public StandardConsoleLogger withEnhancedFormatting(boolean useEnhanced) {
        this.useEnhancedFormatting = useEnhanced;
        return this;
    }
    
    @Override
    protected boolean isEnhancedFormatting() {
        return useEnhancedFormatting;
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
     * Factory method to create a console logger
     */
    public static StandardConsoleLogger create() {
        return new StandardConsoleLogger();
    }
    
    /**
     * Factory method to create a console logger with a specific source class
     */
    public static StandardConsoleLogger create(Class<?> sourceClass) {
        StandardConsoleLogger logger = new StandardConsoleLogger();
        logger.setSourceClass(sourceClass);
        return logger;
    }
    
    /**
     * Factory method to create an enhanced console logger (with emojis)
     */
    public static StandardConsoleLogger createEnhanced() {
        return new StandardConsoleLogger().withEnhancedFormatting(true);
    }
    
    /**
     * Factory method to create an enhanced console logger with source class
     */
    public static StandardConsoleLogger createEnhanced(Class<?> sourceClass) {
        StandardConsoleLogger logger = new StandardConsoleLogger();
        logger.setSourceClass(sourceClass);
        logger.withEnhancedFormatting(true);
        return logger;
    }
}