package io.joshuasalcedo.library.demo;

import io.joshuasalcedo.library.logging.console.EnhancedLoggerFactory;
import io.joshuasalcedo.library.logging.exception.ExceptionFactory;
import io.joshuasalcedo.library.logging.model.LogLevel;
import io.joshuasalcedo.library.logging.model.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Demonstration of the EnhancedLoggingAspect, which automatically logs
 * method executions and enhances exceptions with contextual information.
 */
public class AspectLoggingDemo {

    private static final Logger logger = EnhancedLoggerFactory.getLogger(AspectLoggingDemo.class);

    public static void main(String[] args) {
        // Set global log level to show everything in the demo
        EnhancedLoggerFactory.setGlobalThreshold(LogLevel.TRACE);

        System.out.println("\n=== EXCEPTION HANDLING DEMONSTRATION ===\n");
        logger.info("Starting exception handling demonstration");

        try {
            // 1. Simple method call with parameters
            processOrder("ORD-12345", new BigDecimal("549.99"), "Express");

            // 2. Method that throws an exception
            processPayment("12345", "4111-1111-1111-1111", new BigDecimal("549.99"));

        } catch (Exception  e) {
            // We just need to handle it
            logger.info("Main method caught the exception");
        }

        try {
            // 3. Method with a chain of calls that leads to an exception
            processComplexTransaction("TX-67890");
        } catch (Exception  e) {
            logger.info("Complex transaction failed");
        }

        // 4. Using collections
        String[] items = new String[]{"item1", "item2", "item3"};
        processItems(items);

        // 5. Using a list
        List<String> itemList = Arrays.asList("itemA", "itemB", "itemC");
        processList(itemList);

        // Done
        logger.info("Exception handling demonstration completed");
    }

    /**
     * Process an order with various parameters
     */
    public static void processOrder(String orderId, BigDecimal amount, String shippingMethod) {
        logger.info("Processing order: " + orderId);

        // Simulate some processing time
        simulateProcessing(500);

        logger.success("Order " + orderId + " processed successfully");
    }

    /**
     * Process a payment that will throw an exception
     */
    public static void processPayment(String userId, String cardNumber, BigDecimal amount) throws Exception {
        logger.info("Processing payment for user: " + userId);

        // Simulate processing
        simulateProcessing(300);

        // Deliberately throw an exception
        if (true) {
            // Use our factory to create a context-aware exception
            throw ExceptionFactory.error("Payment gateway connection timeout")
                    .withContext("userId", userId)
                    .withContext("amount", amount)
                    .withContext("cardLast4", cardNumber.substring(cardNumber.length() - 4));
        }

        logger.success("Payment processed successfully");
    }

    /**
     * Process a complex transaction with nested method calls
     */
    public static void processComplexTransaction(String transactionId) throws Exception {
        logger.info("Starting complex transaction: " + transactionId);

        // First step
        validateTransaction(transactionId);

        // Second step - will throw exception
        updateDatabase(transactionId);

        // We never get here
        logger.success("Transaction completed: " + transactionId);
    }

    /**
     * Validate a transaction
     */
    private static void validateTransaction(String transactionId) {
        logger.debug("Validating transaction: " + transactionId);
        simulateProcessing(200);
        logger.debug("Transaction validation successful");
    }

    /**
     * Update database - will throw exception
     */
    private static void updateDatabase(String transactionId) throws Exception {
        logger.debug("Updating database for transaction: " + transactionId);

        try {
            // Nested call that will throw an exception
            readConfigFile();
        } catch (IOException e) {
            // Wrap the exception with our factory
            throw ExceptionFactory.severe("Database update failed", e)
                    .withContext("transactionId", transactionId)
                    .withContext("operation", "update")
                    .withContext("timestamp", System.currentTimeMillis());
        }
    }

    /**
     * Read a configuration file - will throw exception
     */
    private static void readConfigFile() throws IOException {
        logger.debug("Reading configuration file");

        // Try to read a non-existent file
        File file = new File("nonexistent-config.json");
        try (FileInputStream fis = new FileInputStream(file)) {
            // This won't execute
            logger.debug("Configuration file loaded");
        }
    }

    /**
     * Process an array of items
     */
    public static void processItems(String[] items) {
        logger.info("Processing " + items.length + " items");

        // Process each item
        for (String item : items) {
            logger.debug("Processing item: " + item);
            simulateProcessing(100);
        }

        logger.success("Processed " + items.length + " items successfully");
    }

    /**
     * Process a list of items
     */
    public static void processList(List<String> items) {
        logger.info("Processing " + items.size() + " items from list");

        // Process each item
        for (String item : items) {
            logger.debug("Processing list item: " + item);
            simulateProcessing(100);
        }

        logger.success("Processed " + items.size() + " list items successfully");
    }

    /**
     * Helper method to simulate processing time
     */
    private static void simulateProcessing(long milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}