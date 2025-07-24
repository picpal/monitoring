package com.picpal.framework.monitoring.service.impl;

import com.picpal.framework.common.enums.MonitoringStatus;
import com.picpal.framework.common.utils.ThymeleafTemplateGenerator;
import com.picpal.framework.monitoring.dto.MonitoringResultDTO;
import com.picpal.framework.monitoring.repository.MonitoringResultRepository;
import com.picpal.framework.monitoring.repository.model.MonitoringResult;
import com.picpal.framework.monitoring.service.MonitoringService;
import com.picpal.framework.monitoring.service.TransactionAnalysisService;
import com.picpal.framework.monitoring.mapper.MonitoringStatisticsMapper;
import com.picpal.framework.monitoring.dto.MonitoringStatisticsDTO;
import com.picpal.framework.redmine.dto.RedmineIssueDTO;
import com.picpal.framework.redmine.service.RedmineService;
import com.picpal.framework.monitoring.vo.TransactionAnalysisVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import com.picpal.framework.monitoring.exception.MonitoringException;

@Slf4j
@Service
@RequiredArgsConstructor
public class MonitoringServiceImpl implements MonitoringService {

    private final TransactionAnalysisService transactionAnalysisService;
    private final ThymeleafTemplateGenerator templateGenerator;
    private final MonitoringResultRepository monitoringResultRepository;
    private final RedmineService redmineService;
    private final MonitoringStatisticsMapper statisticsMapper;

    @Override
    public MonitoringResultDTO executeMonitoring(String analysisPeriod) {
        log.info("모니터링 실행 시작: {}", analysisPeriod);
        
        LocalDate today = LocalDate.now();
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = today.atTime(23, 59, 59);
        
        return executeMonitoring(startOfDay, endOfDay, analysisPeriod);
    }

    @Override
    public MonitoringResultDTO executeMonitoring(LocalDateTime startDate, LocalDateTime endDate, String analysisPeriod) {
        log.info("모니터링 실행 시작: {} ~ {}, 분석 기간: {}", startDate, endDate, analysisPeriod);
        
        MonitoringResultDTO resultDTO = new MonitoringResultDTO();
        resultDTO.setMonitoringDate(LocalDateTime.now());
        resultDTO.setAnalysisPeriod(analysisPeriod);
        resultDTO.setStatus(MonitoringStatus.PROCESSING);
        
        try {
            // 1. 거래 데이터 분석
            TransactionAnalysisVO analysisVO = transactionAnalysisService.analyzeTransactions(startDate, endDate, analysisPeriod);
            
            // 2. 통계 데이터 생성
            MonitoringStatisticsDTO.StatisticsResponse statistics = generateStatisticsData();
            
            // 3. HTML 생성 (거래 분석 + 통계) - Thymeleaf 사용
            String htmlContent = templateGenerator.generateComprehensiveReportHtml(analysisVO, statistics);
            
            // 3. 결과 설정
            resultDTO.setTotalTransactions(analysisVO.getTotalTransactions());
            resultDTO.setTotalAmount(analysisVO.getTotalAmount());
            resultDTO.setAbnormalCount(analysisVO.getFraudCount() + analysisVO.getHighRiskCount());
            resultDTO.setHtmlContent(htmlContent);
            resultDTO.setStatus(MonitoringStatus.COMPLETED);
            
            log.info("모니터링 실행 완료: 총 거래 수: {}, 총 금액: {}, 이상 거래 수: {}", 
                    resultDTO.getTotalTransactions(), resultDTO.getTotalAmount(), resultDTO.getAbnormalCount());
            
        } catch (Exception e) {
            log.error("모니터링 실행 중 오류 발생", e);
            throw new MonitoringException("모니터링 실행 중 오류 발생", e);
        }
        
        // 4. 결과 저장
        MonitoringResultDTO savedResult;
        try {
            savedResult = saveMonitoringResult(resultDTO);
        } catch (Exception e) {
            log.error("모니터링 결과 저장 실패", e);
            throw new MonitoringException("모니터링 결과 저장 실패", e);
        }
        
        // 5. Redmine 이슈 생성 (이상 거래가 있는 경우)
        if (savedResult.getAbnormalCount() > 0 && savedResult.getStatus() == MonitoringStatus.COMPLETED) {
            try {
                createRedmineIssue("projectA", savedResult);
            } catch (Exception e) {
                log.error("Redmine 이슈 생성 실패", e);
                // Redmine 연동 실패는 모니터링 자체 실패로 간주하지 않고, 로그만 남김
            }
        }
        
        return savedResult;
    }

