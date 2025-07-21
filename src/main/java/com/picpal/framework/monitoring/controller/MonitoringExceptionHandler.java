package com.picpal.framework.monitoring.controller;

import com.picpal.framework.monitoring.exception.MonitoringException;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice(basePackages = "com.picpal.framework.monitoring")
public class MonitoringExceptionHandler {
    @ExceptionHandler(MonitoringException.class)
    public ResponseEntity<?> handleMonitoring(MonitoringException ex) {
        log.error("Monitoring 도메인 예외: {}", ex.getMessage(), ex);
        String requestId = MDC.get("requestId");
        Map<String, Object> errorBody = new HashMap<>();
        errorBody.put("error", ex.getMessage());
        errorBody.put("requestId", requestId);
        return ResponseEntity.status(500).body(errorBody);
    }
} 