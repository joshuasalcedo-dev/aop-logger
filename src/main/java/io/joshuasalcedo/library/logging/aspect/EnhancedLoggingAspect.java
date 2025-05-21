package io.joshuasalcedo.library.logging.aspect;

import io.joshuasalcedo.library.logging.core.LogLevel;
import io.joshuasalcedo.library.logging.core.Logger;
import io.joshuasalcedo.library.logging.exception.ContextAwareException;
import io.joshuasalcedo.library.logging.exception.ExceptionFactory;
import io.joshuasalcedo.library.logging.factory.LoggerFactory;
import io.joshuasalcedo.library.logging.output.console.ConsoleLogger;
import io.joshuasalcedo.library.logging.output.console.EnhancedConsoleLogger;
import io.joshuasalcedo.pretty.core.model.error.EnhancedThrowable;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.reflect.Method;
import java.util.*;

/**
 * Enhanced aspect that automatically logs method execution with rich formatting.
 * Automatically enhances exceptions that are thrown with detailed context information.
 */
@Aspect
public class EnhancedLoggingAspect {

    // IMPORTANT: Fix circular dependency - Use lazy initialization
    private static class LoggerHolder {
        // This is only initialized when first accessed
        static final Logger LOGGER = initLogger();

        private static Logger initLogger() {
            try {
                return LoggerFactory.getLogger(EnhancedLoggingAspect.class);
            } catch (Throwable t) {
                // Fallback to direct instance if factory fails during initialization
                return EnhancedConsoleLogger.create(EnhancedLoggingAspect.class);
            }
        }
    }

    // Access the logger using this method to avoid static initialization issues
    private static Logger getLogger() {
        return LoggerHolder.LOGGER;
    }

    // Set of packages and classes to exclude from logging
    private static final Set<String> EXCLUDED_PACKAGES = new HashSet<>(Arrays.asList(
            "io.joshuasalcedo.library.logging.core",
            "io.joshuasalcedo.library.logging.output.console",
            "io.joshuasalcedo.library.logging.aspect",
            "io.joshuasalcedo.library.logging.factory",
            "io.joshuasalcedo.pretty.api.model.error.EnhancedThrowable"
    ));

    // Set of method names to exclude from logging
    private static final Set<String> EXCLUDED_METHODS = new HashSet<>(Arrays.asList(
            "toString", "hashCode", "equals", "logFormat", "log", "doLog",
            "getThrowable", "getLogLevel", "message", "isEnabled", "getTimeStamp",
            "getTimeStampString", "format", "toLogString", "getColor", "getDescription",
            "printStackTrace", "printEnhancedStackTrace", "printExceptionReport"
    ));

    /**
     * Pointcut that captures all methods in the application
     * while excluding logging framework internals
     */
    @Pointcut("execution(* *(..)) && !within(io.joshuasalcedo.library.logging.aspect.*)")
    public void allMethods() {}

    /**
     * Pointcut for logging framework methods to exclude
     */
    @Pointcut("within(io.joshuasalcedo.library.logging.core..*) || " +
            "within(io.joshuasalcedo.library.logging.output.console..*) || " +
            "within(io.joshuasalcedo.library.logging..*) || " +
            "within(io.joshuasalcedo.pretty.core..*)")
    public void loggingFrameworkMethods() {}

    /**
     * Combined pointcut that includes application methods but excludes logging framework
     */
    @Pointcut("allMethods() && !loggingFrameworkMethods()")
    public void trackedMethods() {}