    private void createRedmineIssue(String projectKey, MonitoringResultDTO result) {
        RedmineIssueDTO issueDTO = RedmineIssueDTO.builder()
                .subject("모니터링 알림: " + result.getAnalysisPeriod())
                .description(buildIssueDescription(result))
                .build();
        Integer redmineIssueId = redmineService.createIssue(projectKey, issueDTO);
        if (redmineIssueId != null) {
            result.setRedmineIssueId(redmineIssueId.toString());
            updateRedmineIssueId(result.getId(), redmineIssueId.toString());
            log.info("Redmine 이슈 생성 완료: {}", redmineIssueId);
        }
    }

    private String buildIssueDescription(MonitoringResultDTO result) {
        StringBuilder description = new StringBuilder();
        description.append("**모니터링 결과 상세**\n\n");
        description.append("**분석 기간:** ").append(result.getAnalysisPeriod()).append("\n");
        description.append("**모니터링 날짜:** ").append(result.getMonitoringDate()).append("\n");
        description.append("**상태:** ").append(result.getStatus()).append("\n");
        description.append("**총 거래 수:** ").append(result.getTotalTransactions()).append("\n");
        description.append("**총 금액:** ").append(result.getTotalAmount()).append("\n");
        description.append("**이상 거래 수:** ").append(result.getAbnormalCount()).append("\n");

        if (result.getErrorMessage() != null && !result.getErrorMessage().isEmpty()) {
            description.append("\n**오류 메시지:**\n").append(result.getErrorMessage());
        }

        if (result.getHtmlContent() != null && !result.getHtmlContent().isEmpty()) {
            description.append("\n\n**HTML 리포트:**\n").append(result.getHtmlContent());
        }

        return description.toString();
    }

    /**
     * Redmine 이슈 ID를 업데이트합니다.
     */
    private void updateRedmineIssueId(Long resultId, String redmineIssueId) {
        Optional<MonitoringResult> resultOpt = monitoringResultRepository.findById(resultId);
        if (resultOpt.isPresent()) {
            MonitoringResult result = resultOpt.get();
            result.setRedmineIssueId(redmineIssueId);
            monitoringResultRepository.save(result);
        }
    }

    @Override
    public MonitoringResultDTO getMonitoringResult(Long resultId) {
        log.info("모니터링 결과 조회: {}", resultId);
        
        Optional<MonitoringResult> result = monitoringResultRepository.findById(resultId);
        return result.map(this::convertToDTO).orElse(null);
    }

    @Override
    public MonitoringResultDTO getLatestMonitoringResult(String analysisPeriod) {
        log.info("최근 모니터링 결과 조회: {}", analysisPeriod);
        
        // TODO: 최근 결과 조회 로직 구현
        // 현재는 기본값 반환
        MonitoringResultDTO resultDTO = new MonitoringResultDTO();
        resultDTO.setAnalysisPeriod(analysisPeriod);
        resultDTO.setStatus(MonitoringStatus.COMPLETED);
        resultDTO.setTotalTransactions(0);
        resultDTO.setTotalAmount(BigDecimal.ZERO);
        resultDTO.setAbnormalCount(0);
        
        return resultDTO;
    }

    @Override
    public MonitoringResultDTO saveMonitoringResult(MonitoringResultDTO resultDTO) {
        log.info("모니터링 결과 저장: {}", resultDTO.getAnalysisPeriod());
        
        MonitoringResult result = convertToEntity(resultDTO);
        MonitoringResult savedResult = monitoringResultRepository.save(result);
        
        return convertToDTO(savedResult);
    }

