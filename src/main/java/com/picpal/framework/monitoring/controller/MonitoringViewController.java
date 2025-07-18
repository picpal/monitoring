package com.picpal.framework.monitoring.controller;

import com.picpal.framework.monitoring.vo.TransactionAnalysisVO;
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
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Controller
@RequestMapping("/monitoring")
@RequiredArgsConstructor
@Tag(name = "Monitoring View", description = "모니터링 리포트 뷰")
public class MonitoringViewController {

    @GetMapping("/report")
    @Operation(summary = "모니터링 리포트 뷰", description = "Thymeleaf 템플릿을 웹에서 확인할 수 있습니다.")
    public String viewReport(Model model) {
        log.info("모니터링 리포트 뷰 요청");
        
        // 샘플 데이터 생성
        TransactionAnalysisVO analysis = createSampleData();
        
        model.addAttribute("analysis", analysis);
        
        return "monitoring-report";
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