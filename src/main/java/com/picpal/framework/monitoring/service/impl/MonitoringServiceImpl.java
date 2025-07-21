package com.picpal.framework.monitoring.service.impl;

import com.picpal.framework.common.enums.MonitoringStatus;
import com.picpal.framework.common.utils.ThymeleafTemplateGenerator;
import com.picpal.framework.monitoring.dto.MonitoringResultDTO;
import com.picpal.framework.monitoring.repository.MonitoringResultRepository;
import com.picpal.framework.monitoring.repository.model.MonitoringResult;
import com.picpal.framework.monitoring.service.MonitoringService;
import com.picpal.framework.monitoring.service.TransactionAnalysisService;
import com.picpal.framework.redmine.service.RedmineService;
import com.picpal.framework.monitoring.vo.TransactionAnalysisVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
            
            // 2. HTML 생성
            String htmlContent = templateGenerator.generateTransactionReportHtml(analysisVO);
            
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
                Integer redmineIssueId = redmineService.createMonitoringIssue(savedResult.getId());
                if (redmineIssueId != null) {
                    savedResult.setRedmineIssueId(redmineIssueId.toString());
                    // Redmine 이슈 ID 업데이트
                    updateRedmineIssueId(savedResult.getId(), redmineIssueId.toString());
                    log.info("Redmine 이슈 생성 완료: {}", redmineIssueId);
                }
            } catch (Exception e) {
                log.error("Redmine 이슈 생성 실패", e);
                // Redmine 연동 실패는 모니터링 자체 실패로 간주하지 않고, 로그만 남김
            }
        }
        
        return savedResult;
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
} 