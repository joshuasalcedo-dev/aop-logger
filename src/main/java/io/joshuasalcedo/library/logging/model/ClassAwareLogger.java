package io.joshuasalcedo.library.logging.model;

/**
 * Interface for loggers that are aware of the source class they are logging for.
 * This helps with including class information in log messages and making them clickable in IDEs.
 */
public interface ClassAwareLogger {
    /**
     * Set the class that this logger is associated with.
     * 
     * @param sourceClass the class that is using this logger
     */
    void setSourceClass(Class<?> sourceClass);
    
    /**
     * Get the class that this logger is associated with.
     * 
     * @return the class that is using this logger
     */
    Class<?> getSourceClass();
}