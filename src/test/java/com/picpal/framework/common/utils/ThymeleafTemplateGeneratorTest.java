package com.picpal.framework.common.utils;

import com.picpal.framework.monitoring.vo.TransactionAnalysisVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("ThymeleafTemplateGenerator 테스트")
class ThymeleafTemplateGeneratorTest {

    @Mock
    private TemplateEngine templateEngine;

    @InjectMocks
    private ThymeleafTemplateGenerator thymeleafTemplateGenerator;

    private TransactionAnalysisVO testAnalysisVO;

    @BeforeEach
    void setUp() {
        testAnalysisVO = new TransactionAnalysisVO();
        testAnalysisVO.setTotalTransactions(100);
        testAnalysisVO.setTotalAmount(new BigDecimal("10000.00"));
        testAnalysisVO.setAverageAmount(new BigDecimal("100.00"));
        testAnalysisVO.setApprovedCount(95);
        testAnalysisVO.setDeclinedCount(5);
    }

    @Test
    @DisplayName("거래 분석 결과 HTML 생성 성공")
    void generateTransactionReportHtml_Success() {
        // given
        String expectedHtml = "<html><body>Test Report</body></html>";
        when(templateEngine.process(eq("monitoring-report"), any(Context.class)))
                .thenReturn(expectedHtml);

        // when
        String result = thymeleafTemplateGenerator.generateTransactionReportHtml(testAnalysisVO);

        // then
        assertThat(result).isEqualTo(expectedHtml);
    }

    @Test
    @DisplayName("템플릿 엔진 예외 발생 시 RuntimeException 던짐")
    void generateTransactionReportHtml_ThrowsException() {
        // given
        when(templateEngine.process(eq("monitoring-report"), any(Context.class)))
                .thenThrow(new RuntimeException("Template processing error"));

        // when & then
        assertThatThrownBy(() -> thymeleafTemplateGenerator.generateTransactionReportHtml(testAnalysisVO))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Thymeleaf HTML 생성 실패");
    }

    @Test
    @DisplayName("null 분석 데이터로 HTML 생성 시도")
    void generateTransactionReportHtml_WithNullAnalysis() {
        // given
        String expectedHtml = "<html><body>Empty Report</body></html>";
        when(templateEngine.process(eq("monitoring-report"), any(Context.class)))
                .thenReturn(expectedHtml);

        // when
        String result = thymeleafTemplateGenerator.generateTransactionReportHtml(null);

        // then
        assertThat(result).isEqualTo(expectedHtml);
    }
}