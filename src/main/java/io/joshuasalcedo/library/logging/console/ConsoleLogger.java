package io.joshuasalcedo.library.logging.console;

import io.joshuasalcedo.library.logging.model.AbstractLogger;
import io.joshuasalcedo.library.logging.model.Log;
import io.joshuasalcedo.library.logging.model.LogLevel;
import io.joshuasalcedo.pretty.core.model.stream.PrettyPrintStream;
import io.joshuasalcedo.pretty.core.theme.TerminalStyle;

import java.util.HashMap;
import java.util.Map;

/**
 * Maven-style console logger implementation that outputs formatted logs to the console.
 * Each line is prefixed with the log level in brackets.
 */
public class ConsoleLogger extends AbstractLogger {

    private static final PrettyPrintStream out = new PrettyPrintStream(System.out);
    private static final PrettyPrintStream err = new PrettyPrintStream(System.err);

    // Map to store terminal styles for each log level
    private static final Map<LogLevel, TerminalStyle> LEVEL_STYLES = new HashMap<>();

    static {
        // Initialize styles for each log level
        LEVEL_STYLES.put(LogLevel.TRACE, TerminalStyle.TRACE);
        LEVEL_STYLES.put(LogLevel.DEBUG, TerminalStyle.DEBUG);
        LEVEL_STYLES.put(LogLevel.INFO, TerminalStyle.INFO);
        LEVEL_STYLES.put(LogLevel.SUCCESS, TerminalStyle.SUCCESS);
        LEVEL_STYLES.put(LogLevel.NOTICE, TerminalStyle.DEV_NOTE);
        LEVEL_STYLES.put(LogLevel.IMPORTANT, TerminalStyle.IMPORTANT);
        LEVEL_STYLES.put(LogLevel.WARN, TerminalStyle.WARNING);
        LEVEL_STYLES.put(LogLevel.ERROR, TerminalStyle.ERROR);
        LEVEL_STYLES.put(LogLevel.SEVERE, TerminalStyle.CRITICAL_ERROR);
        LEVEL_STYLES.put(LogLevel.FATAL, TerminalStyle.SECURITY_ALERT);
        LEVEL_STYLES.put(LogLevel.STUB, TerminalStyle.DEV_TODO);
    }

    @Override
    protected void doLog(Log logEntry) {
        // Determine which stream to use based on the log level
        PrettyPrintStream stream = logEntry.getLogLevel().isAtLeast(LogLevel.ERROR) ? err : out;

        // Get the style for this log level
        TerminalStyle style = LEVEL_STYLES.getOrDefault(logEntry.getLogLevel(), TerminalStyle.PLAIN);

        // Get the log level name enclosed in brackets
        String levelPrefix = "[" + logEntry.getLogLevel().name() + "]";

        // Get the message (which may contain multiple lines)
        String message = logEntry.message();

        // Split the message into lines
        String[] lines = message.split("\\r?\\n");

        // Print each line with the level prefix
        for (String line : lines) {
            stream.style(style).print(levelPrefix + " ");
            stream.reset().println(line);
        }

        // Handle throwable if present
        Throwable throwable = logEntry.getThrowable();
        if (throwable != null) {
            printThrowable(stream, throwable, logEntry.getLogLevel());
        }
    }

    /**
     * Prints a throwable with stack trace, maintaining the level prefix on each line
     */
    private void printThrowable(PrettyPrintStream stream, Throwable throwable, LogLevel level) {
        TerminalStyle style = LEVEL_STYLES.getOrDefault(level, TerminalStyle.PLAIN);
        String levelPrefix = "[" + level.name() + "]";

        // Print the exception class and message
        stream.style(style).print(levelPrefix + " ");
        stream.style(TerminalStyle.ERROR).println(throwable.getClass().getName() + ": " + throwable.getMessage());

        // Print stack trace with level prefix on each line
        for (StackTraceElement element : throwable.getStackTrace()) {
            stream.style(style).print(levelPrefix + " ");
            stream.print("    at " + element.toString());
            stream.println();
        }

        // Print cause if present
        Throwable cause = throwable.getCause();
        if (cause != null) {
            stream.style(style).print(levelPrefix + " ");
            stream.println("Caused by:");
            printThrowable(stream, cause, level);
        }
    }

    /**
     * Factory method to create a console logger
     */
    public static ConsoleLogger create() {
        return new ConsoleLogger();
    }
}