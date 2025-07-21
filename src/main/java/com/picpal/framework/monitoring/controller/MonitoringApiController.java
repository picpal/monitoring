package com.picpal.framework.monitoring.controller;

import com.picpal.framework.common.enums.MonitoringStatus;
import com.picpal.framework.monitoring.dto.MonitoringResultDTO;
import com.picpal.framework.monitoring.service.MonitoringService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/monitoring")
@RequiredArgsConstructor
@Tag(name = "Monitoring API", description = "모니터링 실행 및 결과 조회 API")
public class MonitoringApiController {

    private final MonitoringService monitoringService;

    @PostMapping("/execute")
    @Operation(summary = "모니터링 실행", description = "지정된 분석 기간으로 모니터링을 실행합니다.")
    public ResponseEntity<MonitoringResultDTO> executeMonitoring(
            @Parameter(description = "분석 기간 (예: 09:00, 13:00, 18:00)")
            @RequestParam String analysisPeriod) {
        
        log.info("모니터링 실행 요청: {}", analysisPeriod);
        
        MonitoringResultDTO result = monitoringService.executeMonitoring(analysisPeriod);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/execute/period")
    @Operation(summary = "특정 기간 모니터링 실행", description = "지정된 기간과 분석 기간으로 모니터링을 실행합니다.")
    public ResponseEntity<MonitoringResultDTO> executeMonitoringWithPeriod(
            @Parameter(description = "시작 시간")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @Parameter(description = "종료 시간")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @Parameter(description = "분석 기간")
            @RequestParam String analysisPeriod) {
        
        log.info("특정 기간 모니터링 실행 요청: {} ~ {}, 분석 기간: {}", startDate, endDate, analysisPeriod);
        
        MonitoringResultDTO result = monitoringService.executeMonitoring(startDate, endDate, analysisPeriod);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/status")
    @Operation(summary = "모니터링 상태 확인", description = "사용 가능한 분석 기간과 마지막 실행 시간을 확인합니다.")
    public ResponseEntity<Map<String, Object>> getMonitoringStatus() {
        
        log.info("모니터링 상태 확인 요청");
        
        try {
            Map<String, Object> status = new HashMap<>();
            status.put("availableAnalysisPeriods", List.of("09:00", "13:00", "18:00"));
            status.put("lastExecution", LocalDateTime.now()); // 실제로는 DB에서 조회
            
            return ResponseEntity.ok(status);
        } catch (Exception e) {
            log.error("모니터링 상태 확인 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
} 