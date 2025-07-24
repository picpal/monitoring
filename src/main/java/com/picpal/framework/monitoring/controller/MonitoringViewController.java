package com.picpal.framework.monitoring.controller;

import com.picpal.framework.monitoring.vo.TransactionAnalysisVO;
import com.picpal.framework.monitoring.dto.MonitoringStatisticsDTO;
import com.picpal.framework.monitoring.mapper.MonitoringStatisticsMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

@Slf4j
@Controller
@RequestMapping("/monitoring")
@RequiredArgsConstructor
@Tag(name = "Monitoring View", description = "모니터링 리포트 뷰")
public class MonitoringViewController {

    private final MonitoringStatisticsMapper statisticsMapper;

    @GetMapping("/report")
    @Operation(summary = "모니터링 리포트 뷰", description = "Thymeleaf 템플릿을 웹에서 확인할 수 있습니다.")
    public String viewReport(Model model) {
        log.info("모니터링 리포트 뷰 요청");
        
        // 샘플 데이터 생성
        TransactionAnalysisVO analysis = createSampleData();
        
        model.addAttribute("analysis", analysis);
        
        return "monitoring-report";
    }

    @GetMapping("/statistics")
    @Operation(summary = "모니터링 통계 뷰", description = "모니터링 통계 페이지를 표시합니다.")
    public String viewStatistics(Model model) {
        log.info("모니터링 통계 뷰 요청");
        
        // 샘플 통계 데이터 생성
        List<Map<String, Object>> statisticsList = createSampleStatisticsList();
        model.addAttribute("statisticsList", statisticsList);
        
        return "monitoring-statistics";
    }

    @GetMapping("/detailed-statistics")
    @Operation(summary = "모니터링 상세 통계 뷰", description = "모니터링 상세 통계 페이지를 표시합니다.")
    public String viewDetailedStatistics(Model model) {
        log.info("모니터링 상세 통계 뷰 요청");
        
        // 샘플 상세 통계 데이터 생성
        MonitoringStatisticsDTO.StatisticsResponse statistics = createSampleDetailedStatistics();
        model.addAttribute("statistics", statistics);
        
        return "monitoring-detailed-statistics";
    }

    @GetMapping("/comprehensive-report")
    @Operation(summary = "모니터링 종합 리포트 뷰", description = "모니터링 종합 리포트 페이지를 표시합니다.")
    public String viewComprehensiveReport(Model model) {
        log.info("모니터링 종합 리포트 뷰 요청");
        
        // 상세 통계 데이터
        MonitoringStatisticsDTO.StatisticsResponse statistics = createSampleDetailedStatistics();
        model.addAttribute("statistics", statistics);
        
        // 거래 분석 HTML
        String transactionHtml = "<div class='text-center p-4'><p class='text-gray-600'>거래 분석 데이터를 준비중입니다.</p></div>";
        model.addAttribute("transactionHtml", transactionHtml);
        
        return "monitoring-comprehensive-report";
    }

    private List<Map<String, Object>> createSampleStatisticsList() {
        List<Map<String, Object>> statisticsList = new ArrayList<>();
        
        Map<String, Object> today = new HashMap<>();
        today.put("label", "오늘");
        today.put("totalRequests", 1250);
        today.put("successRequests", 1200);
        today.put("failRequests", 50);
        today.put("successRate", "96.0");
        today.put("failureRate", "4.0");
        statisticsList.add(today);
        
        Map<String, Object> day7 = new HashMap<>();
        day7.put("label", "7일 전");
        day7.put("totalRequests", 1180);
        day7.put("successRequests", 1130);
        day7.put("failRequests", 50);
        day7.put("successRate", "95.8");
        day7.put("failureRate", "4.2");
        statisticsList.add(day7);
        
        Map<String, Object> day14 = new HashMap<>();
        day14.put("label", "14일 전");
        day14.put("totalRequests", 1100);
        day14.put("successRequests", 1050);
        day14.put("failRequests", 50);
        day14.put("successRate", "95.5");
        day14.put("failureRate", "4.5");
        statisticsList.add(day14);
        
        return statisticsList;
    }