    /**
     * Around advice to log method execution and enhance exceptions
     */
    @Around("trackedMethods()")
    public Object logMethodExecution(ProceedingJoinPoint joinPoint) throws Throwable {
        // Skip if this is a method we don't want to log
        if (shouldSkipLogging(joinPoint)) {
            return joinPoint.proceed();
        }

        // Get method metadata
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        String className = getClassName(joinPoint);
        String methodName = method.getName();
        String[] parameterNames = signature.getParameterNames();
        Object[] args = joinPoint.getArgs();

        // Collect method context information
        Map<String, Object> methodContext = new HashMap<>();
        methodContext.put("class", className);
        methodContext.put("method", methodName);
        methodContext.put("timestamp", System.currentTimeMillis());
        methodContext.put("threadId", Thread.currentThread().getId());
        methodContext.put("threadName", Thread.currentThread().getName());

        // Add parameter values to context if available
        if (parameterNames != null && args != null && parameterNames.length == args.length) {
            for (int i = 0; i < parameterNames.length; i++) {
                if (args[i] != null && !isLargeOrSensitiveObject(args[i])) {
                    methodContext.put("param." + parameterNames[i], args[i]);
                }
            }
        }

        // Log method entry
        String formattedArgs = formatArguments(args);
        getLogger().debug(className + "." + methodName + "(" + formattedArgs + ")");

        long startTime = System.currentTimeMillis();

        try {
            // Execute the method
            Object result = joinPoint.proceed();

            // Calculate execution time
            long executionTime = System.currentTimeMillis() - startTime;
            methodContext.put("executionTime", executionTime);

            // Log method exit with result and execution time
            String resultSummary = formatResult(result);
            getLogger().debug(className + "." + methodName + " completed in " + executionTime + "ms" +
                    (resultSummary != null ? " with result: " + resultSummary : ""));

            return result;

        } catch (Throwable ex) {
            // Calculate execution time until exception
            long executionTime = System.currentTimeMillis() - startTime;
            methodContext.put("executionTime", executionTime);
            methodContext.put("failurePoint", className + "." + methodName);

            // If not already enhanced, wrap it in a context-aware exception
            Throwable enhancedEx = enhanceException(ex, methodContext);

            // Log the exception with our enhanced formatter
            logException(enhancedEx, className, methodName, methodContext);

            // Rethrow the enhanced exception
            throw enhancedEx;
        }
    }

    /**
     * Enhances an exception with contextual information if it's not already enhanced.
     *
     * @param ex the exception to enhance
     * @param methodContext the method context information
     * @return the enhanced exception
     */
    private Throwable enhanceException(Throwable ex, Map<String, Object> methodContext) {
        // If it's already a context-aware throwable, just add our additional context
        if (ex instanceof ContextAwareException) {
            ContextAwareException contextEx = (ContextAwareException) ex;
            contextEx.withContext(methodContext);
            return contextEx;
        }

        // If it's an enhanced throwable but not context-aware, wrap it
        if (ex instanceof EnhancedThrowable) {
            return ExceptionFactory.wrap(ex, LogLevel.ERROR, methodContext);
        }

        // For standard exceptions, create the appropriate wrapped exception
        LogLevel level = determineSeverityLevel(ex);
        return ExceptionFactory.wrap(ex, level, methodContext);
    }

    /**
     * Logs an exception using the EnhancedConsoleLogger if available.
     */
    private void logException(Throwable ex, String className, String methodName, Map<String, Object> methodContext) {
        String message = className + "." + methodName + " threw " + ex.getClass().getName() + ": " + ex.getMessage();
        Logger logger = getLogger();

        if (logger instanceof EnhancedConsoleLogger) {
            // For enhanced loggers, use the context-rich reporting
            EnhancedConsoleLogger enhancedLogger = (EnhancedConsoleLogger) logger;

            LogLevel level = determineSeverityLevel(ex);
            enhancedLogger.logExceptionReport(level, ex, methodContext);
        } else {
            // Fallback to standard error logging
            logger.error(message, ex);
        }
    }

    /**
     * Determines the severity level based on exception type.
     */
    private LogLevel determineSeverityLevel(Throwable ex) {
        String exName = ex.getClass().getName().toLowerCase();

        // Critical exceptions
        if (exName.contains("outofmemory") ||
                exName.contains("stackoverflow") ||
                exName.contains("linkageerror") ||
                exName.contains("securityexception")) {
            return LogLevel.FATAL;
        }

        // Serious exceptions
        if (exName.contains("sql") ||
                exName.contains("io") ||
                exName.contains("file") ||
                exName.contains("network") ||
                exName.contains("timeout")) {
            return LogLevel.SEVERE;
        }

        // Validation/precondition exceptions
        if (exName.contains("illegal") ||
                exName.contains("invalid") ||
                exName.contains("validation") ||
                exName.contains("argument") ||
                exName.contains("state")) {
            return LogLevel.ERROR;
        }

        // Default to ERROR level
        return LogLevel.ERROR;
    }

