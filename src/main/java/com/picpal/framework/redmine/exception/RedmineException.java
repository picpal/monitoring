package com.picpal.framework.redmine.exception;

public class RedmineException extends RuntimeException {
    public RedmineException(String message) { super(message); }
    public RedmineException(String message, Throwable cause) { super(message, cause); }
} 