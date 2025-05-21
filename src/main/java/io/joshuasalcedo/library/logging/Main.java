package io.joshuasalcedo.library.logging;


import io.joshuasalcedo.library.logging.core.LogLevel;
import io.joshuasalcedo.library.logging.core.Logger;
import io.joshuasalcedo.library.logging.core.Logging;
import io.joshuasalcedo.library.logging.exception.ContextAwareException;
import io.joshuasalcedo.library.logging.exception.ExceptionFactory;
import io.joshuasalcedo.library.logging.exception.LoggingExceptionFormatter;
import io.joshuasalcedo.library.logging.factory.LoggerFactory;
import io.joshuasalcedo.pretty.core.theme.TerminalStyle;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Demonstrates the use of the enhanced logging system with custom LogLevel enum.
 * Shows different logging levels and formatting.
 */
public class Main {

    // Create a logger for this class
    private static final Logger logger = Logging.getLogger(Main.class);

    public static void main(String[] args) {
        logger.important("=== Starting Logging Library Showcase ===");

        try {
            setupLogging();

            // Demonstrate log levels
            showcaseLogLevels();

            // Demonstrate exception formatting
            showcaseExceptionFormatting();

            // Demonstrate context-aware exceptions
            showcaseContextAwareExceptions();

            // Demonstrate AspectJ automatic method logging
            showcaseAspectJLogging();

            // Demonstrate multi-threaded logging
            showcaseMultiThreadedLogging();

            logger.success("=== Logging Library Showcase Completed Successfully ===");

        } catch (Exception e) {
            logger.fatal("Demo failed with unexpected error", e);
        }
    }

    private static void setupLogging() {
        logger.info("Setting up logging system...");

        // Configure global settings
        Logging.setGlobalThreshold(LogLevel.TRACE);
        Logging.setEnhancedFormatting(true);

        // Configure package-specific log levels
        Logging.setPackageLogLevel("io.joshuasalcedo.library.demo", LogLevel.DEBUG);

        logger.info("Logging system configured successfully");
    }

    private static void showcaseLogLevels() {
        logger.important("Demonstrating different log levels:");

        // Log at each available level
        logger.trace("Detailed tracing information (TRACE)");
        logger.debug("Debugging information (DEBUG)");
        logger.info("General information (INFO)");
        logger.success("Operation completed successfully (SUCCESS)");
        logger.notice("Notable event that might need attention (NOTICE)");
        logger.important("Significant event requiring attention (IMPORTANT)");
        logger.warn("Warning that might cause issues (WARN)");
        logger.error("Error that affects operation (ERROR)");
        logger.severe("Serious error that may cause system instability (SEVERE)");
        logger.stub("Code under development (STUB)");

        // Demonstrate message formatting
        String username = "JohnDoe";
        int count = 42;
        logger.info(logger.format("User {} processed {} items", username, count));

        logger.success("Log levels demonstration completed");
    }

    private static void showcaseExceptionFormatting() {
        logger.important("Demonstrating exception formatting:");

        try {
            // Generate a nested exception
            throwNestedExceptions();
        } catch (Exception e) {
            // Format the exception with different styles
            LoggingExceptionFormatter formatter = new LoggingExceptionFormatter();

            // Basic formatting
            logger.notice("Basic exception format:");
            System.out.println(formatter.format(e));

            // Detailed report with context
            logger.notice("Detailed exception report with context:");
            Map<String, Object> context = new HashMap<>();
            context.put("Application", "LoggingDemo");
            context.put("Version", "1.0.0");
            context.put("Environment", "Development");

            formatter.printExceptionReport(e, System.out, context);

            // Custom styled formatter
            LoggingExceptionFormatter customFormatter = LoggingExceptionFormatter.builder()
                    .logLevel(LogLevel.ERROR)
                    .highlightPackage("io.joshuasalcedo", TerminalStyle.SUCCESS)
                    .style("header", TerminalStyle.UI_HEADER)
                    .build();

            logger.notice("Custom styled exception format:");
            System.out.println(customFormatter.format(e));
        }

        logger.success("Exception formatting demonstration completed");
    }

