//package io.joshuasalcedo.library.logging;
//
//import java.io.File;
//import java.io.FileWriter;
//import java.io.IOException;
//import java.io.PrintWriter;
//import java.text.SimpleDateFormat;
//import java.util.Date;
//import java.util.concurrent.BlockingQueue;
//import java.util.concurrent.LinkedBlockingQueue;
//
//public class AsyncLogger {
//    private static AsyncLogger instance;
//    private BlockingQueue<LogMessage> logQueue;
//    private LogWriter logWriter;
//    private Thread writerThread;
//    private volatile boolean isRunning;
//
//    public enum LogLevel {
//        DEBUG, INFO, WARN, ERROR
//    }
//
//    private AsyncLogger(String logFilePath) {
//        this.logQueue = new LinkedBlockingQueue<>();
//        this.logWriter = new LogWriter(logFilePath);
//        this.isRunning = true;
//
//        // Start the background thread that processes the log queue
//        this.writerThread = new Thread(() -> {
//            while (isRunning || !logQueue.isEmpty()) {
//                try {
//                    LogMessage message = logQueue.take();
//                    logWriter.write(message);
//                } catch (InterruptedException e) {
//                    Thread.currentThread().interrupt();
//                    break;
//                }
//            }
//            logWriter.close();
//        });
//
//        writerThread.setDaemon(true);
//        writerThread.start();
//    }
//
//    public static synchronized AsyncLogger getInstance(String logFilePath) {
//        if (instance == null) {
//            instance = new AsyncLogger(logFilePath);
//        }
//        return instance;
//    }
//
//    public void debug(String message) {
//        log(LogLevel.DEBUG, message);
//    }
//
//    public void info(String message) {
//        log(LogLevel.INFO, message);
//    }
//
//    public void warn(String message) {
//        log(LogLevel.WARN, message);
//    }
//
//    public void error(String message) {
//        log(LogLevel.ERROR, message);
//    }
//
//    public void error(String message, Throwable throwable) {
//        log(LogLevel.ERROR, message + ": " + throwableToString(throwable));
//    }
//
//    private String throwableToString(Throwable throwable) {
//        StringBuilder sb = new StringBuilder();
//        sb.append(throwable.toString()).append("\n");
//        for (StackTraceElement element : throwable.getStackTrace()) {
//            sb.append("\tat ").append(element.toString()).append("\n");
//        }
//        return sb.toString();
//    }
//
//    private void log(LogLevel level, String message) {
//        LogMessage logMessage = new LogMessage(level, message);
//        try {
//            logQueue.put(logMessage);
//        } catch (InterruptedException e) {
//            Thread.currentThread().interrupt();
//            // Fall back to synchronous logging if interrupted
//            logWriter.write(logMessage);
//        }
//    }
//
//    public void shutdown() {
//        isRunning = false;
//        writerThread.interrupt();
//        try {
//            writerThread.join(5000); // Wait up to 5 seconds for logs to flush
//        } catch (InterruptedException e) {
//            Thread.currentThread().interrupt();
//        }
//    }
//
//    private static class LogMessage {
//        private final LogLevel level;
//        private final String message;
//        private final long timestamp;
//
//        public LogMessage(LogLevel level, String message) {
//            this.level = level;
//            this.message = message;
//            this.timestamp = System.currentTimeMillis();
//        }
//
//        public LogLevel getLevel() {
//            return level;
//        }
//
//        public String getMessage() {
//            return message;
//        }
//
//        public long getTimestamp() {
//            return timestamp;
//        }
//    }
//
//    private static class LogWriter {
//        private PrintWriter writer;
//        private SimpleDateFormat dateFormat;
//        private boolean logToConsole = true; // Add this flag
//
//        public LogWriter(String filePath) {
//            try {
//                // Ensure parent directory exists
//                File logFile = new File(filePath);
//                File parentDir = logFile.getParentFile();
//                if (parentDir != null && !parentDir.exists()) {
//                    parentDir.mkdirs();
//                }
//
//                this.writer = new PrintWriter(new FileWriter(filePath, true), true);
//                this.dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
//            } catch (IOException e) {
//                System.err.println("Failed to open log file: " + e.getMessage());
//                throw new RuntimeException("Failed to initialize log writer", e);
//            }
//        }
//
//        public void write(LogMessage logMessage) {
//            if (writer != null) {
//                String formattedDate = dateFormat.format(new Date(logMessage.getTimestamp()));
//                String logEntry = formattedDate + " [" + logMessage.getLevel() + "] " + logMessage.getMessage();
//                writer.println(logEntry);
//
//                // Also print to console if enabled
//                if (logToConsole) {
//                    System.out.println(logEntry);
//                }
//            }
//        }
//
//        public void close() {
//            if (writer != null) {
//                writer.flush();
//                writer.close();
//            }
//        }
//    }
//}