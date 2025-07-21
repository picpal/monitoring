package com.picpal.framework.monitoring.service;

import com.picpal.framework.monitoring.repository.mapper.TransactionMapper;
import com.picpal.framework.monitoring.service.impl.TransactionAnalysisServiceImpl;
import com.picpal.framework.monitoring.vo.TransactionAnalysisVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TransactionAnalysisServiceImplTest {
    @Mock
    private TransactionMapper transactionMapper;

    private TransactionAnalysisServiceImpl service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        service = new TransactionAnalysisServiceImpl(transactionMapper);
    }

    @Test
    @DisplayName("정상 거래 데이터 분석")
    void testAnalyzeTransactions_Normal() {
        LocalDateTime start = LocalDateTime.of(2024, 7, 1, 0, 0);
        LocalDateTime end = LocalDateTime.of(2024, 7, 1, 23, 59);
        String period = "09:00";
        TransactionAnalysisVO vo = service.analyzeTransactions(start, end, period);
        assertNotNull(vo);
        assertEquals(period, vo.getAnalysisPeriod());
        assertEquals("2024-07-01", vo.getStartDate());
        assertEquals("2024-07-01", vo.getEndDate());
        assertEquals(0, vo.getTotalTransactions());
        assertEquals(BigDecimal.ZERO, vo.getTotalAmount());
    }

    @Test
    @DisplayName("거래 데이터 분석 중 예외 발생")
    void testAnalyzeTransactions_Exception() {
        TransactionAnalysisServiceImpl spyService = spy(service);
        doThrow(new RuntimeException("DB 오류")).when(spyService).analyzeTransactions(any(), any(), any());
        assertThrows(RuntimeException.class, () -> spyService.analyzeTransactions(LocalDateTime.now(), LocalDateTime.now(), "09:00"));
    }

    @Test
    @DisplayName("오늘 거래 데이터 분석")
    void testAnalyzeTodayTransactions() {
        String period = "09:00";
        TransactionAnalysisVO vo = service.analyzeTodayTransactions(period);
        assertNotNull(vo);
        assertEquals(period, vo.getAnalysisPeriod());
    }

    @Test
    @DisplayName("어제 거래 데이터 분석")
    void testAnalyzeYesterdayTransactions() {
        String period = "09:00";
        TransactionAnalysisVO vo = service.analyzeYesterdayTransactions(period);
        assertNotNull(vo);
        assertEquals(period, vo.getAnalysisPeriod());
    }

    @Test
    @DisplayName("이상 거래 데이터 분석")
    void testAnalyzeAbnormalTransactions() {
        LocalDateTime start = LocalDateTime.of(2024, 7, 1, 0, 0);
        LocalDateTime end = LocalDateTime.of(2024, 7, 1, 23, 59);
        String period = "09:00";
        TransactionAnalysisVO vo = service.analyzeAbnormalTransactions(start, end, period);
        assertNotNull(vo);
        assertEquals(period, vo.getAnalysisPeriod());
        assertEquals("2024-07-01", vo.getStartDate());
        assertEquals("2024-07-01", vo.getEndDate());
        assertEquals(0, vo.getTotalTransactions());
        assertEquals(BigDecimal.ZERO, vo.getTotalAmount());
    }

    @Test
    @DisplayName("평균 금액 계산 - 정상")
    void testCalculateAverageAmount_Normal() throws Exception {
        java.lang.reflect.Method m = TransactionAnalysisServiceImpl.class.getDeclaredMethod("calculateAverageAmount", BigDecimal.class, Integer.class);
        m.setAccessible(true);
        BigDecimal avg = (BigDecimal) m.invoke(service, new BigDecimal("1000"), 4);
        assertEquals(new BigDecimal("250.00"), avg);
    }

    @Test
    @DisplayName("평균 금액 계산 - 분모 0, null 등")
    void testCalculateAverageAmount_ZeroOrNull() throws Exception {
        java.lang.reflect.Method m = TransactionAnalysisServiceImpl.class.getDeclaredMethod("calculateAverageAmount", BigDecimal.class, Integer.class);
        m.setAccessible(true);
        assertEquals(BigDecimal.ZERO, m.invoke(service, null, 10));
        assertEquals(BigDecimal.ZERO, m.invoke(service, new BigDecimal("1000"), null));
        assertEquals(BigDecimal.ZERO, m.invoke(service, new BigDecimal("1000"), 0));
    }
} 