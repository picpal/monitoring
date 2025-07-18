package com.picpal.framework.monitoring.service;

import com.picpal.framework.monitoring.dto.RedmineIssueDTO;
import com.picpal.framework.monitoring.repository.MonitoringResultRepository;
import com.picpal.framework.monitoring.repository.model.MonitoringResult;
import com.picpal.framework.common.enums.MonitoringStatus;
import com.picpal.framework.monitoring.service.impl.RedmineServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RedmineServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private MonitoringResultRepository monitoringResultRepository;

    private RedmineServiceImpl redmineService;

    @BeforeEach
    void setUp() {
        redmineService = new RedmineServiceImpl(restTemplate, monitoringResultRepository);
        
        // Reflection을 사용하여 private 필드 설정
        try {
            java.lang.reflect.Field urlField = RedmineServiceImpl.class.getDeclaredField("redmineApiUrl");
            urlField.setAccessible(true);
            urlField.set(redmineService, "http://localhost:3000");

            java.lang.reflect.Field keyField = RedmineServiceImpl.class.getDeclaredField("redmineApiKey");
            keyField.setAccessible(true);
            keyField.set(redmineService, "test_api_key");

            java.lang.reflect.Field projectField = RedmineServiceImpl.class.getDeclaredField("projectId");
            projectField.setAccessible(true);
            projectField.set(redmineService, 1);

            java.lang.reflect.Field trackerField = RedmineServiceImpl.class.getDeclaredField("trackerId");
            trackerField.setAccessible(true);
            trackerField.set(redmineService, 1);

            java.lang.reflect.Field priorityField = RedmineServiceImpl.class.getDeclaredField("priorityId");
            priorityField.setAccessible(true);
            priorityField.set(redmineService, 2);
        } catch (Exception e) {
            fail("필드 설정 실패: " + e.getMessage());
        }
    }

    @Test
    void testCreateIssue() {
        // Given
        RedmineIssueDTO issueDTO = RedmineIssueDTO.builder()
                .subject("테스트 이슈")
                .description("테스트 설명")
                .projectId(1)
                .trackerId(1)
                .priorityId(2)
                .build();

        Map<String, Object> responseBody = new HashMap<>();
        Map<String, Object> issue = new HashMap<>();
        issue.put("id", 12345);
        responseBody.put("issue", issue);

        when(restTemplate.postForEntity(anyString(), any(), eq(Map.class)))
                .thenReturn(ResponseEntity.status(HttpStatus.CREATED).body(responseBody));

        // When
        Integer issueId = redmineService.createIssue(issueDTO);

        // Then
        assertNotNull(issueId, "이슈 ID가 생성되어야 합니다.");
        assertEquals(12345, issueId, "Mock 서버에서 반환하는 이슈 ID는 12345입니다.");
    }

    @Test
    void testCreateMonitoringIssue() {
        // Given
        MonitoringResult result = MonitoringResult.builder()
                .monitoringDate(LocalDateTime.now())
                .analysisPeriod("09:00")
                .totalTransactions(100)
                .totalAmount(new BigDecimal("1000000"))
                .abnormalCount(5)
                .htmlContent("<html><body>테스트 리포트</body></html>")
                .status(MonitoringStatus.COMPLETED)
                .build();

        Map<String, Object> responseBody = new HashMap<>();
        Map<String, Object> issue = new HashMap<>();
        issue.put("id", 12345);
        responseBody.put("issue", issue);

        when(monitoringResultRepository.findById(1L)).thenReturn(Optional.of(result));
        when(restTemplate.postForEntity(anyString(), any(), eq(Map.class)))
                .thenReturn(ResponseEntity.status(HttpStatus.CREATED).body(responseBody));

        // When
        Integer issueId = redmineService.createMonitoringIssue(1L);

        // Then
        assertNotNull(issueId, "모니터링 이슈 ID가 생성되어야 합니다.");
        assertEquals(12345, issueId, "Mock 서버에서 반환하는 이슈 ID는 12345입니다.");
    }

    @Test
    void testCreateMonitoringIssueWithError() {
        // Given
        MonitoringResult result = MonitoringResult.builder()
                .monitoringDate(LocalDateTime.now())
                .analysisPeriod("13:00")
                .totalTransactions(50)
                .totalAmount(new BigDecimal("500000"))
                .abnormalCount(0)
                .status(MonitoringStatus.FAILED)
                .errorMessage("테스트 오류 메시지")
                .build();

        Map<String, Object> responseBody = new HashMap<>();
        Map<String, Object> issue = new HashMap<>();
        issue.put("id", 12345);
        responseBody.put("issue", issue);

        when(monitoringResultRepository.findById(1L)).thenReturn(Optional.of(result));
        when(restTemplate.postForEntity(anyString(), any(), eq(Map.class)))
                .thenReturn(ResponseEntity.status(HttpStatus.CREATED).body(responseBody));

        // When
        Integer issueId = redmineService.createMonitoringIssue(1L);

        // Then
        assertNotNull(issueId, "오류가 있는 모니터링 결과도 이슈가 생성되어야 합니다.");
    }
} 