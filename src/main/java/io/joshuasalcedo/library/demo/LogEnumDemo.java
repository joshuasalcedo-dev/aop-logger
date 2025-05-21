package io.joshuasalcedo.library.demo;


import io.joshuasalcedo.library.logging.console.ConsoleLogger;
import io.joshuasalcedo.library.logging.model.Log;
import io.joshuasalcedo.library.logging.model.LogLevel;
import io.joshuasalcedo.library.logging.model.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Enhanced demonstration of the logging system in real-world scenarios.
 * This class shows practical usage of the logger in various application contexts
 * such as service operations, database interactions, and concurrent processing.
 */
public class LogEnumDemo {

    // Create a logger instance for this class
    private static final Logger logger = new ConsoleLogger();
    private static final Random random = new Random();

    public static void main(String[] args) {
        logger.info("Starting LogEnumDemo application");

        // Basic demos
        displayAllLogLevels();
        demonstrateBasicLogging();
        demonstrateErrorHandling();
        demonstrateLogFiltering();

        // Advanced application scenarios
        simulateUserAuthentication("john_doe", "password123");
        simulateDatabaseOperations();
        simulateAsyncOperations();
        simulateComplexBusinessProcess();

        logger.success("LogEnumDemo application completed successfully");
    }

    private static void displayAllLogLevels() {
        logger.info("Displaying all available log levels");

        for (LogLevel level : LogLevel.values()) {
            System.out.println(level.toLogString() + " - Value: " + level.getValue() +
                    " - Description: " + level.getDescription());
        }
    }

    private static void demonstrateBasicLogging() {
        logger.info("Demonstrating basic logging capabilities");

        // Create and display logs of different levels
        System.out.println("\n>> Basic Logging Examples:");

        Log traceLog = Log.trace("Detailed trace information about method execution");
        Log debugLog = Log.debug("Debug information for developers about variable state");
        Log infoLog = Log.info("General information message about application progress");
        Log successLog = Log.success("Operation completed successfully with expected results");
        Log noticeLog = Log.notice("Important notice for users about upcoming maintenance");

        // Print formatted logs
        System.out.println(traceLog.logFormat());
        System.out.println(debugLog.logFormat());
        System.out.println(infoLog.logFormat());
        System.out.println(successLog.logFormat());
        System.out.println(noticeLog.logFormat());
    }

    private static void demonstrateErrorHandling() {
        logger.info("Demonstrating error handling with various exception types");

        // Create and display logs with exceptions
        System.out.println("\n>> Error Handling Examples:");

        try {
            // Simulate a network connectivity error
            throw new RuntimeException("Connection timeout: Unable to reach authentication server");
        } catch (RuntimeException e) {
            Log warnLog = Log.warn("Authentication may be delayed", e);
            System.out.println(warnLog.logFormat());

            try {
                // Simulate cascading errors
                throw new IllegalStateException("Session validation failed due to server error");
            } catch (Exception nested) {
                Log errorLog = Log.error("User session could not be validated", nested);
                Log severeLog = Log.severe("Security protocol breach detected", nested);
                Log fatalLog = Log.fatal("System integrity compromised - immediate shutdown required", nested);

                System.out.println(errorLog.logFormat());
                System.out.println(severeLog.logFormat());
                System.out.println(fatalLog.logFormat());
            }
        }
    }

    private static void demonstrateLogFiltering() {
        logger.info("Demonstrating log filtering based on threshold levels");

        System.out.println("\n>> Log Filtering Examples:");

        LogLevel threshold = LogLevel.WARN; // Only show WARN and above
        System.out.println("Setting log threshold to: " + threshold.toLogString());

        // Create logs of different levels
        Log[] logs = {
                Log.trace("Entering method processUserRequest with id=1234"),
                Log.debug("Request parameters validated: {userId: 1234, action: 'VIEW'}"),
                Log.info("Processing user view request for dashboard"),
                Log.warn("User session will expire in 5 minutes"),
                Log.error("Failed to load user preferences from database"),
                Log.severe("Data integrity check failed in user profile")
        };

        // Demonstrate filtering
        System.out.println("\nFiltered log messages (only " + threshold + " and above):");
        for (Log log : logs) {
            if (log.isEnabled(threshold)) {
                System.out.println(log.logFormat());
            } else {
                System.out.println("FILTERED: " + log.getLogLevel() + " is below threshold");
            }
        }
    }

    /**
     * Simulates a user authentication process with appropriate logging
     */
    private static void simulateUserAuthentication(String username, String password) {
        logger.info(logger.format("Authentication attempt for user: {}", username));

        try {
            // Simulate some processing
            validateCredentials(username, password);

            if (random.nextInt(10) < 7) {  // 70% success rate
                logger.success(logger.format("User {} successfully authenticated", username));
                loadUserProfile(username);
            } else {
                throw new SecurityException("Invalid credentials provided");
            }

        } catch (SecurityException e) {
            logger.error(logger.format("Authentication failed for user: {}", username), e);
        } catch (Exception e) {
            logger.severe("Unexpected error during authentication process", e);
        }
    }

