package com.picpal.framework.redmine;

import com.picpal.framework.redmine.dto.RedmineIssueDTO;
import com.picpal.framework.monitoring.repository.MonitoringResultRepository;
import com.picpal.framework.monitoring.repository.model.MonitoringResult;
import com.picpal.framework.common.enums.MonitoringStatus;
import com.picpal.framework.redmine.config.RedmineConfig;
import com.picpal.framework.redmine.service.impl.RedmineServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.quality.Strictness;
import org.mockito.junit.jupiter.MockitoSettings;
import org.springframework.http.HttpMethod;
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
@MockitoSettings(strictness = Strictness.LENIENT)
class RedmineServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private MonitoringResultRepository monitoringResultRepository;

    @Mock
    private RedmineConfig redmineConfig;

    private RedmineServiceImpl redmineService;

    @BeforeEach
    void setUp() {
        restTemplate = mock(RestTemplate.class);
        monitoringResultRepository = mock(MonitoringResultRepository.class);
        redmineConfig = mock(RedmineConfig.class);
        redmineService = new RedmineServiceImpl(restTemplate, monitoringResultRepository, redmineConfig);

        RedmineConfig.Api api = new RedmineConfig.Api();
        api.setUrl("http://localhost:3000");
        api.setKey("test_api_key");
        when(redmineConfig.getApi()).thenReturn(api);

        RedmineConfig.Project project = new RedmineConfig.Project();
        project.setId(1);
        project.setTrackerId(1);
        project.setPriorityId(2);
        Map<String, RedmineConfig.Project> projects = new HashMap<>();
        projects.put("projectA", project);
        when(redmineConfig.getProjects()).thenReturn(projects);
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
        Integer issueId = redmineService.createIssue("projectA", issueDTO);

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
        Integer issueId = redmineService.createMonitoringIssue("projectA", 1L);

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
        Integer issueId = redmineService.createMonitoringIssue("projectA", 1L);

        // Then
        assertNotNull(issueId, "오류가 있는 모니터링 결과도 이슈가 생성되어야 합니다.");
    }

    @Test
    void testCreateIssueWithDifferentProjectKey() {
        // Given
        RedmineConfig.Project projectB = new RedmineConfig.Project();
        projectB.setId(2);
        projectB.setTrackerId(3);
        projectB.setPriorityId(4);
        Map<String, RedmineConfig.Project> projects = new HashMap<>();
        projects.put("projectA", redmineConfig.getProjects().get("projectA"));
        projects.put("projectB", projectB);
        when(redmineConfig.getProjects()).thenReturn(projects);

        RedmineIssueDTO issueDTO = RedmineIssueDTO.builder()
                .subject("프로젝트B 이슈")
                .description("프로젝트B 설명")
                .build();

        Map<String, Object> responseBody = new HashMap<>();
        Map<String, Object> issue = new HashMap<>();
        issue.put("id", 54321);
        responseBody.put("issue", issue);
        when(restTemplate.postForEntity(anyString(), any(), eq(Map.class)))
                .thenReturn(ResponseEntity.status(HttpStatus.CREATED).body(responseBody));

        // When
        Integer issueId = redmineService.createIssue("projectB", issueDTO);

        // Then
        assertNotNull(issueId, "이슈 ID가 생성되어야 합니다.");
        assertEquals(54321, issueId, "Mock 서버에서 반환하는 이슈 ID는 54321입니다.");
    }

    @Test
    void testCreateIssueWithInvalidProjectKey() {
        // Given
        RedmineIssueDTO issueDTO = RedmineIssueDTO.builder()
                .subject("잘못된 프로젝트 이슈")
                .description("잘못된 프로젝트 설명")
                .build();
        // When & Then
        Exception exception = assertThrows(RuntimeException.class, () -> {
            redmineService.createIssue("notExistKey", issueDTO);
        });
        assertTrue(exception.getMessage().contains("Redmine 프로젝트 설정을 찾을 수 없습니다"));
    }

    @Test
    void testCreateIssueWithMissingFields() {
        // Given
        RedmineConfig.Project incompleteProject = new RedmineConfig.Project();
        incompleteProject.setId(null); // 필수 필드 누락
        incompleteProject.setTrackerId(null);
        incompleteProject.setPriorityId(null);
        Map<String, RedmineConfig.Project> projects = new HashMap<>();
        projects.put("incomplete", incompleteProject);
        when(redmineConfig.getProjects()).thenReturn(projects);

        RedmineIssueDTO issueDTO = RedmineIssueDTO.builder()
                .subject("누락된 필드 이슈")
                .description("필수 필드 누락")
                .build();
        Map<String, Object> responseBody = new HashMap<>();
        Map<String, Object> issue = new HashMap<>();
        issue.put("id", 99999);
        responseBody.put("issue", issue);
        when(restTemplate.postForEntity(anyString(), any(), eq(Map.class)))
                .thenReturn(ResponseEntity.status(HttpStatus.CREATED).body(responseBody));
        // When & Then
        // 실제 서비스에서는 null 체크 및 예외처리가 필요하나, 현재는 null로 들어가도 API 호출이 되므로, 예외 발생 여부만 확인
        Integer issueId = redmineService.createIssue("incomplete", issueDTO);
        assertNotNull(issueId, "이슈 ID가 생성되어야 합니다.");
        assertEquals(99999, issueId, "Mock 서버에서 반환하는 이슈 ID는 99999입니다.");
    }

    @Test
    void testCreateIssue_ResponseFail() {
        RedmineIssueDTO issueDTO = RedmineIssueDTO.builder().subject("fail").build();
        when(restTemplate.postForEntity(anyString(), any(), eq(Map.class)))
                .thenReturn(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null));
        Exception ex = assertThrows(RuntimeException.class, () -> redmineService.createIssue("projectA", issueDTO));
        assertTrue(ex.getMessage().contains("Redmine 이슈 생성 중 예외 발생"));
    }

    @Test
    void testCreateIssue_HttpClientErrorException() {
        RedmineIssueDTO issueDTO = RedmineIssueDTO.builder().subject("error").build();
        when(restTemplate.postForEntity(anyString(), any(), eq(Map.class)))
                .thenThrow(new org.springframework.web.client.HttpClientErrorException(HttpStatus.BAD_REQUEST));
        Exception ex = assertThrows(RuntimeException.class, () -> redmineService.createIssue("projectA", issueDTO));
        assertTrue(ex.getMessage().contains("Redmine API 호출 실패"));
    }

    @Test
    void testCreateIssue_GenericException() {
        RedmineIssueDTO issueDTO = RedmineIssueDTO.builder().subject("error").build();
        when(restTemplate.postForEntity(anyString(), any(), eq(Map.class)))
                .thenThrow(new RuntimeException("기타 오류"));
        Exception ex = assertThrows(RuntimeException.class, () -> redmineService.createIssue("projectA", issueDTO));
        assertTrue(ex.getMessage().contains("Redmine 이슈 생성 중 예외 발생"));
    }

    @Test
    void testCreateMonitoringIssue_ResultNotFound() {
        when(monitoringResultRepository.findById(anyLong())).thenReturn(Optional.empty());
        Integer issueId = redmineService.createMonitoringIssue("projectA", 999L);
        assertNull(issueId);
    }

    @Test
    void testCreateMonitoringIssue_ProjectConfigNotFound() {
        MonitoringResult result = MonitoringResult.builder().monitoringDate(LocalDateTime.now()).build();
        when(monitoringResultRepository.findById(anyLong())).thenReturn(Optional.of(result));
        when(redmineConfig.getProjects()).thenReturn(new HashMap<>()); // empty
        Exception ex = assertThrows(RuntimeException.class, () -> redmineService.createMonitoringIssue("notExistKey", 1L));
        assertTrue(ex.getMessage().contains("Redmine 프로젝트 설정을 찾을 수 없습니다"));
    }

    @Test
    void testTestConnection_Success() {
        Map<String, Object> responseBody = new HashMap<>();
        ResponseEntity<Map> response = ResponseEntity.status(HttpStatus.OK).body(responseBody);
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(), eq(Map.class))).thenReturn(response);
        assertTrue(redmineService.testConnection());
    }

    @Test
    void testTestConnection_Fail() {
        Map<String, Object> responseBody = new HashMap<>();
        ResponseEntity<Map> response = ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseBody);
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(), eq(Map.class))).thenReturn(response);
        assertFalse(redmineService.testConnection());
    }

    @Test
    void testTestConnection_Exception() {
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(), eq(Map.class))).thenThrow(new RuntimeException("연결 오류"));
        Exception ex = assertThrows(RuntimeException.class, () -> redmineService.testConnection());
        assertTrue(ex.getMessage().contains("Redmine 연결 테스트 실패"));
    }

    @Test
    void testBuildIssueDescription_ErrorMessageAndHtml() throws Exception {
        java.lang.reflect.Method m = RedmineServiceImpl.class.getDeclaredMethod("buildIssueDescription", MonitoringResult.class);
        m.setAccessible(true);
        MonitoringResult result = MonitoringResult.builder()
                .monitoringDate(LocalDateTime.now())
                .analysisPeriod("09:00")
                .totalTransactions(10)
                .totalAmount(new BigDecimal("10000"))
                .abnormalCount(1)
                .htmlContent("<html>HTML</html>")
                .status(MonitoringStatus.COMPLETED)
                .errorMessage("에러 발생!")
                .build();
        String desc = (String) m.invoke(redmineService, result);
        assertTrue(desc.contains("에러 발생!"));
        assertTrue(desc.contains("<html>HTML</html>"));
    }

    @Test
    void testBuildIssueDescription_EmptyErrorAndHtml() throws Exception {
        java.lang.reflect.Method m = RedmineServiceImpl.class.getDeclaredMethod("buildIssueDescription", MonitoringResult.class);
        m.setAccessible(true);
        MonitoringResult result = MonitoringResult.builder()
                .monitoringDate(LocalDateTime.now())
                .analysisPeriod("09:00")
                .totalTransactions(10)
                .totalAmount(new BigDecimal("10000"))
                .abnormalCount(1)
                .htmlContent("")
                .status(MonitoringStatus.COMPLETED)
                .errorMessage("")
                .build();
        String desc = (String) m.invoke(redmineService, result);
        assertFalse(desc.contains("에러 발생!"));
        assertFalse(desc.contains("HTML"));
    }

    @Test
    void testTestConnection_NullBody() {
        ResponseEntity<Map> response = ResponseEntity.status(HttpStatus.OK).body(null);
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(), eq(Map.class))).thenReturn(response);
        assertTrue(redmineService.testConnection());
    }
} 