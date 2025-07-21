package com.picpal.framework.monitoring.service;

import com.picpal.framework.common.enums.MonitoringStatus;
import com.picpal.framework.monitoring.dto.MonitoringResultDTO;
import com.picpal.framework.monitoring.repository.MonitoringResultRepository;
import com.picpal.framework.monitoring.repository.model.MonitoringResult;
import com.picpal.framework.monitoring.service.impl.MonitoringServiceImpl;
import com.picpal.framework.monitoring.vo.TransactionAnalysisVO;
import com.picpal.framework.common.utils.ThymeleafTemplateGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import com.picpal.framework.redmine.service.RedmineService;

@ExtendWith(MockitoExtension.class)
class MonitoringServiceTest {

    @Mock
    private TransactionAnalysisService transactionAnalysisService;

    @Mock
    private ThymeleafTemplateGenerator templateGenerator;

    @Mock
    private MonitoringResultRepository monitoringResultRepository;

    @Mock
    private RedmineService redmineService;

    @InjectMocks
    private MonitoringServiceImpl monitoringService;

    private TransactionAnalysisVO sampleAnalysisVO;
    private MonitoringResult sampleMonitoringResult;

    @BeforeEach
    void setUp() {
        // 샘플 분석 결과 설정
        sampleAnalysisVO = new TransactionAnalysisVO();
        sampleAnalysisVO.setTotalTransactions(1250);
        sampleAnalysisVO.setTotalAmount(new BigDecimal("125000000"));
        sampleAnalysisVO.setFraudCount(5);
        sampleAnalysisVO.setHighRiskCount(3);

        // 샘플 모니터링 결과 설정
        sampleMonitoringResult = MonitoringResult.builder()
                .monitoringDate(LocalDateTime.now())
                .analysisPeriod("09:00")
                .totalTransactions(1250)
                .totalAmount(new BigDecimal("125000000"))
                .abnormalCount(8)
                .htmlContent("<html>Sample Report</html>")
                .status(MonitoringStatus.COMPLETED)
                .build();
        sampleMonitoringResult.setId(1L); // ID는 별도로 설정
    }

    @Test
    @DisplayName("모니터링 실행 테스트 - 성공 케이스")
    void testExecuteMonitoring_Success() {
        // Given
        String analysisPeriod = "09:00";
        when(transactionAnalysisService.analyzeTransactions(any(), any(), eq(analysisPeriod)))
                .thenReturn(sampleAnalysisVO);
        when(templateGenerator.generateTransactionReportHtml(sampleAnalysisVO))
                .thenReturn("<html>Sample Report</html>");
        when(monitoringResultRepository.save(any(MonitoringResult.class)))
                .thenReturn(sampleMonitoringResult);
        when(redmineService.createMonitoringIssue(anyLong()))
                .thenReturn(12345);

        // When
        MonitoringResultDTO result = monitoringService.executeMonitoring(analysisPeriod);

        // Then
        assertNotNull(result);
        assertEquals(1250, result.getTotalTransactions());
        assertEquals(new BigDecimal("125000000"), result.getTotalAmount());
        assertEquals(8, result.getAbnormalCount()); // 5 + 3
        assertEquals(MonitoringStatus.COMPLETED, result.getStatus());
        assertEquals("12345", result.getRedmineIssueId());

        verify(transactionAnalysisService, times(1)).analyzeTransactions(any(), any(), eq(analysisPeriod));
        verify(templateGenerator, times(1)).generateTransactionReportHtml(sampleAnalysisVO);
        verify(monitoringResultRepository, times(1)).save(any(MonitoringResult.class));
        verify(redmineService, times(1)).createMonitoringIssue(anyLong());
    }

    @Test
    @DisplayName("특정 기간 모니터링 실행 테스트")
    void testExecuteMonitoringWithPeriod() {
        // Given
        LocalDateTime startDate = LocalDateTime.of(2024, 1, 15, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(2024, 1, 15, 23, 59);
        String analysisPeriod = "09:00";

        when(transactionAnalysisService.analyzeTransactions(startDate, endDate, analysisPeriod))
                .thenReturn(sampleAnalysisVO);
        when(templateGenerator.generateTransactionReportHtml(sampleAnalysisVO))
                .thenReturn("<html>Sample Report</html>");
        when(monitoringResultRepository.save(any(MonitoringResult.class)))
                .thenReturn(sampleMonitoringResult);

        // When
        MonitoringResultDTO result = monitoringService.executeMonitoring(startDate, endDate, analysisPeriod);

        // Then
        assertNotNull(result);
        assertEquals(1250, result.getTotalTransactions());
        assertEquals(MonitoringStatus.COMPLETED, result.getStatus());

        verify(transactionAnalysisService, times(1)).analyzeTransactions(startDate, endDate, analysisPeriod);
    }

    @Test
    @DisplayName("모니터링 결과 조회 테스트")
    void testGetMonitoringResult() {
        // Given
        Long resultId = 1L;
        when(monitoringResultRepository.findById(resultId))
                .thenReturn(Optional.of(sampleMonitoringResult));

        // When
        MonitoringResultDTO result = monitoringService.getMonitoringResult(resultId);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("09:00", result.getAnalysisPeriod());
        assertEquals(1250, result.getTotalTransactions());

        verify(monitoringResultRepository, times(1)).findById(resultId);
    }

    @Test
    @DisplayName("모니터링 결과 조회 테스트 - 결과가 없는 경우")
    void testGetMonitoringResult_NotFound() {
        // Given
        Long resultId = 999L;
        when(monitoringResultRepository.findById(resultId))
                .thenReturn(Optional.empty());

        // When
        MonitoringResultDTO result = monitoringService.getMonitoringResult(resultId);

        // Then
        assertNull(result);

        verify(monitoringResultRepository, times(1)).findById(resultId);
    }

    @Test
    @DisplayName("모니터링 결과 저장 테스트")
    void testSaveMonitoringResult() {
        // Given
        MonitoringResultDTO inputDTO = new MonitoringResultDTO();
        inputDTO.setAnalysisPeriod("13:00");
        inputDTO.setTotalTransactions(1000);
        inputDTO.setTotalAmount(new BigDecimal("100000000"));
        inputDTO.setAbnormalCount(5);
        inputDTO.setStatus(MonitoringStatus.COMPLETED);

        when(monitoringResultRepository.save(any(MonitoringResult.class)))
                .thenReturn(sampleMonitoringResult);

        // When
        MonitoringResultDTO result = monitoringService.saveMonitoringResult(inputDTO);

        // Then
        assertNotNull(result);
        assertEquals("09:00", result.getAnalysisPeriod()); // 저장된 결과 반환

        verify(monitoringResultRepository, times(1)).save(any(MonitoringResult.class));
    }
} 