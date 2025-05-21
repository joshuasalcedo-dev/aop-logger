package io.joshuasalcedo.library.logging.core;

import io.joshuasalcedo.pretty.core.model.time.TimeStamp;
import io.joshuasalcedo.pretty.core.model.time.TimeStampFactory;

/**
 * Interface representing a loggable entry with various formatting options.
 * Provides methods to retrieve log components and format them consistently.
 */
public interface Log {
    // Instance methods
    String log();
    LogLevel getLogLevel();
    String log(Throwable throwable);
    String log(String message, Throwable throwable);
    String message();
    Throwable getThrowable();

    // Default methods
    default String logFormat() {
        return log() + "[ " + getTimeStampString() + " ] " + " { " + message() + "}";
    }

    default TimeStamp getTimeStamp() {
        return TimeStampFactory.createPrettyTimeStamp();
    }

    default String getTimeStampString() {
        return getTimeStamp().toString();
    }

    default boolean isEnabled(LogLevel threshold) {
        return getLogLevel().getValue() >= threshold.getValue();
    }

    // Static factory methods
    static Log trace(String message) {
        return of(LogLevel.TRACE, message);
    }

    static Log debug(String message) {
        return of(LogLevel.DEBUG, message);
    }

    static Log info(String message) {
        return of(LogLevel.INFO, message);
    }

    static Log success(String message) {
        return of(LogLevel.SUCCESS, message);
    }

    static Log notice(String message) {
        return of(LogLevel.NOTICE, message);
    }

    static Log important(String message) {
        return of(LogLevel.IMPORTANT, message);
    }

    static Log warn(String message) {
        return of(LogLevel.WARN, message);
    }

    static Log warn(String message, Throwable throwable) {
        return of(LogLevel.WARN, message, throwable);
    }

    static Log error(String message) {
        return of(LogLevel.ERROR, message);
    }

    static Log error(String message, Throwable throwable) {
        return of(LogLevel.ERROR, message, throwable);
    }

    static Log severe(String message) {
        return of(LogLevel.SEVERE, message);
    }

    static Log severe(String message, Throwable throwable) {
        return of(LogLevel.SEVERE, message, throwable);
    }

    static Log fatal(String message) {
        return of(LogLevel.FATAL, message);
    }

    static Log fatal(String message, Throwable throwable) {
        return of(LogLevel.FATAL, message, throwable);
    }

    static Log stub(String message) {
        return of(LogLevel.STUB, message);
    }

    // The core factory method that all others use
    static Log of(LogLevel level, String message) {
        return new LogImpl(level, message);
    }

    static Log of(LogLevel level, String message, Throwable throwable) {
        return new LogImpl(level, message, throwable);
    }
}