    /**
     * Validates user credentials (simulated)
     */
    private static void validateCredentials(String username, String password) {
        logger.debug(logger.format("Validating credentials for {}", username));

        if (password == null || password.isEmpty()) {
            throw new IllegalArgumentException("Password cannot be empty");
        }

        // Simulate password strength check
        if (password.length() < 8) {
            logger.warn(logger.format("Weak password detected for user {}", username));
        }

        // Simulate a security check
        if ("admin".equals(username)) {
            logger.important("Admin login detected - applying enhanced security checks");
        }

        logger.debug("Credential validation completed");
    }

    /**
     * Loads a user profile (simulated)
     */
    private static void loadUserProfile(String username) {
        logger.debug(logger.format("Loading profile for user {}", username));

        try {
            // Simulate database query
            Map<String, Object> userProfile = fetchUserFromDatabase(username);

            logger.info(logger.format("User profile loaded with {} preference settings",
                    userProfile.get("preferencesCount")));

            // Simulate profile analysis
            analyzeUserBehavior(userProfile);

        } catch (Exception e) {
            logger.error("Failed to load complete user profile", e);
        }
    }

    /**
     * Simulates fetching user data from a database
     */
    private static Map<String, Object> fetchUserFromDatabase(String username) {
        logger.debug(logger.format("Executing database query for user {}", username));

        // Simulate query latency
        try {
            Thread.sleep(random.nextInt(500) + 100);
        } catch (InterruptedException e) {
            logger.warn("Database query interrupted", e);
            Thread.currentThread().interrupt();
        }

        // Simulate random database errors
        if (random.nextInt(10) < 2) {  // 20% chance of error
            throw new RuntimeException("Database connection timeout");
        }

        // Create simulated user data
        Map<String, Object> userData = new HashMap<>();
        userData.put("username", username);
        userData.put("email", username + "@example.com");
        userData.put("lastLogin", System.currentTimeMillis() - (random.nextInt(10) * 86400000L));
        userData.put("accountStatus", "ACTIVE");
        userData.put("preferencesCount", random.nextInt(20) + 5);

        logger.debug(logger.format("Retrieved {} data fields for user", userData.size()));
        return userData;
    }

    /**
     * Analyzes user behavior (simulated)
     */
    private static void analyzeUserBehavior(Map<String, Object> userProfile) {
        logger.notice("Performing user behavior analysis");

        // Simulate some behavior analysis with potential issues
        if (random.nextInt(10) < 3) {  // 30% chance of suspicious activity
            long lastLogin = (long) userProfile.get("lastLogin");
            String loginLocation = random.nextBoolean() ? "United States" : "Ukraine";

            logger.warn(logger.format(
                    "Suspicious login activity detected for user {}: Login from {} after {} days inactivity",
                    userProfile.get("username"), loginLocation,
                    (System.currentTimeMillis() - lastLogin) / 86400000L));
        }
    }

    /**
     * Simulates database operations with appropriate logging
     */
    private static void simulateDatabaseOperations() {
        logger.info("Starting database operations simulation");

        try {
            // Simulate opening a database connection
            logger.debug("Opening database connection to production server");

            // Simulate a transaction
            logger.debug("Beginning database transaction");

            // Simulate data operations
            int recordsProcessed = performBatchUpdate();
            logger.success(logger.format("Successfully processed {} database records", recordsProcessed));

            // Simulate transaction commit
            logger.debug("Committing transaction");

        } catch (Exception e) {
            logger.error("Database operation failed", e);
            logger.debug("Rolling back transaction");
        } finally {
            logger.debug("Closing database connection");
        }
    }

    /**
     * Simulates a batch update operation (simulated)
     */
    private static int performBatchUpdate() {
        int recordCount = random.nextInt(1000) + 500;
        logger.debug(logger.format("Preparing batch update for {} records", recordCount));

        // Simulate processing time
        try {
            Thread.sleep(random.nextInt(1000) + 500);
        } catch (InterruptedException e) {
            logger.warn("Batch update interrupted", e);
            Thread.currentThread().interrupt();
        }

        // Simulate random database errors
        if (random.nextInt(10) < 2) {  // 20% chance of error
            throw new RuntimeException("Database constraint violation during batch update");
        }

        logger.debug("Batch update completed successfully");
        return recordCount;
    }

