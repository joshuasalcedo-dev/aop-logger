package io.joshuasalcedo.library.logging.model;

/**
 * Implementation of Log that includes source class information.
 * This makes the logs more informative and error messages clickable in IDEs.
 */
public class ClassAwareLogImpl extends LogImpl {
    private final Class<?> sourceClass;

    /**
     * Create a new class-aware log entry.
     *
     * @param level the log level
     * @param message the log message
     * @param sourceClass the source class
     */
    public ClassAwareLogImpl(LogLevel level, String message, Class<?> sourceClass) {
        super(level, message);
        this.sourceClass = sourceClass;
    }

    /**
     * Create a new class-aware log entry with a throwable.
     *
     * @param level the log level
     * @param message the log message
     * @param throwable the throwable
     * @param sourceClass the source class
     */
    public ClassAwareLogImpl(LogLevel level, String message, Throwable throwable, Class<?> sourceClass) {
        super(level, message, throwable);
        this.sourceClass = sourceClass;
    }

    /**
     * Get the source class for this log entry.
     *
     * @return the source class
     */
    public Class<?> getSourceClass() {
        return sourceClass;
    }

    /**
     * Override the log format to include the source class.
     * The class name is formatted as a simple name to keep logs clean.
     * The full class name is included as a comment that IDEs can recognize and make clickable.
     */
    @Override
    public String logFormat() {
        String baseFormat = super.logFormat();
        String className = sourceClass.getSimpleName();
        String fullClassName = sourceClass.getName();

        // Format the class name in a way that IDEs like IntelliJ and Eclipse will recognize as clickable
        // The format "at package.Class.method(Class.java:line)" is recognized by most IDEs
        return baseFormat + " at " + fullClassName + "(" + className + ".java:0)";
    }
}