    private MonitoringStatisticsDTO.StatisticsResponse createSampleDetailedStatistics() {
        // 요약 통계
        MonitoringStatisticsDTO.SummaryStatistics summary = MonitoringStatisticsDTO.SummaryStatistics.builder()
            .totalRequests(5000L)
            .approvalCount(4500L)
            .cancelCount(300L)
            .failCount(200L)
            .approvalRate(new BigDecimal("90.0"))
            .cancelRate(new BigDecimal("6.0"))
            .failRate(new BigDecimal("4.0"))
            .d0Total(120L)
            .d7Total(115L)
            .d14Total(110L)
            .d21Total(105L)
            .d28Total(100L)
            .d35Total(95L)
            .d42Total(90L)
            .d49Total(85L)
            .d56Total(80L)
            .d63Total(75L)
            .d70Total(70L)
            .build();

        // 승인 요청 샘플 데이터
        List<MonitoringStatisticsDTO.RequestCount> approvalRequests = new ArrayList<>();
        approvalRequests.add(MonitoringStatisticsDTO.RequestCount.builder()
            .resultCd("0000")
            .resultMsg("승인완료")
            .d0Cnt(100L).d7Cnt(95L).d14Cnt(90L).d21Cnt(85L).d28Cnt(80L)
            .d35Cnt(75L).d42Cnt(70L).d49Cnt(65L).d56Cnt(60L).d63Cnt(55L).d70Cnt(50L)
            .build());
        approvalRequests.add(MonitoringStatisticsDTO.RequestCount.builder()
            .resultCd("3001")
            .resultMsg("승인거부")
            .d0Cnt(20L).d7Cnt(20L).d14Cnt(20L).d21Cnt(20L).d28Cnt(20L)
            .d35Cnt(20L).d42Cnt(20L).d49Cnt(20L).d56Cnt(20L).d63Cnt(20L).d70Cnt(20L)
            .build());

        // 취소 요청 샘플 데이터
        List<MonitoringStatisticsDTO.RequestCount> cancelRequests = new ArrayList<>();
        cancelRequests.add(MonitoringStatisticsDTO.RequestCount.builder()
            .resultCd("0000")
            .resultMsg("취소완료")
            .d0Cnt(25L).d7Cnt(24L).d14Cnt(23L).d21Cnt(22L).d28Cnt(21L)
            .d35Cnt(20L).d42Cnt(19L).d49Cnt(18L).d56Cnt(17L).d63Cnt(16L).d70Cnt(15L)
            .build());
        cancelRequests.add(MonitoringStatisticsDTO.RequestCount.builder()
            .resultCd("4001")
            .resultMsg("취소불가")
            .d0Cnt(5L).d7Cnt(5L).d14Cnt(5L).d21Cnt(5L).d28Cnt(5L)
            .d35Cnt(5L).d42Cnt(5L).d49Cnt(5L).d56Cnt(5L).d63Cnt(5L).d70Cnt(5L)
            .build());

        // 실패 요청 샘플 데이터
        List<MonitoringStatisticsDTO.RequestCount> failRequests = new ArrayList<>();
        failRequests.add(MonitoringStatisticsDTO.RequestCount.builder()
            .resultCd("9001")
            .resultMsg("인증실패")
            .d0Cnt(15L).d7Cnt(14L).d14Cnt(13L).d21Cnt(12L).d28Cnt(11L)
            .d35Cnt(10L).d42Cnt(9L).d49Cnt(8L).d56Cnt(7L).d63Cnt(6L).d70Cnt(5L)
            .build());
        failRequests.add(MonitoringStatisticsDTO.RequestCount.builder()
            .resultCd("9002")
            .resultMsg("권한없음")
            .d0Cnt(8L).d7Cnt(8L).d14Cnt(8L).d21Cnt(8L).d28Cnt(8L)
            .d35Cnt(8L).d42Cnt(8L).d49Cnt(8L).d56Cnt(8L).d63Cnt(8L).d70Cnt(8L)
            .build());

        return MonitoringStatisticsDTO.StatisticsResponse.builder()
            .summary(summary)
            .approvalRequests(approvalRequests)
            .cancelRequests(cancelRequests)
            .failRequests(failRequests)
            .build();
    }

