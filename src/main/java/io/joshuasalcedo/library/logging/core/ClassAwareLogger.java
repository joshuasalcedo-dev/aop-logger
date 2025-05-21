package io.joshuasalcedo.library.logging.core;

/**
 * Interface for loggers that are aware of the source class.
 * Extends the base Logger interface with class awareness functionality.
 */
public interface ClassAwareLogger extends Logger {
    /**
     * Set the source class for this logger
     *
     * @param sourceClass the class that is using this logger
     */
    void setSourceClass(Class<?> sourceClass);
    
    /**
     * Get the source class for this logger
     *
     * @return the class that is using this logger
     */
    Class<?> getSourceClass();
}