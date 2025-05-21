package io.joshuasalcedo.library.demo;

import io.joshuasalcedo.library.logging.console.EnhancedLoggerFactory;
import io.joshuasalcedo.library.logging.exception.ContextAwareException;
import io.joshuasalcedo.library.logging.exception.ExceptionFactory;
import io.joshuasalcedo.library.logging.model.LogLevel;
import io.joshuasalcedo.library.logging.model.Logger;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * Demonstration of using ExceptionFactory with the logging framework.
 * Shows how to create various types of exceptions with consistent styling.
 */
public class ExceptionFactoryDemo {
    // Get a logger for this class
    private static final Logger logger = EnhancedLoggerFactory.getLogger(ExceptionFactoryDemo.class);
    
    public static void main(String[] args) {
        // Set global log level to show everything in the demo
        EnhancedLoggerFactory.setGlobalThreshold(LogLevel.TRACE);
        
        System.out.println("\n=== EXCEPTION FACTORY DEMONSTRATION ===\n");
        
        // Basic factory methods
        basicFactoryMethodsDemo();
        
        // Specialized exception types
        specializedExceptionTypesDemo();
        
        // Exception wrapping
        exceptionWrappingDemo();
        
        // Real-world usage scenarios
        realWorldUsageDemo();
    }
    
    /**
     * Demonstrates basic factory methods for creating exceptions
     */
    private static void basicFactoryMethodsDemo() {
        System.out.println("\n=== Basic Factory Methods ===\n");
        
        try {
            // Create a simple error exception
            throw ExceptionFactory.error("Failed to process request");
        } catch (Throwable e) {
            logger.error("Request processing error", e);
        }
        
        try {
            // Create a warning exception
            throw ExceptionFactory.warning("Resource usage approaching limit")
                    .withContext("resourceType", "memory")
                    .withContext("usedPercent", 85)
                    .withContext("threshold", 90);
        } catch (Throwable e) {
            logger.warn("Resource warning", e);
        }
        
        try {
            // Create a severe exception
            throw ExceptionFactory.severe("Critical system failure")
                    .withContext("component", "DataProcessor")
                    .withContext("state", "DEGRADED");
        } catch (Throwable e) {
            logger.severe("System alert", e);
        }
        
        try {
            // Create with builder pattern
            throw ExceptionFactory.builder("Operation timed out")
                    .logLevel(LogLevel.ERROR)
                    .context("operation", "fileUpload")
                    .context("timeout", 30000) // ms
                    .context("fileSize", "150MB")
                    .build();
        } catch (Throwable e) {
            logger.error("Timeout error", e);
        }
    }
    
    /**
     * Demonstrates specialized exception types
     */
    private static void specializedExceptionTypesDemo() {
        System.out.println("\n=== Specialized Exception Types ===\n");
        
        try {
            // Create a validation exception
            throw ExceptionFactory.validationError("Invalid user input")
                    .withField("email")
                    .withValue("not-an-email")
                    .withConstraint("pattern", "^[A-Za-z0-9+_.-]+@(.+)$")
                    .withContext("formId", "user-registration");
        } catch (Throwable e) {
            logger.error("Validation failed", e);
        }
        
        try {
            // Create a configuration exception
            throw ExceptionFactory.configError("Missing required configuration")
                    .withProperty("api.key")
                    .withSource("application.properties")
                    .withContext("environment", "production");
        } catch (Throwable e) {
            logger.error("Configuration error", e);
        }
        
        try {
            // Create a database exception
            throw ExceptionFactory.dbError("Database query failed", 
                    new SQLException("Connection reset"))
                    .withQuery("UPDATE users SET status = ? WHERE id = ?")
                    .withParameters("active", 12345)
                    .withDatabase("user_db")
                    .withContext("connectionPool", "HikariCP");
        } catch (Throwable e) {
            logger.error("Database error", e);
        }
    }
    
    /**
     * Demonstrates wrapping exceptions from third-party libraries
     */
    private static void exceptionWrappingDemo() {
        System.out.println("\n=== Exception Wrapping ===\n");
        
        try {
            try {
                // Simulate a third-party exception
                throw new IOException("File not found: config.json");
            } catch (IOException e) {
                // Wrap it with context
                Map<String, Object> context = new HashMap<>();
                context.put("filePath", "/etc/app/config.json");
                context.put("operation", "read");
                
                throw ExceptionFactory.wrap(e, LogLevel.ERROR, context);
            }
        } catch (Throwable e) {
            logger.error("IO operation failed", e);
        }
        
        try {
            try {
                // Simulate a SQL exception from a library
                throw new SQLException("Duplicate key violation");
            } catch (SQLException e) {
                // Wrap it in a domain-specific exception
                throw ExceptionFactory.dbError("Failed to create user record", e)
                        .withQuery("INSERT INTO users (username, email) VALUES (?, ?)")
                        .withParameters("johndoe", "john@example.com");
            }
        } catch (Throwable e) {
            logger.error("User creation failed", e);
        }
    }
    
    /**
     * Demonstrates real-world usage scenarios
     */
    private static void realWorldUsageDemo() {
        System.out.println("\n=== Real-World Usage ===\n");
        
        // Simulate a user service
        UserService userService = new UserService();
        
        try {
            // Try to register a user
            userService.registerUser("john", "not-an-email", "password123");
        } catch (Throwable e) {
            logger.error("Failed to register user", e);
        }
        
        // Simulate a payment service
        PaymentService paymentService = new PaymentService();
        
        try {
            // Try to process a payment
            paymentService.processPayment("user123", "order456", 99.99);
        } catch (Throwable e) {
            logger.error("Payment processing failed", e);
        }
    }
    
    /**
     * Mock user service for demo purposes
     */
    static class UserService {
        private final Logger logger = EnhancedLoggerFactory.getLogger(UserService.class);
        
        public void registerUser(String username, String email, String password) throws ExceptionFactory.ValidationException {
            logger.info(String.format("Registering user: %s", username));
            
            // Validate email
            if (!email.contains("@")) {
                throw ExceptionFactory.validationError("Invalid email format")
                        .withField("email")
                        .withValue(email)
                        .withConstraint("format", "Must be a valid email address");
            }
            
            // More logic...
            logger.success(String.format("User %s registered successfully", username));
        }
    }
    
    /**
     * Mock payment service for demo purposes
     */
    static class PaymentService {
        private final Logger logger = EnhancedLoggerFactory.getLogger(PaymentService.class);
        
        public void processPayment(String userId, String orderId, double amount) throws ContextAwareException {
            logger.info(String.format("Processing payment of %s for order %s", amount, orderId));
            
            try {
                // Simulate a database lookup
                if (userId.equals("user123")) {
                    throw new SQLException("Connection timeout");
                }
                
                // More logic...
                logger.success("Payment processed successfully");
                
            } catch (SQLException e) {
                // Wrap the SQL exception
                throw ExceptionFactory.dbError("Failed to process payment", e)
                        .withDatabase("payments_db")
                        .withContext("userId", userId)
                        .withContext("orderId", orderId)
                        .withContext("amount", amount)
                        .withContext("timestamp", System.currentTimeMillis());
            }
        }
    }
}