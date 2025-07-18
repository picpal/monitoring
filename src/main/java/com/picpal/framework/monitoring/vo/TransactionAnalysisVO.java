package com.picpal.framework.monitoring.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
public class TransactionAnalysisVO {
    
    private Integer totalTransactions;
    private BigDecimal totalAmount;
    private BigDecimal averageAmount;
    private Integer approvedCount;
    private Integer declinedCount;
    private Integer fraudCount;
    private Integer highRiskCount;
    
    // 고객별 통계 (상위 10개)
    private List<Map<String, Object>> topCustomers;
    
    // 상점별 통계 (상위 10개)
    private List<Map<String, Object>> topMerchants;
    
    // 시간대별 통계
    private List<Map<String, Object>> hourlyStats;
    
    // 지역별 통계
    private List<Map<String, Object>> regionalStats;
    
    // 카드 타입별 통계
    private List<Map<String, Object>> cardTypeStats;
    
    // 채널별 통계
    private List<Map<String, Object>> channelStats;
    
    // 이상 거래 목록
    private List<Map<String, Object>> abnormalTransactions;
    
    // 분석 기간
    private String analysisPeriod;
    private String startDate;
    private String endDate;
} 