package io.joshuasalcedo.library.logging.exception;

import io.joshuasalcedo.pretty.core.theme.TerminalStyle;
import io.joshuasalcedo.pretty.core.utils.TextUtils;
import io.joshuasalcedo.pretty.core.utils.TerminalUtils;
import io.joshuasalcedo.library.logging.model.LogLevel;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * An exception formatter that creates beautifully formatted stack traces
 * with color, improved layout, and helpful debugging information,
 * integrated with the logging system.
 */
public class LoggingExceptionFormatter {
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    // Default styles mapped to log levels
    private Map<String, TerminalStyle> styles;
    private Map<String, TerminalStyle> packageHighlights;
    
    // Box drawing characters for layout
    private static final String BOX_H = "─";
    private static final String BOX_V = "│";
    private static final String BOX_TL = "┌";
    private static final String BOX_TR = "┐";
    private static final String BOX_BL = "└";
    private static final String BOX_BR = "┘";
    private static final String BOX_T_BRANCH = "┬";
    private static final String BOX_B_BRANCH = "┴";
    private static final String BOX_L_BRANCH = "├";
    private static final String BOX_R_BRANCH = "┤";
    private static final String BOX_CROSS = "┼";
    
    /**
     * Creates a new formatter with default styles
     */
    public LoggingExceptionFormatter() {
        styles = new HashMap<>();
        packageHighlights = new HashMap<>();
        
        // Set default styles based on log levels
        styles.put("header", TerminalStyle.ERROR);
        styles.put("message", TerminalStyle.ERROR);
        styles.put("type", TerminalStyle.ERROR);
        styles.put("stackTrace", TerminalStyle.SECONDARY);
        styles.put("className", TerminalStyle.EMPHASIS);
        styles.put("methodName", TerminalStyle.UI_VALUE);
        styles.put("fileName", TerminalStyle.TRACE);
        styles.put("lineNumber", TerminalStyle.TRACE);
        styles.put("causedBy", TerminalStyle.WARNING);
        styles.put("suppressed", TerminalStyle.INFO);
        styles.put("nativeMethod", TerminalStyle.DEV_NOTE);
        styles.put("moreFrames", TerminalStyle.TERTIARY);
        styles.put("packageName", TerminalStyle.UI_SUBHEADER);
        
        // Setup default package highlighting
        setupDefaultPackageHighlighting();
    }
    
    /**
     * Sets up default package highlighting rules
     */
    private void setupDefaultPackageHighlighting() {
        // JDK packages
        packageHighlights.put("java.", TerminalStyle.SECONDARY);
        packageHighlights.put("javax.", TerminalStyle.SECONDARY);
        packageHighlights.put("sun.", TerminalStyle.SECONDARY);
        
        // Common third-party libraries
        packageHighlights.put("org.springframework", TerminalStyle.TRACE);
        packageHighlights.put("org.hibernate", TerminalStyle.TRACE);
        packageHighlights.put("com.google", TerminalStyle.TRACE);
        packageHighlights.put("org.apache", TerminalStyle.TRACE);
        
        // Application code (customize this for your app)
        packageHighlights.put("io.joshuasalcedo", TerminalStyle.SUCCESS);
    }
    
    /**
     * Sets a style for a specific part of the stack trace
     *
     * @param part the part to style
     * @param style the style to use
     * @return this formatter for method chaining
     */
    public LoggingExceptionFormatter setStyle(String part, TerminalStyle style) {
        styles.put(part, style);
        return this;
    }
    
    /**
     * Adds a package highlighting rule
     *
     * @param packagePrefix the package prefix to highlight
     * @param style the style to use
     * @return this formatter for method chaining
     */
    public LoggingExceptionFormatter highlightPackage(String packagePrefix, TerminalStyle style) {
        packageHighlights.put(packagePrefix, style);
        return this;
    }
    
    /**
     * Maps a log level to appropriate styles for formatting exceptions
     *
     * @param level the log level
     * @return this formatter for method chaining
     */
    public LoggingExceptionFormatter useLogLevel(LogLevel level) {
        // Set styles based on log level
        setStyle("header", level.getStyle());
        setStyle("message", level.getStyle());
        setStyle("type", level.getStyle());
        
        // For error levels, make sure causedBy is also styled appropriately
        if (level.isAtLeast(LogLevel.ERROR)) {
            setStyle("causedBy", level.getStyle());
        }
        
        return this;
    }
    