    private TransactionAnalysisVO createSampleData() {
        TransactionAnalysisVO analysis = new TransactionAnalysisVO();
        
        // 기본 정보
        analysis.setAnalysisPeriod("09:00");
        analysis.setStartDate("2024-01-15");
        analysis.setEndDate("2024-01-15");
        
        // 통계 데이터
        analysis.setTotalTransactions(1250);
        analysis.setTotalAmount(new BigDecimal("125000000"));
        analysis.setAverageAmount(new BigDecimal("100000"));
        analysis.setApprovedCount(1200);
        analysis.setDeclinedCount(50);
        analysis.setFraudCount(3);
        analysis.setHighRiskCount(7);
        
        // 상위 고객 데이터
        Map<String, Object> customer1 = new HashMap<>();
        customer1.put("customer_id", "CUST001");
        customer1.put("customer_name", "김철수");
        customer1.put("transaction_count", 45);
        customer1.put("total_amount", new BigDecimal("4500000"));
        
        Map<String, Object> customer2 = new HashMap<>();
        customer2.put("customer_id", "CUST002");
        customer2.put("customer_name", "이영희");
        customer2.put("transaction_count", 38);
        customer2.put("total_amount", new BigDecimal("3800000"));
        
        Map<String, Object> customer3 = new HashMap<>();
        customer3.put("customer_id", "CUST003");
        customer3.put("customer_name", "박민수");
        customer3.put("transaction_count", 32);
        customer3.put("total_amount", new BigDecimal("3200000"));
        
        analysis.setTopCustomers(Arrays.asList(customer1, customer2, customer3));
        
        // 상위 상점 데이터
        Map<String, Object> merchant1 = new HashMap<>();
        merchant1.put("merchant_id", "MERCH001");
        merchant1.put("merchant_name", "온라인 쇼핑몰 A");
        merchant1.put("transaction_count", 156);
        merchant1.put("total_amount", new BigDecimal("15600000"));
        
        Map<String, Object> merchant2 = new HashMap<>();
        merchant2.put("merchant_id", "MERCH002");
        merchant2.put("merchant_name", "오프라인 매장 B");
        merchant2.put("transaction_count", 89);
        merchant2.put("total_amount", new BigDecimal("8900000"));
        
        Map<String, Object> merchant3 = new HashMap<>();
        merchant3.put("merchant_id", "MERCH003");
        merchant3.put("merchant_name", "모바일 앱 C");
        merchant3.put("transaction_count", 67);
        merchant3.put("total_amount", new BigDecimal("6700000"));
        
        analysis.setTopMerchants(Arrays.asList(merchant1, merchant2, merchant3));
        
        // 시간대별 통계
        Map<String, Object> hour9 = new HashMap<>();
        hour9.put("hour", 9);
        hour9.put("transaction_count", 45);
        hour9.put("total_amount", new BigDecimal("4500000"));
        hour9.put("avg_amount", new BigDecimal("100000"));
        
        Map<String, Object> hour10 = new HashMap<>();
        hour10.put("hour", 10);
        hour10.put("transaction_count", 78);
        hour10.put("total_amount", new BigDecimal("7800000"));
        hour10.put("avg_amount", new BigDecimal("100000"));
        
        Map<String, Object> hour11 = new HashMap<>();
        hour11.put("hour", 11);
        hour11.put("transaction_count", 92);
        hour11.put("total_amount", new BigDecimal("9200000"));
        hour11.put("avg_amount", new BigDecimal("100000"));
        
        Map<String, Object> hour12 = new HashMap<>();
        hour12.put("hour", 12);
        hour12.put("transaction_count", 156);
        hour12.put("total_amount", new BigDecimal("15600000"));
        hour12.put("avg_amount", new BigDecimal("100000"));
        
        analysis.setHourlyStats(Arrays.asList(hour9, hour10, hour11, hour12));
        
        return analysis;
    }
} 