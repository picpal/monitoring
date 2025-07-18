package com.picpal.framework.common.utils;

import com.picpal.framework.monitoring.vo.TransactionAnalysisVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Slf4j
@Component
@RequiredArgsConstructor
public class ThymeleafTemplateGenerator {

    private final TemplateEngine templateEngine;

    /**
     * Thymeleaf를 사용하여 거래 분석 결과를 HTML로 변환합니다.
     */
    public String generateTransactionReportHtml(TransactionAnalysisVO analysisVO) {
        log.info("Thymeleaf를 사용한 거래 분석 결과 HTML 생성 시작");
        
        try {
            Context context = new Context();
            context.setVariable("analysis", analysisVO);
            
            String html = templateEngine.process("monitoring-report", context);
            
            log.info("Thymeleaf를 사용한 거래 분석 결과 HTML 생성 완료");
            return html;
            
        } catch (Exception e) {
            log.error("Thymeleaf HTML 생성 중 오류 발생", e);
            throw new RuntimeException("Thymeleaf HTML 생성 실패", e);
        }
    }
} 