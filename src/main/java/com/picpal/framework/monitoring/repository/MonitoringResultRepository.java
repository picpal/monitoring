package com.picpal.framework.monitoring.repository;

import com.picpal.framework.common.enums.MonitoringStatus;
import com.picpal.framework.monitoring.repository.model.MonitoringResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface MonitoringResultRepository extends JpaRepository<MonitoringResult, Long> {

    /**
     * 특정 기간의 모니터링 결과 조회
     */
    List<MonitoringResult> findByMonitoringDateBetweenOrderByMonitoringDateDesc(
            LocalDateTime startDate, LocalDateTime endDate);

    /**
     * 특정 분석 기간의 모니터링 결과 조회
     */
    List<MonitoringResult> findByAnalysisPeriodOrderByMonitoringDateDesc(String analysisPeriod);

    /**
     * 특정 상태의 모니터링 결과 조회
     */
    List<MonitoringResult> findByStatusOrderByMonitoringDateDesc(MonitoringStatus status);

    /**
     * 특정 기간과 상태의 모니터링 결과 조회
     */
    @Query("SELECT mr FROM MonitoringResult mr WHERE mr.monitoringDate BETWEEN :startDate AND :endDate AND mr.status = :status ORDER BY mr.monitoringDate DESC")
    List<MonitoringResult> findByDateRangeAndStatus(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("status") MonitoringStatus status);

    /**
     * 최근 모니터링 결과 조회 (페이징)
     */
    Page<MonitoringResult> findAllByOrderByMonitoringDateDesc(Pageable pageable);

    /**
     * 특정 Redmine 이슈 ID로 모니터링 결과 조회
     */
    Optional<MonitoringResult> findByRedmineIssueId(String redmineIssueId);

    /**
     * 오늘의 모니터링 결과 조회
     */
    @Query("SELECT mr FROM MonitoringResult mr WHERE CAST(mr.monitoringDate AS date) = CURRENT_DATE ORDER BY mr.monitoringDate DESC")
    List<MonitoringResult> findTodayResults();

    /**
     * 특정 기간의 실패한 모니터링 결과 조회
     */
    @Query("SELECT mr FROM MonitoringResult mr WHERE mr.monitoringDate BETWEEN :startDate AND :endDate AND mr.status IN ('FAILED', 'ERROR') ORDER BY mr.monitoringDate DESC")
    List<MonitoringResult> findFailedResultsByDateRange(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);
} 