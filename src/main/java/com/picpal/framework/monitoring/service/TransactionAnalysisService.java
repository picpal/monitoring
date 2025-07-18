package com.picpal.framework.monitoring.service;

import com.picpal.framework.monitoring.vo.TransactionAnalysisVO;

import java.time.LocalDateTime;

public interface TransactionAnalysisService {
    
    /**
     * 특정 기간의 거래 데이터를 분석합니다.
     */
    TransactionAnalysisVO analyzeTransactions(LocalDateTime startDate, LocalDateTime endDate, String analysisPeriod);
    
    /**
     * 오늘의 거래 데이터를 분석합니다.
     */
    TransactionAnalysisVO analyzeTodayTransactions(String analysisPeriod);
    
    /**
     * 어제의 거래 데이터를 분석합니다.
     */
    TransactionAnalysisVO analyzeYesterdayTransactions(String analysisPeriod);
    
    /**
     * 특정 기간의 이상 거래를 분석합니다.
     */
    TransactionAnalysisVO analyzeAbnormalTransactions(LocalDateTime startDate, LocalDateTime endDate, String analysisPeriod);
} 