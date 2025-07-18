package com.picpal.framework.monitoring.dto;

import com.picpal.framework.common.enums.MonitoringStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class MonitoringResultDTO {
    
    private Long id;
    private LocalDateTime monitoringDate;
    private String analysisPeriod;
    private Integer totalTransactions;
    private BigDecimal totalAmount;
    private Integer abnormalCount;
    private String htmlContent;
    private String redmineIssueId;
    private MonitoringStatus status;
    private String errorMessage;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
} 