    /**
     * Entity를 DTO로 변환합니다.
     */
    private MonitoringResultDTO convertToDTO(MonitoringResult entity) {
        if (entity == null) {
            throw new IllegalArgumentException("MonitoringResult entity cannot be null");
        }
        
        MonitoringResultDTO dto = new MonitoringResultDTO();
        dto.setId(entity.getId());
        dto.setMonitoringDate(entity.getMonitoringDate());
        dto.setAnalysisPeriod(entity.getAnalysisPeriod());
        dto.setTotalTransactions(entity.getTotalTransactions());
        dto.setTotalAmount(entity.getTotalAmount());
        dto.setAbnormalCount(entity.getAbnormalCount());
        dto.setHtmlContent(entity.getHtmlContent());
        dto.setRedmineIssueId(entity.getRedmineIssueId());
        dto.setStatus(entity.getStatus());
        dto.setErrorMessage(entity.getErrorMessage());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        return dto;
    }

    /**
     * DTO를 Entity로 변환합니다.
     */
    private MonitoringResult convertToEntity(MonitoringResultDTO dto) {
        return MonitoringResult.builder()
                .monitoringDate(dto.getMonitoringDate())
                .analysisPeriod(dto.getAnalysisPeriod())
                .totalTransactions(dto.getTotalTransactions())
                .totalAmount(dto.getTotalAmount())
                .abnormalCount(dto.getAbnormalCount())
                .htmlContent(dto.getHtmlContent())
                .redmineIssueId(dto.getRedmineIssueId())
                .status(dto.getStatus())
                .errorMessage(dto.getErrorMessage())
                .build();
    }

    /**
     * 통계 데이터 생성
     */
    private MonitoringStatisticsDTO.StatisticsResponse generateStatisticsData() {
        LocalDateTime now = LocalDateTime.now();
        String dday = now.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String ddtm = now.format(DateTimeFormatter.ofPattern("HHmmss"));
        
        MonitoringStatisticsDTO.QueryParams params = MonitoringStatisticsDTO.QueryParams.builder()
                .dday(dday)
                .ddtm(ddtm)
                .build();
        
        // 각 카테고리별 데이터 조회
        List<MonitoringStatisticsDTO.RequestCount> allRequests = statisticsMapper.getAllRequestCounts(params);
        List<MonitoringStatisticsDTO.RequestCount> approvalRequests = statisticsMapper.getApprovalRequests(params);
        List<MonitoringStatisticsDTO.RequestCount> cancelRequests = statisticsMapper.getCancelRequests(params);
        List<MonitoringStatisticsDTO.RequestCount> failRequests = statisticsMapper.getFailRequests(params);
        
        // 전체 요약 통계 계산
        MonitoringStatisticsDTO.SummaryStatistics summary = calculateSummaryStatistics(
                allRequests, approvalRequests, cancelRequests, failRequests);
        
        return MonitoringStatisticsDTO.StatisticsResponse.builder()
                .summary(summary)
                .approvalRequests(approvalRequests)
                .cancelRequests(cancelRequests)
                .failRequests(failRequests)
                .build();
    }
    
