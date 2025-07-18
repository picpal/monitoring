package com.picpal.framework.monitoring.service;

import com.picpal.framework.monitoring.dto.MonitoringResultDTO;

import java.time.LocalDateTime;

public interface MonitoringService {
    
    /**
     * 모니터링 프로세스를 실행합니다.
     */
    MonitoringResultDTO executeMonitoring(String analysisPeriod);
    
    /**
     * 특정 기간의 모니터링을 실행합니다.
     */
    MonitoringResultDTO executeMonitoring(LocalDateTime startDate, LocalDateTime endDate, String analysisPeriod);
    
    /**
     * 모니터링 결과를 조회합니다.
     */
    MonitoringResultDTO getMonitoringResult(Long resultId);
    
    /**
     * 최근 모니터링 결과를 조회합니다.
     */
    MonitoringResultDTO getLatestMonitoringResult(String analysisPeriod);
    
    /**
     * 모니터링 결과를 저장합니다.
     */
    MonitoringResultDTO saveMonitoringResult(MonitoringResultDTO resultDTO);
    

} 