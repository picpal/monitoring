package com.picpal.framework.monitoring.service;

import com.picpal.framework.common.enums.MonitoringStatus;
import com.picpal.framework.monitoring.dto.MonitoringResultDTO;
import com.picpal.framework.monitoring.repository.MonitoringResultRepository;
import com.picpal.framework.monitoring.repository.model.MonitoringResult;
import com.picpal.framework.monitoring.service.impl.MonitoringServiceImpl;
import com.picpal.framework.monitoring.vo.TransactionAnalysisVO;
import com.picpal.framework.common.utils.ThymeleafTemplateGenerator;
import com.picpal.framework.redmine.dto.RedmineIssueDTO;
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
        when(redmineService.createIssue(anyString(), any(RedmineIssueDTO.class)))
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
        verify(redmineService, times(1)).createIssue(anyString(), any(RedmineIssueDTO.class));
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

    @Test
    @DisplayName("거래 데이터 분석 중 예외 발생 시")
    void testExecuteMonitoring_AnalyzeException() {
        String analysisPeriod = "09:00";
        when(transactionAnalysisService.analyzeTransactions(any(), any(), eq(analysisPeriod)))
                .thenThrow(new RuntimeException("분석 오류"));
        assertThrows(Exception.class, () -> monitoringService.executeMonitoring(analysisPeriod));
    }

    @Test
    @DisplayName("HTML 생성 중 예외 발생 시")
    void testExecuteMonitoring_HtmlException() {
        String analysisPeriod = "09:00";
        when(transactionAnalysisService.analyzeTransactions(any(), any(), eq(analysisPeriod)))
                .thenReturn(sampleAnalysisVO);
        when(templateGenerator.generateTransactionReportHtml(any()))
                .thenThrow(new RuntimeException("HTML 오류"));
        assertThrows(Exception.class, () -> monitoringService.executeMonitoring(analysisPeriod));
    }

    @Test
    @DisplayName("결과 저장 중 예외 발생 시")
    void testExecuteMonitoring_SaveException() {
        String analysisPeriod = "09:00";
        when(transactionAnalysisService.analyzeTransactions(any(), any(), eq(analysisPeriod)))
                .thenReturn(sampleAnalysisVO);
        when(templateGenerator.generateTransactionReportHtml(any()))
                .thenReturn("<html>Sample Report</html>");
        when(monitoringResultRepository.save(any(MonitoringResult.class)))
                .thenThrow(new RuntimeException("저장 오류"));
        assertThrows(Exception.class, () -> monitoringService.executeMonitoring(analysisPeriod));
    }

    @Test
    @DisplayName("Redmine 이슈 생성 중 예외 발생 시")
    void testExecuteMonitoring_RedmineException() {
        String analysisPeriod = "09:00";
        when(transactionAnalysisService.analyzeTransactions(any(), any(), eq(analysisPeriod)))
                .thenReturn(sampleAnalysisVO);
        when(templateGenerator.generateTransactionReportHtml(any()))
                .thenReturn("<html>Sample Report</html>");
        when(monitoringResultRepository.save(any(MonitoringResult.class)))
                .thenReturn(sampleMonitoringResult);
        when(redmineService.createIssue(anyString(), any(RedmineIssueDTO.class)))
                .thenThrow(new RuntimeException("Redmine 오류"));
        MonitoringResultDTO result = monitoringService.executeMonitoring(analysisPeriod);
        assertNull(result.getRedmineIssueId());
    }

    @Test
    @DisplayName("이상 거래가 없는 경우 Redmine 이슈 생성 안함")
    void testExecuteMonitoring_NoAbnormal() {
        String analysisPeriod = "09:00";
        TransactionAnalysisVO vo = new TransactionAnalysisVO();
        vo.setTotalTransactions(100);
        vo.setTotalAmount(new BigDecimal("10000"));
        vo.setFraudCount(0);
        vo.setHighRiskCount(0);
        MonitoringResult noAbnormalResult = MonitoringResult.builder()
                .monitoringDate(LocalDateTime.now())
                .analysisPeriod(analysisPeriod)
                .totalTransactions(100)
                .totalAmount(new BigDecimal("10000"))
                .abnormalCount(0)
                .htmlContent("<html>Sample Report</html>")
                .status(MonitoringStatus.COMPLETED)
                .build();
        noAbnormalResult.setId(2L);
        when(transactionAnalysisService.analyzeTransactions(any(), any(), eq(analysisPeriod)))
                .thenReturn(vo);
        when(templateGenerator.generateTransactionReportHtml(any()))
                .thenReturn("<html>Sample Report</html>");
        when(monitoringResultRepository.save(any(MonitoringResult.class)))
                .thenReturn(noAbnormalResult);
        MonitoringResultDTO result = monitoringService.executeMonitoring(analysisPeriod);
        assertNull(result.getRedmineIssueId());
        verify(redmineService, never()).createIssue(anyString(), any(RedmineIssueDTO.class));
    }

    @Test
    @DisplayName("getLatestMonitoringResult 정상 동작")
    void testGetLatestMonitoringResult() {
        MonitoringResultDTO result = monitoringService.getLatestMonitoringResult("09:00");
        assertNotNull(result);
        assertEquals("09:00", result.getAnalysisPeriod());
        assertEquals(MonitoringStatus.COMPLETED, result.getStatus());
        assertEquals(0, result.getTotalTransactions());
    }

    @Test
    @DisplayName("updateRedmineIssueId 정상 동작")
    void testUpdateRedmineIssueId() {
        MonitoringResult result = MonitoringResult.builder()
                .monitoringDate(LocalDateTime.now())
                .analysisPeriod("09:00")
                .totalTransactions(100)
                .totalAmount(new BigDecimal("10000"))
                .abnormalCount(1)
                .htmlContent("<html>Sample Report</html>")
                .status(MonitoringStatus.COMPLETED)
                .build();
        result.setId(3L);
        when(monitoringResultRepository.findById(3L)).thenReturn(Optional.of(result));
        when(monitoringResultRepository.save(any(MonitoringResult.class))).thenReturn(result);
        // private method라서 reflection으로 호출
        assertDoesNotThrow(() -> {
            java.lang.reflect.Method m = MonitoringServiceImpl.class.getDeclaredMethod("updateRedmineIssueId", Long.class, String.class);
            m.setAccessible(true);
            m.invoke(monitoringService, 3L, "RID-123");
        });
        assertEquals("RID-123", result.getRedmineIssueId());
    }

    @Test
    @DisplayName("updateRedmineIssueId - 결과 없음")
    void testUpdateRedmineIssueId_NotFound() {
        when(monitoringResultRepository.findById(4L)).thenReturn(Optional.empty());
        assertDoesNotThrow(() -> {
            java.lang.reflect.Method m = MonitoringServiceImpl.class.getDeclaredMethod("updateRedmineIssueId", Long.class, String.class);
            m.setAccessible(true);
            m.invoke(monitoringService, 4L, "RID-999");
        });
    }
} 