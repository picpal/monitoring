package com.picpal.framework.redmine.exception;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice(basePackages = "com.picpal.framework.redmine")
public class RedmineExceptionHandler {
    @ExceptionHandler(RedmineException.class)
    public ResponseEntity<?> handleRedmine(RedmineException ex) {
        log.error("Redmine 연동 예외: {}", ex.getMessage(), ex);
        String requestId = MDC.get("requestId");
        Map<String, Object> errorBody = new HashMap<>();
        errorBody.put("error", ex.getMessage());
        errorBody.put("requestId", requestId);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorBody);
    }
} 