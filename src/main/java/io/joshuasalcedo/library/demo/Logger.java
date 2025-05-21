//package io.joshuasalcedo.library.logging.model;
//
//import io.joshuasalcedo.pretty.core.model.time.TimeStamp;
//import io.joshuasalcedo.pretty.core.model.time.TimeStampFactory;
//
///**
// * Logger interface defining logging methods for all available log levels.
// * Provides throwable variants only for error-related levels.
// */
//public interface Logger<T extends LogLevel> {
//
//    String log(String message);
//    String log(String message, Throwable throwable);
//
//
//    private String getTimeStamp() {
//        TimeStamp timeStamp = TimeStampFactory.createPrettyTimeStamp();
//        return timeStamp.toString();
//    }
//
//}