    /**
     * Simulates asynchronous operations with logging
     */
    private static void simulateAsyncOperations() {
        logger.info("Starting asynchronous operations simulation");

        int taskCount = 5;
        ExecutorService executor = Executors.newFixedThreadPool(taskCount);

        try {
            // Submit multiple tasks
            for (int i = 0; i < taskCount; i++) {
                final int taskId = i + 1;
                executor.submit(() -> performAsyncTask(taskId));
            }

            // Shutdown and wait for completion
            executor.shutdown();
            boolean completed = executor.awaitTermination(10, TimeUnit.SECONDS);

            if (completed) {
                logger.success("All asynchronous tasks completed successfully");
            } else {
                logger.warn("Some asynchronous tasks did not complete in the allocated time");
                executor.shutdownNow();
            }

        } catch (InterruptedException e) {
            logger.error("Asynchronous operations interrupted", e);
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Performs a single asynchronous task (simulated)
     */
    private static void performAsyncTask(int taskId) {
        logger.debug(logger.format("Starting asynchronous task #{}", taskId));

        try {
            // Simulate variable processing time
            Thread.sleep(random.nextInt(2000) + 500);

            // Simulate random failures
            if (random.nextInt(10) < 3) {  // 30% chance of failure
                throw new RuntimeException(logger.format("Task #{} failed during processing", taskId));
            }

            logger.debug(logger.format("Asynchronous task #{} completed successfully", taskId));

        } catch (InterruptedException e) {
            logger.warn(logger.format("Asynchronous task #{} was interrupted", taskId), e);
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            logger.error(logger.format("Asynchronous task #{} failed", taskId), e);
        }
    }

    /**
     * Simulates a complex business process with multiple steps
     */
    private static void simulateComplexBusinessProcess() {
        logger.info("Starting complex business process simulation");

        try {
            logger.debug("Initializing business process components");

            // Simulate data collection step
            List<Map<String, Object>> data = collectProcessData();
            logger.info(logger.format("Collected {} data items for processing", data.size()));

            // Simulate validation step
            int validItems = validateProcessData(data);
            logger.info(logger.format("Validated {}/{} data items ({}%)",
                    validItems, data.size(), (validItems * 100 / data.size())));

            if (validItems < data.size() * 0.8) {  // Less than 80% valid
                logger.warn("Data quality below acceptable threshold");
            }

            // Simulate processing step
            processBusinessData(data, validItems);

            // Simulate completion
            logger.success("Complex business process completed successfully");

        } catch (Exception e) {
            logger.severe("Critical failure in business process", e);
        }
    }

    /**
     * Collects data for business processing (simulated)
     */
    private static List<Map<String, Object>> collectProcessData() {
        logger.debug("Beginning data collection phase");

        List<Map<String, Object>> dataItems = new ArrayList<>();
        int itemCount = random.nextInt(50) + 50;

        for (int i = 0; i < itemCount; i++) {
            try {
                // Simulate data retrieval with occasional errors
                if (random.nextInt(20) < 1) {  // 5% chance of error
                    throw new RuntimeException("Data source unavailable");
                }

                Map<String, Object> item = new HashMap<>();
                item.put("id", "ITEM-" + (1000 + i));
                item.put("timestamp", System.currentTimeMillis());
                item.put("value", random.nextDouble() * 1000);
                item.put("status", random.nextBoolean() ? "VALID" : "PENDING");

                dataItems.add(item);

            } catch (Exception e) {
                logger.warn(logger.format("Failed to collect data item #{}", i), e);
            }
        }

        logger.debug(logger.format("Data collection completed with {} items", dataItems.size()));
        return dataItems;
    }

    /**
     * Validates business process data (simulated)
     */
    private static int validateProcessData(List<Map<String, Object>> data) {
        logger.debug("Beginning data validation phase");

        int validCount = 0;

        for (int i = 0; i < data.size(); i++) {
            Map<String, Object> item = data.get(i);

            try {
                // Simulate validation logic
                boolean isValid = "VALID".equals(item.get("status")) ||
                        (double) item.get("value") > 100.0;

                if (isValid) {
                    item.put("status", "VALID");
                    validCount++;
                } else {
                    item.put("status", "INVALID");
                    logger.debug(logger.format("Item {} failed validation: value too low", item.get("id")));
                }

            } catch (Exception e) {
                logger.error(logger.format("Error validating item {}", item.get("id")), e);
                item.put("status", "ERROR");
            }
        }

        logger.debug("Data validation phase completed");
        return validCount;
    }

    /**
     * Processes business data (simulated)
     */
    private static void processBusinessData(List<Map<String, Object>> data, int validItems) {
        logger.debug("Beginning data processing phase");

        int processedCount = 0;
        double totalValue = 0.0;

        for (Map<String, Object> item : data) {
            if (!"VALID".equals(item.get("status"))) {
                continue;
            }

            try {
                // Simulate processing
                Thread.sleep(random.nextInt(50) + 10);

                // Simulate process error
                if (random.nextInt(20) < 1) {  // 5% chance of processing error
                    throw new RuntimeException("Processing engine failure");
                }

                // Update item and track metrics
                totalValue += (double) item.get("value");
                item.put("processed", true);
                processedCount++;

            } catch (Exception e) {
                logger.error(logger.format("Failed to process item {}", item.get("id")), e);
                item.put("status", "PROCESS_FAILED");
            }
        }

        double successRate = (double) processedCount / validItems * 100;
        if (successRate < 95) {
            logger.warn(logger.format("Process success rate below target: {:.2f}%", successRate));
        }

        logger.info(logger.format(
                "Processing complete: {}/{} valid items processed, total value: {:.2f}",
                processedCount, validItems, totalValue));
    }
}