package io.joshuasalcedo.library.demo;

import io.joshuasalcedo.library.logging.console.EnhancedConsoleLogger;
import io.joshuasalcedo.library.logging.console.EnhancedLoggerFactory;
import io.joshuasalcedo.library.logging.exception.ContextAwareException;
import io.joshuasalcedo.library.logging.model.LogLevel;
import io.joshuasalcedo.library.logging.model.Logger;
import io.joshuasalcedo.pretty.core.theme.TerminalStyle;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Demonstration of using ContextAwareException with the logging framework.
 * Shows how to create and throw context-aware exceptions with rich formatting.
 */
public class ContextAwareThrowableDemo {
    // Get a logger for this class
    private static final Logger logger = EnhancedLoggerFactory.getLogger(ContextAwareThrowableDemo.class);
    
    public static void main(String[] args) {
        // Set global log level to show everything in the demo
        EnhancedLoggerFactory.setGlobalThreshold(LogLevel.TRACE);
        
        System.out.println("\n=== CONTEXT-AWARE THROWABLE DEMONSTRATION ===\n");
        
        // Basic ContextAwareException example
        basicContextAwareThrowableDemo();
        
        // ContextAwareException with rich context information
        richContextThrowableDemo();
        
        // Nested ContextAwareException chains
        nestedContextAwareThrowableDemo();
        
        // Using the builder pattern
        builderPatternDemo();
        
        // Integrating with custom exceptions
        customExceptionDemo();
        
        // Compare with standard exceptions
        comparisonDemo();
    }
    
    /**
     * Demonstrates basic ContextAwareException usage
     */
    private static void basicContextAwareThrowableDemo() {
        System.out.println("\n=== Basic ContextAwareException ===\n");
        
        try {
            // Create and throw a context-aware exception
            throw new ContextAwareException("Configuration file not found")
                    .withLogLevel(LogLevel.ERROR)
                    .withContext("configFile", "/etc/myapp/config.json");
        } catch (Throwable e) {
            // The logger will detect the ContextAwareException and use its formatting
            logger.error("Failed to load application settings", e);
        }
    }
    
    /**
     * Demonstrates ContextAwareException with rich context information
     */
    private static void richContextThrowableDemo() {
        System.out.println("\n=== Rich Context Information ===\n");
        
        try {
            // Create and throw a context-aware exception with detailed context
            throw new ContextAwareException("Database query failed")
                    .withLogLevel(LogLevel.SEVERE)
                    .withContext("query", "SELECT * FROM users WHERE status = ?")
                    .withContext("parameters", new String[]{"active"})
                    .withContext("database", "customers_db")
                    .withContext("server", "db-prod-03.example.com")
                    .withContext("connectionPool", "HikariCP")
                    .withContext("threadId", Thread.currentThread().getId())
                    .withContext("timestamp", System.currentTimeMillis())
                    .withContext("retryAttempts", 3);
        } catch (Throwable e) {
            // The logger will include all context info in the output
            logger.severe("Failed to fetch user data", e);
        }
    }
    
    /**
     * Demonstrates nested ContextAwareException chains
     */
    private static void nestedContextAwareThrowableDemo() {
        System.out.println("\n=== Nested ContextAwareException ===\n");
        
        try {
            try {
                try {
                    // Root cause (low-level exception)
                    throw new ContextAwareException("Network connection interrupted")
                            .withLogLevel(LogLevel.ERROR)
                            .withContext("host", "api.example.com")
                            .withContext("port", 443)
                            .withContext("protocol", "HTTPS")
                            .highlightPackage("io.joshuasalcedo.network", TerminalStyle.NETWORK_ERROR);
                } catch (Throwable e) {
                    // Mid-level exception
                    throw new ContextAwareException("API service unavailable", e)
                            .withLogLevel(LogLevel.ERROR)
                            .withContext("service", "PaymentGateway")
                            .withContext("endpoint", "/api/v2/payments")
                            .withContext("requestId", "req-293847")
                            .highlightPackage("io.joshuasalcedo.api", TerminalStyle.API_ERROR);
                }
            } catch (Throwable e) {
                // High-level application exception
                throw new ContextAwareException("Payment processing failed", e)
                        .withLogLevel(LogLevel.SEVERE)
                        .withContext("orderId", "ORD-28374")
                        .withContext("userId", "user-19283")
                        .withContext("amount", "$245.67")
                        .withContext("timestamp", System.currentTimeMillis())
                        .highlightPackage("io.joshuasalcedo.payment", TerminalStyle.ERROR);
            }
        } catch (Throwable e) {
            // Log the complete exception chain with all context
            logger.severe("Transaction failed", e);
            
            // Demonstrate the full exception report
            if (logger instanceof EnhancedConsoleLogger) {
                System.out.println("\n=== FULL EXCEPTION REPORT ===\n");
                
                Map<String, Object> additionalContext = new HashMap<>();
                additionalContext.put("reportId", "ERR-" + System.currentTimeMillis());
                additionalContext.put("supportContact", "support@example.com");
                
                ((EnhancedConsoleLogger) logger).logExceptionReport(
                        LogLevel.SEVERE, e, additionalContext);
            }
        }
    }
    
