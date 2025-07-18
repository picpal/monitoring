package com.picpal.framework.monitoring.repository.model;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class TransactionData {
    
    private Long id;
    private LocalDateTime transactionDate;
    private BigDecimal amount;
    private String customerId;
    private String customerName;
    private String transactionType;
    private String status;
    private String description;
    private String merchantId;
    private String merchantName;
    private String cardNumber;
    private String cardType;
    private String currency;
    private String country;
    private String city;
    private String ipAddress;
    private String deviceType;
    private String channel;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // 추가 필드들 (실제 거래 데이터 구조에 맞게 조정 필요)
    private String riskLevel;
    private String fraudFlag;
    private String approvalCode;
    private String declineReason;
    private BigDecimal fee;
    private BigDecimal tax;
    private String batchId;
    private String settlementId;
} 