package io.joshuasalcedo.library.logging;

import io.joshuasalcedo.library.logging.console.ConsoleLogger;
import io.joshuasalcedo.library.logging.model.Log;
import io.joshuasalcedo.library.logging.model.LogLevel;
import io.joshuasalcedo.library.logging.model.Logger;
import io.joshuasalcedo.library.logging.model.LoggerFactory;

/**
 * Demonstrates the use of the enhanced logging system with custom LogLevel enum.
 * Shows different logging levels and formatting.
 */
public class Main {

    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        // Create a console logger

        // Set the global log level
        logger.setThreshold(LogLevel.TRACE);

        // Basic logging
        logger.info("Application starting");

        try {
            // Log different levels
            logger.trace("This is a trace message");
            logger.debug("This is a debug message");
            logger.info("This is an info message");
            logger.success("This is a success message");
            logger.notice("This is a notice message");
            logger.important("This is an important message");
            logger.warn("This is a warning message");

            // Simulate an error
            try {
                throw new RuntimeException("Something went wrong!");
            } catch (Exception e) {
                logger.error("An error occurred", e);
            }

            // Log with placeholders
            logger.info(logger.format("Hello, {}! Your score is: {}", "User", 95));

            // Test another class's logger
            AnotherClass.doSomething();

        } catch (Exception e) {
            logger.severe("Severe error in main", e);
        }

        logger.info("Application finished");
  
        // Set the threshold to DEBUG to see all levels except TRACE
        logger.setThreshold(LogLevel.DEBUG);

        System.out.println("=== Logging Demo ===");

        // Log messages at different levels
        logger.trace("This is a TRACE message (shouldn't be visible)");
        logger.debug("This is a DEBUG message");
        logger.info("This is an INFO message");
        logger.success("This is a SUCCESS message");
        logger.notice("This is a NOTICE message");
        logger.important("This is an IMPORTANT message");
        logger.warn("This is a WARNING message");

        // Log with throwable
        try {
            // Simulate an exception
            int result = 10 / 0;
        } catch (Exception e) {
            logger.error("An error occurred during calculation", e);
        }

        // Demonstrate format method
        String username = "John";
        int itemCount = 5;
        logger.info(logger.format("User {} added {} items to cart", username, itemCount));

        // Change log level to show how filtering works
        System.out.println("\n=== Changing Log Level to NOTICE ===");
        logger.setThreshold(LogLevel.NOTICE);

        // These should be hidden now
        logger.debug("This DEBUG message should be hidden");
        logger.info("This INFO message should be hidden");

        // These should still show
        logger.notice("This NOTICE message should be visible");
        logger.warn("This WARNING message should be visible");
        logger.error("This ERROR message should be visible");

        // Test severe and fatal levels
        logger.severe("This is a SEVERE error message");
        logger.fatal("This is a FATAL error message");

        // Test stub level (for development code)
        logger.stub("This feature is under development");

        System.out.println("\n=== Logging Demo Complete ===");
    }

    static class AnotherClass {
        // Each class gets its own logger instance
        private static final Logger logger =  LoggerFactory.getLogger(AnotherClass.class);

        static void doSomething() {
            logger.debug("AnotherClass is doing something");
            try {
                // Simulate a division by zero error
                int result = 5 / 0;
            } catch (Exception e) {
                logger.error("Error in AnotherClass", e);
            }
        }
    }
}