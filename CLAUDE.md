# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build/Test Commands
- Full build: `mvn clean install`
- Run tests: `mvn test`
- Run single test: `mvn test -Dtest=LogLevelTest`
- Run specific test method: `mvn test -Dtest=LogLevelTest#getValue`
- Run demo: `mvn exec:java@run-demo`
- Package: `mvn package`

## Code Style Guidelines
- Use Java 21 language features
- Follow JavaDoc conventions with comprehensive documentation
- Indent: 4 spaces
- Use interfaces for abstraction (see Logger, ClassAwareLogger patterns)
- Imports: Organize with standard Java packages first, then third-party libs
- Naming: Follow Java conventions with descriptive camelCase names
- Error handling: Use ContextAwareException with detailed context information
- Logging: Use appropriate LogLevel enum values for log messages
- Builder pattern preferred for complex object construction
- Method chaining supported for fluent APIs
- Apply inheritance judiciously (see AbstractLogger as base class)

    1. Package Structure Reorganization

  The current package structure needs better organization. Here's how to restructure:

  io.joshuasalcedo.library.logging
  ├── core         # Core logging interfaces and enums
  ├── output       # Output implementations (console, file)
  ├── format       # Formatting logic
  ├── aspect       # AOP components
  ├── exception    # Exception handling
  ├── context      # Context tracking
  ├── factory      # Factory methods
  ├── util         # Common utilities
  └── demo         # Examples/demos

  Benefits:
    - Clear separation of concerns
    - Logical grouping of related components
    - Better extensibility for new features

    2. Logger Implementation Hierarchy

  Current issue: ConsoleLogger and EnhancedConsoleLogger have duplicated code but aren't connected through inheritance.

  Before:
  // In model package                                                                                                                                                                                                                                                                  
  public interface Logger { ... }

  // In console package                                                                                                                                                                                                                                                                
  public class ConsoleLogger implements Logger { ... }
  public class EnhancedConsoleLogger implements Logger { ... }

  After:
  // In core package                                                                                                                                                                                                                                                                   
  public interface Logger { ... }
  public abstract class AbstractLogger implements Logger { ... }

  // In output.console package                                                                                                                                                                                                                                                         
  public class StandardConsoleLogger extends AbstractLogger { ... }
  public class EnhancedConsoleLogger extends StandardConsoleLogger { ... }

  Benefits:
    - Eliminates code duplication
    - Creates proper inheritance hierarchy
    - Maintains backward compatibility
    - Separates core functionality from implementation details

    3. Factory Method Unification

  Current issue: Separate factories (LoggerFactory and EnhancedLoggerFactory) with overlapping responsibilities.

  Before:
  // In model package                                                                                                                                                                                                                                                                  
  public class LoggerFactory {
  public static Logger getLogger(Class<?> clazz) { ... }
  }

  // In console package                                                                                                                                                                                                                                                                
  public class EnhancedLoggerFactory {
  public static Logger getLogger(Class<?> clazz) { ... }
  }

  After:
  // In factory package                                                                                                                                                                                                                                                                
  public interface LoggerProvider {
  Logger getLogger(Class<?> clazz);
  }

  public abstract class AbstractLoggerFactory implements LoggerProvider {
  // Common functionality                                                                                                                                                                                                                                                          
  }

  public class StandardLoggerFactory extends AbstractLoggerFactory {
  // Implementation for standard loggers                                                                                                                                                                                                                                           
  }

  public class EnhancedLoggerFactory extends AbstractLoggerFactory {
  // Implementation for enhanced loggers                                                                                                                                                                                                                                           
  }

  // Facade for client code                                                                                                                                                                                                                                                            
  public class Logging {
  public static Logger getLogger(Class<?> clazz) {
  // Delegate to appropriate factory                                                                                                                                                                                                                                           
  }
  }

  Benefits:
    - Unified approach to logger creation
    - Better extensibility for new logger types
    - Cleaner API for clients
    - Maintains backward compatibility

    4. Exception Handling Improvement

  Current issue: Uses reflection to access EnhancedThrowable, creating brittle code.

  After:
  // New design pattern to avoid reflection                                                                                                                                                                                                                                            
  public class StyleAdapter {
  // Centralized pattern for style application                                                                                                                                                                                                                                     
  public void applyStyle(EnhancedThrowable throwable, StyleType type, TerminalStyle style) {
  // Implementation with proper error handling                                                                                                                                                                                                                                 
  }
  }

  Benefits:
    - Eliminates brittle reflection code
    - Improves reliability and maintainability
    - Centralizes style management
    - Maintains backward compatibility

    5. AOP Component Refactoring

  Current issue: Duplicate code between LoggingAspect and EnhancedLoggingAspect.

  After:
  // Base abstract aspect                                                                                                                                                                                                                                                              
  public abstract class BaseLoggingAspect {
  // Common functionality                                                                                                                                                                                                                                                          
  }

  // Standard implementation                                                                                                                                                                                                                                                           
  public class LoggingAspect extends BaseLoggingAspect {
  // Basic implementation                                                                                                                                                                                                                                                          
  }

  // Enhanced implementation                                                                                                                                                                                                                                                           
  public class EnhancedLoggingAspect extends LoggingAspect {
  // Additional advanced features                                                                                                                                                                                                                                                  
  }

  Benefits:
    - Eliminates redundant code
    - Creates proper inheritance structure
    - Improves configurability
    - Better integration with logger system

  Detailed Implementation Strategy

  Phase 1: Package Reorganization

    1. Create new package structure
    2. Move classes to appropriate packages
    3. Update import statements
    4. Create package-info.java for documentation

  Phase 2: Logger Implementation

    1. Refactor AbstractLogger to include common functionality
    2. Create AbstractConsoleLogger with shared console logic
    3. Rename ConsoleLogger to StandardConsoleLogger
    4. Refactor EnhancedConsoleLogger to extend StandardConsoleLogger

  Phase 3: Factory Refactoring

    1. Create LoggerProvider interface
    2. Implement AbstractLoggerFactory
    3. Create concrete factory implementations
    4. Create Logging facade class

  Phase 4: Exception Handling

    1. Create StyleAdapter to replace reflection
    2. Update ContextAwareException to use StyleAdapter
    3. Organize specialized exceptions into their own files
    4. Create integration layer between logging and exceptions

  Phase 5: AOP Implementation

    1. Create BaseLoggingAspect with common functionality
    2. Refactor LoggingAspect to extend it
    3. Refactor EnhancedLoggingAspect to extend LoggingAspect
    4. Create configurability interfaces

  Key Benefits of This Approach

    1. Maintainability: Proper inheritance and organization makes the code easier to understand and maintain
    2. Consistency: Unified patterns for factories, styling, and exception handling
    3. Extensibility: Clear extension points for new logger types, formatters, and outputs
    4. Separation of Concerns: Clear boundaries between components with single responsibilities
    5. Backward Compatibility: All existing functionality is preserved with the same APIs

  This approach addresses all the issues in your current codebase while preserving the rich formatting and exception handling capabilities that make your library special.