    private static void showcaseContextAwareExceptions() {
        logger.important("Demonstrating context-aware exceptions:");

        try {
            // Try to generate a more interesting exception
            processUserData("invalid-id");
        } catch (ContextAwareException e) {
            // The exception already has context information
            logger.error("Caught context-aware exception:", e);

            // Print out the context information
            Map<String, Object> context = e.getContext();
            if (context != null) {
                logger.info("Exception context information:");
                for (Map.Entry<String, Object> entry : context.entrySet()) {
                    logger.info("  " + entry.getKey() + ": " + entry.getValue());
                }
            }

            // Print the full report
            logger.notice("Full exception report:");
            e.printExceptionReport(System.out);
        }

        // Show how to create different types of exceptions
        logger.notice("Creating different types of context-aware exceptions:");

        // Create a database exception
        ExceptionFactory.DatabaseException dbEx = ExceptionFactory.dbError("Failed to connect to database",
                        new IOException("Connection refused"))
                .withDatabase("users_db")
                .withQuery("SELECT * FROM users WHERE id = ?");

        logger.error("Database exception example:", dbEx);

        // Create a validation exception
        ExceptionFactory.ValidationException validationEx = ExceptionFactory.validationError("Invalid email format")
                .withField("email")
                .withValue("not-an-email")
                .withConstraint("pattern", "^[A-Za-z0-9+_.-]+@(.+)$");

        logger.error("Validation exception example:", validationEx);

        // Create a configuration exception
        ExceptionFactory.ConfigurationException configEx = ExceptionFactory.configError("Missing required configuration")
                .withProperty("database.url")
                .withSource("application.properties");

        logger.error("Configuration exception example:", configEx);

        logger.success("Context-aware exceptions demonstration completed");
    }

    private static void showcaseAspectJLogging() {
        logger.important("Demonstrating AspectJ automatic method logging:");

        // Create a service that will be automatically logged by AspectJ
        DemoService service = new DemoService();

        // Call methods to demonstrate automatic logging
        service.performOperation("test-operation", 3);

        try {
            // Call a method that throws an exception
            service.riskyOperation("danger");
        } catch (Exception e) {
            // The exception and method call were logged automatically
            logger.info("Caught exception from riskyOperation (this was logged automatically by AspectJ)");
        }

        // Demonstrate method with return value
        String result = service.transformData("hello world");
        logger.info("Got result: " + result + " (method call was logged automatically)");

        logger.success("AspectJ automatic logging demonstration completed");
    }

    private static void showcaseMultiThreadedLogging() throws InterruptedException {
        logger.important("Demonstrating multi-threaded logging:");

        // Create a thread pool
        int threadCount = 3;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);

        // Submit tasks that will log from different threads
        for (int i = 0; i < threadCount; i++) {
            final int threadNumber = i + 1;
            executor.submit(() -> {
                Thread.currentThread().setName("Worker-" + threadNumber);

                logger.info("Starting work in thread " + Thread.currentThread().getName());

                try {
                    // Do some work
                    for (int j = 0; j < 3; j++) {
                        logger.debug("Thread " + threadNumber + " - step " + (j + 1));
                        Thread.sleep(100);
                    }

                    // Log a success message
                    logger.success("Thread " + threadNumber + " completed successfully");

                } catch (Exception e) {
                    logger.error("Error in thread " + threadNumber, e);
                }
            });
        }

        // Shutdown the executor and wait for tasks to complete
        executor.shutdown();
        executor.awaitTermination(10, TimeUnit.SECONDS);

        logger.success("Multi-threaded logging demonstration completed");
    }

    private static void throwNestedExceptions() throws Exception {
        try {
            try {
                // Level 3: Root cause
                int result = 10 / 0; // ArithmeticException
            } catch (ArithmeticException e) {
                // Level 2: Middle exception
                throw new IllegalStateException("Failed to perform calculation", e);
            }
        } catch (IllegalStateException e) {
            // Level 1: Top level exception
            throw new Exception("Operation could not be completed", e);
        }
    }

    private static void processUserData(String userId) throws ContextAwareException {
        logger.debug("Processing user data for ID: " + userId);

        try {
            // Try to load a user file (this will fail)
            File userFile = new File("/users/" + userId + ".json");
            FileReader reader = new FileReader(userFile); // This throws FileNotFoundException

            // We never get here
            reader.close();

        } catch (IOException e) {
            // Create a context-aware exception with details about the operation
            Map<String, Object> context = new HashMap<>();
            context.put("userId", userId);
            context.put("operation", "processUserData");
            context.put("timestamp", System.currentTimeMillis());

            // Wrap the original exception with context
            throw ExceptionFactory.error("Failed to process user data", e)
                    .withContext(context)
                    .withSolution("Verify that the user exists and the file is accessible");
        }
    }

    /**
     * Demo service class to show AspectJ automatic method logging.
     * All method calls will be automatically logged by the LoggingAspect.
     */
    static class DemoService {

        public void performOperation(String name, int iterations) {
            // This method entry and exit will be logged automatically by AspectJ
            for (int i = 0; i < iterations; i++) {
                // Do something
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }

        public String transformData(String input) {
            // This method entry, exit, and return value will be logged automatically
            return input.toUpperCase();
        }

        public List<String> riskyOperation(String parameter) {
            // This method will throw an exception that will be logged and enhanced automatically
            if ("danger".equals(parameter)) {
                throw new IllegalArgumentException("Risky operation failed with parameter: " + parameter);
            }

            return new ArrayList<>();
        }
    }
}