    /**
     * Formats a throwable into a beautifully styled string
     *
     * @param throwable the throwable to format
     * @return a formatted string
     */
    public String format(Throwable throwable) {
        if (throwable == null) {
            return "";
        }
        
        // If terminal doesn't support ANSI colors, return a simpler format
        if (!TerminalUtils.isAnsiSupported()) {
            return formatSimple(throwable);
        }
        
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(output);
        
        printBeautifulStackTrace(throwable, out);
        
        return output.toString();
    }
    
    /**
     * Prints a beautifully formatted stack trace for an exception
     *
     * @param e The exception to print
     * @param out The print stream to write to
     */
    public void printBeautifulStackTrace(Throwable e, PrintStream out) {
        if (e == null) {
            out.println(styles.get("error").apply("No exception provided"));
            return;
        }

        // Print the main exception header
        out.println(styles.get("header").apply(" EXCEPTION "));
        out.println(styles.get("type").apply("╭─ Type: ") + styles.get("type").apply(e.getClass().getName()));
        out.println(styles.get("message").apply("╰─ Message: ") + styles.get("message").apply(e.getMessage()));

        // Print stack trace
        out.println(styles.get("stackTrace").apply("\n" + BOX_TL + "─ STACK TRACE ") + 
                    styles.get("stackTrace").apply(BOX_H.repeat(45)));

        StackTraceElement[] stackTrace = e.getStackTrace();
        printStackElements(stackTrace, e.getClass().getName(), out);

        // Handle nested exceptions (causes)
        int causeDepth = 1;
        Throwable cause = e.getCause();
        while (cause != null && causeDepth < 5) { // Limit depth to avoid excessive output
            out.println(styles.get("causedBy").apply("\n" + BOX_L_BRANCH + "─ CAUSED BY: ") + 
                        styles.get("type").apply(cause.getClass().getName()));
            out.println(styles.get("causedBy").apply(BOX_V + "  ") + 
                        styles.get("message").apply(cause.getMessage()));
            out.println(styles.get("causedBy").apply(BOX_V));

            // Print cause stack trace
            printStackElements(cause.getStackTrace(), cause.getClass().getName(), out);

            // Move to next cause
            cause = cause.getCause();
            causeDepth++;
        }

        // If we hit the depth limit but there's still more causes
        if (cause != null) {
            out.println(styles.get("causedBy").apply("\n" + BOX_L_BRANCH + "─ Additional nested causes omitted..."));
        }

        out.println(styles.get("stackTrace").apply(BOX_BL + BOX_H.repeat(67)));
    }
    
    /**
     * Helper method to print stack trace elements with proper formatting and highlighting
     */
    private void printStackElements(StackTraceElement[] stackTrace, String exceptionClassName, PrintStream out) {
        if (stackTrace == null || stackTrace.length == 0) {
            out.println(styles.get("stackTrace").apply(BOX_V + "  No stack trace available"));
            return;
        }
        
        // Group frames by package for better organization
        String currentPackage = "";
        boolean isFirstFrame = true;
        
        // Limit number of frames to show
        int maxFramesToShow = Math.min(stackTrace.length, 20);
        
        for (int i = 0; i < maxFramesToShow; i++) {
            StackTraceElement element = stackTrace[i];
            String className = element.getClassName();
            String packageName = className.contains(".") ?
                    className.substring(0, className.lastIndexOf('.')) : "";
            
            // Add package separator if we're entering a new package
            if (!packageName.equals(currentPackage)) {
                if (!isFirstFrame) {
                    out.println(styles.get("packageName").apply(BOX_V));
                }
                out.println(styles.get("packageName").apply(BOX_V + "  package " + packageName));
                out.println(styles.get("packageName").apply(BOX_V));
                currentPackage = packageName;
            }
            
            // Format the frame
            String methodName = element.getMethodName();
            String fileName = element.getFileName();
            int lineNumber = element.getLineNumber();
            
            // Determine style based on package
            TerminalStyle classStyle = getStyleForClass(className, exceptionClassName);
            
            // Print the frame with line prefix
            StringBuilder frameBuilder = new StringBuilder();
            frameBuilder.append(styles.get("stackTrace").apply(BOX_V + "  "));
            
            // Add frame number
            frameBuilder.append(styles.get("stackTrace").apply(String.format("%2d", i))).append(": ");
            
            // Build the class and method part
            String shortClassName = className.substring(className.lastIndexOf('.') + 1);
            frameBuilder.append(classStyle.apply(shortClassName))
                       .append(styles.get("stackTrace").apply("."))
                       .append(styles.get("methodName").apply(methodName));
            
            // Add source location
            frameBuilder.append(styles.get("stackTrace").apply(" ("));
            if (element.isNativeMethod()) {
                frameBuilder.append(styles.get("nativeMethod").apply("Native Method"));
            } else if (fileName == null) {
                frameBuilder.append(styles.get("fileName").apply("Unknown Source"));
            } else {
                frameBuilder.append(styles.get("fileName").apply(fileName));
                if (lineNumber >= 0) {
                    frameBuilder.append(styles.get("stackTrace").apply(":"))
                               .append(styles.get("lineNumber").apply(String.valueOf(lineNumber)));
                }
            }
            frameBuilder.append(styles.get("stackTrace").apply(")"));
            
            out.println(frameBuilder.toString());
            
            isFirstFrame = false;
        }
        
        // If we have a long stack trace, add a note about trimmed frames
        if (stackTrace.length > maxFramesToShow) {
            out.println(styles.get("stackTrace").apply(BOX_V));
            out.println(styles.get("moreFrames").apply(BOX_V + "  ... " + (stackTrace.length - maxFramesToShow) +
                    " more frames (showing first " + maxFramesToShow + ")"));
        }
    }
    