    /**
     * 전체 요약 통계 계산
     */
    private MonitoringStatisticsDTO.SummaryStatistics calculateSummaryStatistics(
            List<MonitoringStatisticsDTO.RequestCount> allRequests,
            List<MonitoringStatisticsDTO.RequestCount> approvalRequests,
            List<MonitoringStatisticsDTO.RequestCount> cancelRequests,
            List<MonitoringStatisticsDTO.RequestCount> failRequests) {
        
        // 전체 합계 계산
        long totalRequests = calculateTotalCount(allRequests);
        long approvalCount = calculateTotalCount(approvalRequests);
        long cancelCount = calculateTotalCount(cancelRequests);
        long failCount = calculateTotalCount(failRequests);
        
        // 비율 계산
        BigDecimal approvalRate = calculateRate(approvalCount, totalRequests);
        BigDecimal cancelRate = calculateRate(cancelCount, totalRequests);
        BigDecimal failRate = calculateRate(failCount, totalRequests);
        
        return MonitoringStatisticsDTO.SummaryStatistics.builder()
                .totalRequests(totalRequests)
                .approvalCount(approvalCount)
                .cancelCount(cancelCount)
                .failCount(failCount)
                .approvalRate(approvalRate)
                .cancelRate(cancelRate)
                .failRate(failRate)
                // 7일 단위 총계
                .d0Total(sumCountsByDay(allRequests, "d0"))
                .d7Total(sumCountsByDay(allRequests, "d7"))
                .d14Total(sumCountsByDay(allRequests, "d14"))
                .d21Total(sumCountsByDay(allRequests, "d21"))
                .d28Total(sumCountsByDay(allRequests, "d28"))
                .d35Total(sumCountsByDay(allRequests, "d35"))
                .d42Total(sumCountsByDay(allRequests, "d42"))
                .d49Total(sumCountsByDay(allRequests, "d49"))
                .d56Total(sumCountsByDay(allRequests, "d56"))
                .d63Total(sumCountsByDay(allRequests, "d63"))
                .d70Total(sumCountsByDay(allRequests, "d70"))
                .build();
    }
    
    /**
     * 전체 카운트 합계 계산
     */
    private long calculateTotalCount(List<MonitoringStatisticsDTO.RequestCount> counts) {
        return counts.stream()
                .mapToLong(count -> 
                    (count.getD0Cnt() != null ? count.getD0Cnt() : 0L) +
                    (count.getD7Cnt() != null ? count.getD7Cnt() : 0L) +
                    (count.getD14Cnt() != null ? count.getD14Cnt() : 0L) +
                    (count.getD21Cnt() != null ? count.getD21Cnt() : 0L) +
                    (count.getD28Cnt() != null ? count.getD28Cnt() : 0L) +
                    (count.getD35Cnt() != null ? count.getD35Cnt() : 0L) +
                    (count.getD42Cnt() != null ? count.getD42Cnt() : 0L) +
                    (count.getD49Cnt() != null ? count.getD49Cnt() : 0L) +
                    (count.getD56Cnt() != null ? count.getD56Cnt() : 0L) +
                    (count.getD63Cnt() != null ? count.getD63Cnt() : 0L) +
                    (count.getD70Cnt() != null ? count.getD70Cnt() : 0L)
                )
                .sum();
    }
    
    private long sumCountsByDay(List<MonitoringStatisticsDTO.RequestCount> counts, String dayField) {
        return counts.stream()
                .mapToLong(count -> getCountByDay(count, dayField))
                .sum();
    }
    
    private long getCountByDay(MonitoringStatisticsDTO.RequestCount count, String dayField) {
        switch (dayField) {
            case "d0": return count.getD0Cnt() != null ? count.getD0Cnt() : 0L;
            case "d7": return count.getD7Cnt() != null ? count.getD7Cnt() : 0L;
            case "d14": return count.getD14Cnt() != null ? count.getD14Cnt() : 0L;
            case "d21": return count.getD21Cnt() != null ? count.getD21Cnt() : 0L;
            case "d28": return count.getD28Cnt() != null ? count.getD28Cnt() : 0L;
            case "d35": return count.getD35Cnt() != null ? count.getD35Cnt() : 0L;
            case "d42": return count.getD42Cnt() != null ? count.getD42Cnt() : 0L;
            case "d49": return count.getD49Cnt() != null ? count.getD49Cnt() : 0L;
            case "d56": return count.getD56Cnt() != null ? count.getD56Cnt() : 0L;
            case "d63": return count.getD63Cnt() != null ? count.getD63Cnt() : 0L;
            case "d70": return count.getD70Cnt() != null ? count.getD70Cnt() : 0L;
            default: return 0L;
        }
    }
    
    private BigDecimal calculateRate(long numerator, long denominator) {
        if (denominator == 0) {
            return BigDecimal.ZERO;
        }
        return BigDecimal.valueOf(numerator)
                .multiply(new BigDecimal("100"))
                .divide(BigDecimal.valueOf(denominator), 2, RoundingMode.HALF_UP);
    }

}
