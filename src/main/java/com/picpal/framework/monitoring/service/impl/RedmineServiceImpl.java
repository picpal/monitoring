package com.picpal.framework.monitoring.service.impl;

import com.picpal.framework.monitoring.dto.RedmineIssueDTO;
import com.picpal.framework.monitoring.service.RedmineService;
import com.picpal.framework.monitoring.repository.MonitoringResultRepository;
import com.picpal.framework.monitoring.repository.model.MonitoringResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.HttpClientErrorException;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedmineServiceImpl implements RedmineService {

    private final RestTemplate restTemplate;
    private final MonitoringResultRepository monitoringResultRepository;

    @Value("${redmine.api.url}")
    private String redmineApiUrl;

    @Value("${redmine.api.key}")
    private String redmineApiKey;

    @Value("${redmine.project.id}")
    private Integer projectId;

    @Value("${redmine.tracker.id}")
    private Integer trackerId;

    @Value("${redmine.priority.id}")
    private Integer priorityId;

    @Override
    public Integer createIssue(RedmineIssueDTO issueDTO) {
        try {
            Map<String, Object> requestBody = new HashMap<>();
            Map<String, Object> issue = new HashMap<>();
            
            issue.put("subject", issueDTO.getSubject());
            issue.put("description", issueDTO.getDescription());
            issue.put("project_id", issueDTO.getProjectId() != null ? issueDTO.getProjectId() : projectId);
            issue.put("tracker_id", issueDTO.getTrackerId() != null ? issueDTO.getTrackerId() : trackerId);
            issue.put("priority_id", issueDTO.getPriorityId() != null ? issueDTO.getPriorityId() : priorityId);
            
            requestBody.put("issue", issue);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("X-Redmine-API-Key", redmineApiKey);

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
            
            String url = redmineApiUrl + "/issues.json";
            ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);
            
            if (response.getStatusCode() == HttpStatus.CREATED && response.getBody() != null) {
                Map<String, Object> issueResponse = (Map<String, Object>) response.getBody().get("issue");
                Integer issueId = (Integer) issueResponse.get("id");
                log.info("Redmine 이슈 생성 성공: {}", issueId);
                return issueId;
            } else {
                log.error("Redmine 이슈 생성 실패: {}", response.getStatusCode());
                return null;
            }
            
        } catch (HttpClientErrorException e) {
            log.error("Redmine API 호출 실패: {}", e.getMessage());
            return null;
        } catch (Exception e) {
            log.error("Redmine 이슈 생성 중 예외 발생", e);
            return null;
        }
    }

    @Override
    public Integer createMonitoringIssue(Long monitoringResultId) {
        Optional<MonitoringResult> resultOpt = monitoringResultRepository.findById(monitoringResultId);
        
        if (resultOpt.isEmpty()) {
            log.error("모니터링 결과를 찾을 수 없습니다: {}", monitoringResultId);
            return null;
        }

        MonitoringResult result = resultOpt.get();
        
        RedmineIssueDTO issueDTO = RedmineIssueDTO.builder()
                .subject("모니터링 알림: " + result.getAnalysisPeriod())
                .description(buildIssueDescription(result))
                .projectId(projectId)
                .trackerId(trackerId)
                .priorityId(priorityId)
                .build();

        return createIssue(issueDTO);
    }

    @Override
    public boolean testConnection() {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("X-Redmine-API-Key", redmineApiKey);
            
            HttpEntity<String> request = new HttpEntity<>(headers);
            String url = redmineApiUrl + "/projects.json";
            
            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, request, Map.class);
            
            boolean isConnected = response.getStatusCode() == HttpStatus.OK;
            log.info("Redmine 연결 테스트 결과: {}", isConnected);
            return isConnected;
            
        } catch (Exception e) {
            log.error("Redmine 연결 테스트 실패", e);
            return false;
        }
    }

    private String buildIssueDescription(MonitoringResult result) {
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
} 