    /**
     * Demonstrates using the builder pattern for ContextAwareException
     */
    private static void builderPatternDemo() {
        System.out.println("\n=== Builder Pattern ===\n");
        
        try {
            // Use the builder pattern to create a context-aware exception
            Map<String, Object> securityContext = new HashMap<>();
            securityContext.put("userId", "user123");
            securityContext.put("ipAddress", "198.51.100.42");
            securityContext.put("userAgent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) Chrome/90.0.4430.212");
            securityContext.put("sessionId", "sess-a7b3c9d1");
            
            throw ContextAwareException.builder("Authentication failed: Invalid credentials")
                    .logLevel(LogLevel.WARN)
                    .context(securityContext)
                    .context("attemptTime", System.currentTimeMillis())
                    .context("failedAttempts", 5)
                    .highlightPackage("io.joshuasalcedo.security", TerminalStyle.SECURITY_INFO)
                    .highlightPackage("io.joshuasalcedo.auth", TerminalStyle.SECURITY_ALERT)
                    .build();
                    
        } catch (Throwable e) {
            logger.warn("Security alert", e);
        }
    }
    
    /**
     * Demonstrates integrating ContextAwareException with custom exceptions
     */
    private static void customExceptionDemo() {
        System.out.println("\n=== Custom Exception Types ===\n");
        
        try {
            // Throw a custom exception that extends ContextAwareException
            throw new ValidationException("Invalid email format")
                    .withContext("field", "email")
                    .withContext("value", "not-an-email")
                    .withContext("pattern", "^[A-Za-z0-9+_.-]+@(.+)$")
                    .withContext("formId", "user-registration");
        } catch (Throwable e) {
            logger.error("Form validation failed", e);
        }
    }
    
    /**
     * Demonstrates the difference between standard and context-aware exceptions
     */
    private static void comparisonDemo() {
        System.out.println("\n=== Standard vs Context-Aware Exceptions ===\n");
        
        // First demonstrate a standard exception
        try {
            throw new IOException("Failed to write to log file: app.log");
        } catch (Exception e) {
            System.out.println("STANDARD EXCEPTION:");
            logger.error("With standard exception:", e);
        }
        
        // Then demonstrate a context-aware exception with the same message
        try {
            throw new ContextAwareException("Failed to write to log file: app.log")
                    .withLogLevel(LogLevel.ERROR)
                    .withContext("filePath", "/var/log/app.log")
                    .withContext("fileSize", "15MB")
                    .withContext("diskSpace", "120MB free")
                    .withContext("permissions", "rw-r--r--")
                    .withContext("timestamp", System.currentTimeMillis())
                    .withContext("operation", "append")
                    .withContext("bytesToWrite", 1024);
        } catch (Throwable e) {
            System.out.println("\nCONTEXT-AWARE EXCEPTION:");
            logger.error("With context-aware exception:", e);
        }
    }
    
    /**
     * Custom exception that extends ContextAwareException
     */
    static class ValidationException extends ContextAwareException {
        public ValidationException(String message) {
            super(message);
            
            // Set default styling for this exception type
            withLogLevel(LogLevel.ERROR);
            highlightPackage("io.joshuasalcedo.validation", TerminalStyle.VALIDATION_ERROR);
        }
        
        public ValidationException(String message, Throwable cause) {
            super(message, cause);
            
            // Set default styling for this exception type
            withLogLevel(LogLevel.ERROR);
            highlightPackage("io.joshuasalcedo.validation", TerminalStyle.VALIDATION_ERROR);
        }
        
        // Add custom validation-specific methods
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
}