    /**
     * Determines the appropriate style for a class based on its package
     */
    private TerminalStyle getStyleForClass(String className, String exceptionClassName) {
        // If it's the exception class itself, use the exception style
        if (className.equals(exceptionClassName)) {
            return styles.get("type");
        }
        
        // Check package prefixes
        for (Map.Entry<String, TerminalStyle> entry : packageHighlights.entrySet()) {
            if (className.startsWith(entry.getKey())) {
                return entry.getValue();
            }
        }
        
        // Default to className style if no match
        return styles.get("className");
    }
    
    /**
     * Formats a throwable in a simple way, without colors
     */
    private String formatSimple(Throwable throwable) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(output);
        
        out.println("EXCEPTION: " + throwable.getClass().getName());
        out.println("MESSAGE: " + throwable.getMessage());
        out.println("\nSTACK TRACE:");
        
        StackTraceElement[] stackTrace = throwable.getStackTrace();
        for (int i = 0; i < stackTrace.length; i++) {
            out.println("  at " + stackTrace[i].toString());
        }
        
        Throwable cause = throwable.getCause();
        if (cause != null) {
            out.println("\nCAUSED BY: " + cause.getClass().getName());
            out.println("MESSAGE: " + cause.getMessage());
            out.println("\nSTACK TRACE:");
            
            StackTraceElement[] causeTrace = cause.getStackTrace();
            for (int i = 0; i < causeTrace.length; i++) {
                out.println("  at " + causeTrace[i].toString());
            }
        }
        
