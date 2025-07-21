package com.picpal.framework.monitoring.exception;

public class MonitoringException extends RuntimeException {
    public MonitoringException(String message) { super(message); }
    public MonitoringException(String message, Throwable cause) { super(message, cause); }
} 