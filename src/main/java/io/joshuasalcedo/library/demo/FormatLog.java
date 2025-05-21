//package io.joshuasalcedo.library.logging.model;
//
//
//public final class  FormatLog{
//
//    private FormatLog() {}
//
//    private  static final String FORMAT_LOG_ENTRY = "[%s] %s %s";
//
//
//    public static String format(LogLevel level, String loggerName, String message) {
//        return String.format(FORMAT_LOG_ENTRY, level.formatLabel(), loggerName, message);
//    }
//
//    public static String format(LogLevel level, String message) {
//        return String.format(FORMAT_LOG_ENTRY, level.getDisplayName(), "", message);
//    }
//
//}