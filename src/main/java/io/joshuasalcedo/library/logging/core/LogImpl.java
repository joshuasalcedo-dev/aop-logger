package io.joshuasalcedo.library.logging.core;

/**
 * Default implementation of the Log interface.
 */
public class LogImpl implements Log {
    private final LogLevel level;
    private final String message;
    private final Throwable throwable;

    public LogImpl(LogLevel level, String message) {
        this(level, message, null);
    }

    public LogImpl(LogLevel level, String message, Throwable throwable) {
        this.level = level;
        this.message = message;
        this.throwable = throwable;
    }

    @Override
    public String log() {
        return level.toLogString();
    }

    @Override
    public LogLevel getLogLevel() {
        return level;
    }

    @Override
    public String log(Throwable throwable) {
        return log(message(), throwable);
    }

    @Override
    public String log(String message, Throwable throwable) {
        // Here we can use the default logFormat() method from the Log interface
        // which will call our implementation methods
        String result = logFormat();

        // We don't need to format the throwable here since that's handled by the logger
        // Just return the formatted log message
        return result;
    }

    @Override
    public String message() {
        return message;
    }

    @Override
    public Throwable getThrowable() {
        return throwable;
    }

    // Note: We don't need to override logFormat() because we can use the default
    // implementation from the Log interface which calls our getTimeStampString(),
    // log(), and message() methods.
}