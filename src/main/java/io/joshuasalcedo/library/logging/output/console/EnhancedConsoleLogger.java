package io.joshuasalcedo.library.logging.output.console;

import io.joshuasalcedo.library.logging.core.Log;
import io.joshuasalcedo.library.logging.core.LogLevel;
import io.joshuasalcedo.pretty.core.model.stream.PrettyPrintStream;

import java.util.Map;

/**
 * Enhanced console logger implementation with beautiful exception formatting.
 * Always uses emoji-based formatting for better visual distinction.
 * Extends StandardConsoleLogger to reuse common console logging functionality.
 */
public class EnhancedConsoleLogger extends StandardConsoleLogger {
    
    // Always use enhanced formatting
    @Override
    protected boolean isEnhancedFormatting() {
        return true;
    }
    
    /**
     * Enhanced version with additional features that may be added in the future.
     * Currently inherits all behavior from StandardConsoleLogger with enhanced formatting.
     */
    @Override
    protected void doLog(Log logEntry) {
        super.doLog(logEntry);
    }
    
    /**
     * Enhanced exception report specifically designed for detailed troubleshooting.
     * Adds additional context and formatting that may be useful for debugging.
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