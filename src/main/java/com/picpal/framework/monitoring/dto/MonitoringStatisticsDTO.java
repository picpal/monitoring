package com.picpal.framework.monitoring.dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;
import java.util.List;

@Data
public class MonitoringStatisticsDTO {
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RequestCount {
        private String resultCd;
        private String resultMsg;
        private Long d0Cnt;
        private Long d7Cnt;
        private Long d14Cnt;
        private Long d21Cnt;
        private Long d28Cnt;
        private Long d35Cnt;
        private Long d42Cnt;
        private Long d49Cnt;
        private Long d56Cnt;
        private Long d63Cnt;
        private Long d70Cnt;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StatisticsResponse {
        // 전체 요청 요약
        private SummaryStatistics summary;
        
        // 각 카테고리별 상세 통계
        private List<RequestCount> approvalRequests;   // 승인 요청
        private List<RequestCount> cancelRequests;     // 취소 요청
        private List<RequestCount> failRequests;       // 요청 실패
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SummaryStatistics {
        private Long totalRequests;
        private Long approvalCount;
        private Long cancelCount;
        private Long failCount;
        private BigDecimal approvalRate;
        private BigDecimal cancelRate;
        private BigDecimal failRate;
        
        // 7일 단위 총계 (d0 ~ d70)
        private Long d0Total;
        private Long d7Total;
        private Long d14Total;
        private Long d21Total;
        private Long d28Total;
        private Long d35Total;
        private Long d42Total;
        private Long d49Total;
        private Long d56Total;
        private Long d63Total;
        private Long d70Total;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DayStatistics {
        private Long totalRequests;
        private Long successRequests;
        private Long failRequests;
        private BigDecimal successRate;
        private BigDecimal failureRate;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class QueryParams {
        private String dday;
        private String ddtm;
    }
}