        return output.toString();
    }
    
    /**
     * Prints a comprehensive exception report with runtime context and suggested solutions
     */
    public void printExceptionReport(Throwable e, PrintStream out, Map<String, Object> additionalContext) {
        if (e == null || out == null) return;
        
        // Get current time for the report header
        String timestamp = LocalDateTime.now().format(TIME_FORMATTER);
        
        // Print report header
        out.println(styles.get("header").apply(" EXCEPTION REPORT " + timestamp + " "));
        out.println();
        
        // Print basic exception info
        out.println(styles.get("type").apply("Exception Type: ") + e.getClass().getName());
        out.println(styles.get("message").apply("Message: ") + e.getMessage());
        out.println();
        
        // Print runtime environment
        printSubsection(out, "Environment Information");
        printKeyValue(out, "Java Version", System.getProperty("java.version"));
        printKeyValue(out, "OS Name", System.getProperty("os.name"));
        printKeyValue(out, "OS Version", System.getProperty("os.version"));
        printKeyValue(out, "OS Architecture", System.getProperty("os.arch"));
        printKeyValue(out, "User Directory", System.getProperty("user.dir"));
        out.println();
        
        // Print additional context if provided
        if (additionalContext != null && !additionalContext.isEmpty()) {
            printSubsection(out, "Additional Context");
            for (Map.Entry<String, Object> entry : additionalContext.entrySet()) {
                printKeyValue(out, entry.getKey(), String.valueOf(entry.getValue()));
            }
            out.println();
        }
        
        // Print the structured stack trace
        printSubsection(out, "Stack Trace");
        printBeautifulStackTrace(e, out);
        
        // Print potential solutions or actions based on exception type
        printPotentialSolutions(e, out);
    }
    
    /**
     * Prints a subsection header in the report
     */
    private void printSubsection(PrintStream out, String title) {
        out.println(styles.get("header").apply(BOX_TL + "─ " + title + " ") + 
                    styles.get("stackTrace").apply(BOX_H.repeat(50 - title.length())));
    }
    
    /**
     * Prints a key-value pair in the report
     */
    private void printKeyValue(PrintStream out, String key, String value) {
        out.println(styles.get("stackTrace").apply(BOX_V + "  ") + 
                    TerminalStyle.UI_LABEL.apply(key + ": ") + 
                    TerminalStyle.UI_VALUE.apply(value));
    }
    
    /**
     * Prints a bullet list in the report
     */
    private void printBulletList(PrintStream out, List<String> items) {
        for (String item : items) {
            out.println(styles.get("stackTrace").apply(BOX_V + "  • ") + 
                         TerminalStyle.UI_VALUE.apply(item));
        }
    }
    
    /**
     * Provides potential solutions or next steps based on exception type
     */
    private void printPotentialSolutions(Throwable e, PrintStream out) {
        printSubsection(out, "Suggested Actions");
        
        // Custom suggestions based on common exception types
        String exceptionName = e.getClass().getName();
        String message = e.getMessage() != null ? e.getMessage().toLowerCase() : "";
        
        if (exceptionName.contains("NullPointerException")) {
            printBulletList(out, Arrays.asList(
                    "Check if objects are properly initialized before use",
                    "Add null checks for method parameters",
                    "Verify if external service responses are properly validated"
            ));
        } else if (exceptionName.contains("ClassCastException")) {
            printBulletList(out, Arrays.asList(
                    "Verify object types before casting",
                    "Use instanceof operator to check types",
                    "Review generic type parameters"
            ));
        } else if (exceptionName.contains("IndexOutOfBoundsException")) {
            printBulletList(out, Arrays.asList(
                    "Validate array or list indices before access",
                    "Check collection size before iteration",
                    "Ensure loop conditions are correct"
            ));
        } else if (exceptionName.contains("FileNotFoundException")) {
            printBulletList(out, Arrays.asList(
                    "Verify file path is correct",
                    "Check file permissions",
                    "Ensure the file exists"
            ));
        } else if (exceptionName.contains("IOException")) {
            printBulletList(out, Arrays.asList(
                    "Check network connectivity",
                    "Verify file system permissions",
                    "Ensure resources are properly closed"
            ));
        } else if (exceptionName.contains("SQLException")) {
            printBulletList(out, Arrays.asList(
                    "Verify database connection settings",
                    "Check SQL syntax",
                    "Ensure database schema is compatible",
                    "Validate transaction handling"
            ));
        } else if (exceptionName.contains("IllegalArgumentException")) {
            printBulletList(out, Arrays.asList(
                    "Validate method parameters",
                    "Check parameter constraints",
                    "Review API documentation for correct usage"
            ));
        } else if (message.contains("connection") || message.contains("timeout")) {
            printBulletList(out, Arrays.asList(
                    "Check network connectivity",
                    "Verify service endpoint is available",
                    "Increase timeout settings if appropriate",
                    "Implement retry logic with exponential backoff"
            ));
        } else {
            // Generic suggestions
            printBulletList(out, Arrays.asList(
                    "Check the application logs for more details",
                    "Review code around the exception source",
                    "Verify environment configuration",
                    "Add diagnostic logging around the problematic area"
            ));
        }
    }
    
    /**
     * Creates a new builder for fluent configuration
     */
    public static Builder builder() {
        return new Builder();
    }
    
    /**
     * Builder class for fluent configuration
     */
    public static class Builder {
        private final LoggingExceptionFormatter formatter;
        
        private Builder() {
            formatter = new LoggingExceptionFormatter();
        }
        
        public Builder style(String part, TerminalStyle style) {
            formatter.setStyle(part, style);
            return this;
        }
        
        public Builder highlightPackage(String packagePrefix, TerminalStyle style) {
            formatter.highlightPackage(packagePrefix, style);
            return this;
        }
        
        public Builder logLevel(LogLevel level) {
            formatter.useLogLevel(level);
            return this;
        }
        
        public LoggingExceptionFormatter build() {
            return formatter;
        }
    }
}