package io.joshuasalcedo.library.logging.core;

import io.joshuasalcedo.pretty.core.model.time.TimeStampFactory;
import io.joshuasalcedo.pretty.core.properties.RGBColor;
import io.joshuasalcedo.pretty.core.theme.TerminalStyle;

/**
 * Enhanced log level enum with emojis, colors, and bold formatting for improved readability.
 * Integrated with TerminalStyle for consistent styling across the application.
 * Levels are clearly distinguished by purpose, value, and visual indicators.
 */
public enum LogLevel {
    // Diagnostic Levels - For development and troubleshooting
    TRACE(100, "🔍", "Detailed tracing information", TerminalStyle.TRACE),
    DEBUG(200, "🐞", "Debugging information", TerminalStyle.DEBUG),

    // Informational Levels - For regular operation information
    INFO(300, "ℹ️", "General information", TerminalStyle.INFO),
    SUCCESS(350, "✅", "Operation completed successfully", TerminalStyle.SUCCESS),

    // Notification Levels - For important but non-error situations
    NOTICE(500, "📢", "Notable event that might need attention", TerminalStyle.HIGHLIGHT),
    IMPORTANT(600, "❗", "Significant event requiring attention", TerminalStyle.IMPORTANT),

    // Warning Levels - For potential problems
    WARN(700, "⚠️", "Warning that might cause issues", TerminalStyle.WARNING),

    // Error Levels - For actual problems
    ERROR(800, "❌", "Error that affects operation", TerminalStyle.ERROR),
    SEVERE(900, "🚨", "Serious error that may cause system instability", TerminalStyle.CRITICAL_ERROR),
    FATAL(1000, "💀", "Critical error that will cause system failure", TerminalStyle.SECURITY_ALERT),

    // Special purpose levels
    STUB(50, "🚧", "Code under development", TerminalStyle.DEV_TODO),
    OFF(Integer.MAX_VALUE, "🚫", "Logging disabled", TerminalStyle.PLAIN);

    private final int value;
    private final String emoji;
    private final String description;
    private final TerminalStyle style;

    LogLevel(int value, String emoji, String description, TerminalStyle style) {
        this.value = value;
        this.emoji = emoji;
        this.description = description;
        this.style = style;
    }

    /**
     * Get the numerical value of this log level.
     * Higher values indicate higher severity.
     *
     * @return the numerical value
     */
    public int getValue() {
        return value;
    }

    /**
     * Get the emoji for this log level.
     *
     * @return the emoji character
     */
    public String getEmoji() {
        return emoji;
    }

    /**
     * Get the description of this log level.
     *
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Get the TerminalStyle associated with this log level.
     *
     * @return the TerminalStyle for this level
     */
    public TerminalStyle getStyle() {
        return style;
    }

    /**
     * Get the string representation of this log level.
     * Includes emoji, applies color formatting, and makes it bold.
     *
     * @return A colorized bold string with emoji in the format "emoji [LOGLEVEL]"
     */
    @Override
    public String toString() {
        String label = "[ " + name() + " ]";
      return style.apply(label);


    }

    /**
     * Get a formatted log string with emoji, color, and bold formatting.
     * Uses the associated TerminalStyle for consistent styling.
     *
     * @return A colorized bold string with emoji in the format "emoji [LOGLEVEL]"
     */
    public String toLogString() {
        String label = "[" + name() + "]";
        return style.apply(label+emoji);
    }

    /**
     * Format a log message with level prefix in Maven style.
     * Format: [LEVEL] message
     *
     * @param message The message to format
     * @return The formatted message with level prefix
     */
    public String formatLogMessage(String message) {
        if (message == null || message.isEmpty()) {
            return "";
        }

        String levelPrefix = "[" + name() + "]";
        return style.apply(levelPrefix) + " " + message;
    }

    /**
     * Format a log message with level prefix and emoji.
     * Format: [LEVEL] emoji message
     *
     * @param message The message to format
     * @return The formatted message with level prefix and emoji
     */
    public String formatLogMessageWithEmoji(String message) {
        if (message == null || message.isEmpty()) {
            return "";
        }

        String levelPrefix = "[" + name() + "]";
        return style.apply(levelPrefix + " " + emoji) + " " + message;
    }

    /**
     * Get a detailed description for this log level.
     *
     * @return A string containing the name, value, and description of this log level
     */
    public String getDetailedDescription() {
        return String.format("%s (value: %d): %s", name(), value, description);
    }

    /**
     * Find the log level with the specified numerical value.
     * If no exact match is found, returns the next higher level.
     *
     * @param value the numerical value of the log level
     * @return the corresponding LogLevel or next higher level
     */
    public static LogLevel fromValue(int value) {
        LogLevel result = OFF; // Default to highest level

        for (LogLevel level : values()) {
            if (level.value <= value && level.value > result.value) {
                result = level;
            }
        }

        return result;
    }

    /**
     * Convert a string to a LogLevel, case-insensitive.
     * Falls back to INFO if the level is not recognized.
     *
     * @param levelName the level name as string
     * @return the corresponding LogLevel
     */
    public static LogLevel fromString(String levelName) {
        if (levelName == null || levelName.trim().isEmpty()) {
            return INFO; // Default to INFO
        }

        try {
            return valueOf(levelName.toUpperCase());
        } catch (IllegalArgumentException e) {
            System.err.println("Unknown log level: " + levelName + ", using INFO");
            return INFO;
        }
    }

    /**
     * Get the color for this log level (foreground only, no background).
     * Uses the style's foreground color for consistency.
     */
    public RGBColor getColor() {
        return style.getForeground();
    }

    /**
     * Check if this log level is at or above the given minimum level.
     *
     * @param minimumLevel the minimum level to check against
     * @return true if this level is at or above the minimum level
     */
    public boolean isAtLeast(LogLevel minimumLevel) {
        return this.value >= minimumLevel.value;
    }
}