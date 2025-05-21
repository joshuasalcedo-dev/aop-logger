package io.joshuasalcedo.library.logging.aspect;


import io.joshuasalcedo.library.logging.model.LogLevel;

import io.joshuasalcedo.library.logging.model.Logger;
import io.joshuasalcedo.library.logging.model.LoggerFactory;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Aspect that automatically logs method execution in a Maven-style format.
 * Each log line is prefixed with the log level in brackets.
 */
@Aspect
public class LoggingAspect {

    // Logger for the aspect
    private static final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);

    // Set of packages and classes to exclude from logging
    private static final Set<String> EXCLUDED_PACKAGES = new HashSet<>(Arrays.asList(
            "io.joshuasalcedo.library.logging.model",
            "io.joshuasalcedo.library.logging.console",
            "io.joshuasalcedo.library.logging.aspect",
            "io.joshuasalcedo.pretty.core"
    ));

    // Set of method names to exclude from logging
    private static final Set<String> EXCLUDED_METHODS = new HashSet<>(Arrays.asList(
            "toString", "hashCode", "equals", "logFormat", "log", "doLog",
            "getThrowable", "getLogLevel", "message", "isEnabled", "getTimeStamp",
            "getTimeStampString", "format", "toLogString", "getColor", "getDescription"
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
    @Pointcut("within(io.joshuasalcedo.library.logging.model.*) || " +
            "within(io.joshuasalcedo.library.logging.console.*) || " +
            "within(io.joshuasalcedo.pretty.core..*)")
    public void loggingFrameworkMethods() {}

    /**
     * Combined pointcut that includes application methods but excludes logging framework
     */
    @Pointcut("allMethods() && !loggingFrameworkMethods()")
    public void trackedMethods() {}

    /**
     * Around advice to log method execution
     */
    @Around("trackedMethods()")
    public Object logMethodExecution(ProceedingJoinPoint joinPoint) throws Throwable {
        // Skip if this is a method we don't want to log
        if (shouldSkipLogging(joinPoint)) {
            return joinPoint.proceed();
        }

        // Get class and method info
        String className = getClassName(joinPoint);
        String methodName = joinPoint.getSignature().getName();
        String args = formatArguments(joinPoint.getArgs());

        // Log method entry
        logger.debug(className + "." + methodName + "(" + args + ")");

        long startTime = System.currentTimeMillis();

        try {
            // Execute the method
            Object result = joinPoint.proceed();

            // Calculate execution time
            long executionTime = System.currentTimeMillis() - startTime;

            // Log method exit with result
            logger.debug(className + "." + methodName + " completed in " + executionTime + "ms");

            return result;

        } catch (Throwable ex) {
            // Log the exception with stack trace
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            ex.printStackTrace(pw);

            logger.error(className + "." + methodName + " threw " +
                    ex.getClass().getName() + ": " + ex.getMessage(), ex);

            // Rethrow the exception
            throw ex;
        }
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

        // Skip toString, equals, hashCode methods from any class
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
                // Truncate very long string arguments
                String argStr = args[i].toString();
                if (argStr.length() > 50) {
                    sb.append(argStr.substring(0, 47)).append("...");
                } else {
                    sb.append(argStr);
                }
            }
        }

        return sb.toString();
    }
}