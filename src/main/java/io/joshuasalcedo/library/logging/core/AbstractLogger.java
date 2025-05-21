package io.joshuasalcedo.library.logging.core;

/**
 * Enhanced abstract logger implementation that is aware of the source class.
 * Provides common functionality for all logger implementations.
 */
public abstract class AbstractLogger implements Logger, ClassAwareLogger {
    private LogLevel threshold = LogLevel.INFO; // Default threshold
    private Class<?> sourceClass; // The class that is using this logger

    @Override
    public void setSourceClass(Class<?> sourceClass) {
        this.sourceClass = sourceClass;
    }

    @Override
    public Class<?> getSourceClass() {
        return sourceClass;
    }

    @Override
    public LogLevel getThreshold() {
        return threshold;
    }

    @Override
    public void setThreshold(LogLevel threshold) {
        this.threshold = threshold;
    }

    @Override
    public boolean isEnabled(LogLevel level) {
        return level.isAtLeast(threshold);
    }

    @Override
    public void log(LogLevel level, String message) {
        if (isEnabled(level)) {
            doLog(createLogEntry(level, message));
        }
    }

    @Override
    public void log(LogLevel level, String message, Throwable throwable) {
        if (isEnabled(level)) {
            doLog(createLogEntry(level, message, throwable));
        }
    }

    @Override
    public void log(Log logEntry) {
        if (isEnabled(logEntry.getLogLevel())) {
            doLog(logEntry);
        }
    }

    /**
     * Create a log entry with source class information if available.
     *
     * @param level the log level
     * @param message the log message
     * @return a log entry
     */
    protected Log createLogEntry(LogLevel level, String message) {
        if (sourceClass != null) {
            return new ClassAwareLogImpl(level, message, sourceClass);
        } else {
            return Log.of(level, message);
        }
    }

    /**
     * Create a log entry with source class information and throwable if available.
     *
     * @param level the log level
     * @param message the log message
     * @param throwable the throwable
     * @return a log entry
     */
    protected Log createLogEntry(LogLevel level, String message, Throwable throwable) {
        if (sourceClass != null) {
            return new ClassAwareLogImpl(level, message, throwable, sourceClass);
        } else {
            return Log.of(level, message, throwable);
        }
    }

    // Implementation of all the specific level methods using the general log method

    @Override
    public void trace(String message) {
        log(LogLevel.TRACE, message);
    }

    @Override
    public void debug(String message) {
        log(LogLevel.DEBUG, message);
    }

    @Override
    public void info(String message) {
        log(LogLevel.INFO, message);
    }

    @Override
    public void success(String message) {
        log(LogLevel.SUCCESS, message);
    }

    @Override
    public void notice(String message) {
        log(LogLevel.NOTICE, message);
    }

    @Override
    public void important(String message) {
        log(LogLevel.IMPORTANT, message);
    }

    @Override
    public void warn(String message) {
        log(LogLevel.WARN, message);
    }

    @Override
    public void warn(String message, Throwable throwable) {
        log(LogLevel.WARN, message, throwable);
    }

    @Override
    public void error(String message) {
        log(LogLevel.ERROR, message);
    }

    @Override
    public void error(String message, Throwable throwable) {
        log(LogLevel.ERROR, message, throwable);
    }

    @Override
    public void severe(String message) {
        log(LogLevel.SEVERE, message);
    }

    @Override
    public void severe(String message, Throwable throwable) {
        log(LogLevel.SEVERE, message, throwable);
    }

    @Override
    public void fatal(String message) {
        log(LogLevel.FATAL, message);
    }

    @Override
    public void fatal(String message, Throwable throwable) {
        log(LogLevel.FATAL, message, throwable);
    }

    @Override
    public void stub(String message) {
        log(LogLevel.STUB, message);
    }

    @Override
    public String format(String template, Object... args) {
        if (template == null) {
            return "null";
        }

        if (args == null || args.length == 0) {
            return template;
        }

        StringBuilder result = new StringBuilder();
        int argIndex = 0;
        int i = 0;

        while (i < template.length()) {
            int placeholderIndex = template.indexOf("{}", i);

            if (placeholderIndex == -1) {
                // No more placeholders, append the rest of the template
                result.append(template.substring(i));
                break;
            }

            // Append the text before the placeholder
            result.append(template, i, placeholderIndex);

            // Append the argument if available
            if (argIndex < args.length) {
                result.append(args[argIndex] == null ? "null" : args[argIndex].toString());
                argIndex++;
            } else {
                // No more arguments, keep the placeholder
                result.append("{}");
            }

            // Move past the placeholder
            i = placeholderIndex + 2;
        }

        return result.toString();
    }

    /**
     * Concrete loggers must implement this method to actually output the log entry
     *
     * @param logEntry the log entry to output
     */
    protected abstract void doLog(Log logEntry);
}