    /**
     * Determines if a particular method call should be skipped in logging
     */
    private boolean shouldSkipLogging(JoinPoint joinPoint) {
        // Skip if it's an excluded method name
        String methodName = joinPoint.getSignature().getName();
        if (EXCLUDED_METHODS.contains(methodName)) {
            return true;
        }

        // Skip logging framework classes
        String className = joinPoint.getSignature().getDeclaringTypeName();
        for (String excludedPackage : EXCLUDED_PACKAGES) {
            if (className.startsWith(excludedPackage)) {
                return true;
            }
        }

        // Skip common Object methods
        if (isCommonObjectMethod(joinPoint)) {
            return true;
        }

        return false;
    }

    /**
     * Gets the class name safely (works for both instance and static methods)
     */
    private String getClassName(JoinPoint joinPoint) {
        // Try to get the target object
        Object target = joinPoint.getTarget();

        if (target != null) {
            // For instance methods, use the target's class
            return target.getClass().getSimpleName();
        } else {
            // For static methods, use the declaring type
            String fullClassName = joinPoint.getSignature().getDeclaringTypeName();
            int lastDotIndex = fullClassName.lastIndexOf('.');
            return lastDotIndex >= 0 ? fullClassName.substring(lastDotIndex + 1) : fullClassName;
        }
    }

    /**
     * Checks if the method is one of the common Object methods
     */
    private boolean isCommonObjectMethod(JoinPoint joinPoint) {
        if (!(joinPoint.getSignature() instanceof MethodSignature)) {
            return false;
        }

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String methodName = signature.getName();

        if (methodName.equals("toString") && signature.getParameterTypes().length == 0) {
            return true;
        }
        if (methodName.equals("hashCode") && signature.getParameterTypes().length == 0) {
            return true;
        }
        if (methodName.equals("equals") && signature.getParameterTypes().length == 1
                && signature.getParameterTypes()[0] == Object.class) {
            return true;
        }

        return false;
    }

    /**
     * Formats method arguments for logging
     */
    private String formatArguments(Object[] args) {
        if (args == null || args.length == 0) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < args.length; i++) {
            if (i > 0) {
                sb.append(", ");
            }

            // Safely handle null arguments
            if (args[i] == null) {
                sb.append("null");
            } else {
                // Skip large or sensitive objects
                if (isLargeOrSensitiveObject(args[i])) {
                    sb.append(args[i].getClass().getSimpleName()).append("@").append(Integer.toHexString(args[i].hashCode()));
                } else {
                    // Truncate very long string arguments
                    String argStr = args[i].toString();
                    if (argStr.length() > 50) {
                        sb.append(argStr.substring(0, 47)).append("...");
                    } else {
                        sb.append(argStr);
                    }
                }
            }
        }

        return sb.toString();
    }

    /**
     * Format method result for logging
     */
    private String formatResult(Object result) {
        if (result == null) {
            return null;
        }

        // Skip logging result value for certain return types
        if (isLargeOrSensitiveObject(result)) {
            return result.getClass().getSimpleName() + " instance";
        }

        // For other types, limit the string representation
        String resultStr = result.toString();
        if (resultStr.length() > 50) {
            return resultStr.substring(0, 47) + "...";
        }

        return resultStr;
    }

    /**
     * Checks if an object is large (like collections, arrays) or potentially sensitive (like user data)
     * which should not be fully logged
     */
    private boolean isLargeOrSensitiveObject(Object obj) {
        // Collections and arrays might be large
        if (obj instanceof Collection && ((Collection<?>) obj).size() > 5) {
            return true;
        }
        if (obj.getClass().isArray()) {
            return true; // Skip all arrays for simplicity
        }

        // Skip objects that might contain sensitive data - customize for your domain
        String className = obj.getClass().getName().toLowerCase();
        return className.contains("user") ||
                className.contains("password") ||
                className.contains("credential") ||
                className.contains("payment") ||
                className.contains("credit") ||
                className.contains("account");
    }
}