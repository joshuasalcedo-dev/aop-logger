package io.joshuasalcedo.library.demo;

import io.joshuasalcedo.library.logging.exception.LoggingExceptionFormatter;
import io.joshuasalcedo.library.logging.core.LogLevel;
import io.joshuasalcedo.pretty.core.theme.TerminalStyle;

import java.util.HashMap;
import java.util.Map;

/**
 * Standalone test for LoggingExceptionFormatter that doesn't depend on AspectJ
 */
public class StandaloneExceptionTest {

    public static void main(String[] args) {
        // Create the formatter directly without using factory methods
        LoggingExceptionFormatter formatter = new LoggingExceptionFormatter();

        // Configure styles manually if needed
        formatter.setStyle("header", TerminalStyle.ERROR);
        formatter.useLogLevel(LogLevel.ERROR);

        // Create a sample exception
        Exception sampleException = createSampleException();

        // Print basic format
        System.out.println("\n===== BASIC EXCEPTION FORMAT =====");
        System.out.println(formatter.format(sampleException));

        // Print full report
        System.out.println("\n===== FULL EXCEPTION REPORT =====");
        Map<String, Object> context = new HashMap<>();
        context.put("TestName", "StandaloneExceptionTest");
        context.put("Timestamp", System.currentTimeMillis());

        formatter.printExceptionReport(sampleException, System.out, context);
    }

    /**
     * Creates a sample nested exception for demonstration
     */
    private static Exception createSampleException() {
        try {
            // Create a nested exception structure
            try {
                // Level 3: Root cause
                throw new IllegalArgumentException("Invalid parameter: count cannot be negative");
            } catch (IllegalArgumentException e) {
                // Level 2: Middle exception
                throw new RuntimeException("Processing error in calculateTotal() method", e);
            }
        } catch (RuntimeException e) {
            // Level 1: Top level exception
            return new Exception("Failed to complete the operation", e);
        }
    }
}