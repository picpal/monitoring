package com.picpal.framework.redmine.service.impl;

import com.picpal.framework.redmine.dto.RedmineIssueDTO;
import com.picpal.framework.redmine.service.RedmineService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.HttpClientErrorException;

import java.util.HashMap;
import java.util.Map;
import com.picpal.framework.redmine.exception.RedmineException;
import com.picpal.framework.redmine.config.RedmineConfig;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedmineServiceImpl implements RedmineService {

    private final RestTemplate restTemplate;
    private final RedmineConfig redmineConfig;

    @Override
    public Integer createIssue(String projectKey, RedmineIssueDTO issueDTO) {
        RedmineConfig.Project projectConfig = redmineConfig.getProjects().get(projectKey);
        if (projectConfig == null) {
            throw new RedmineException("Redmine 프로젝트 설정을 찾을 수 없습니다: " + projectKey);
        }
        RedmineConfig.Api apiConfig = redmineConfig.getApi();
        try {
            Map<String, Object> requestBody = new HashMap<>();
            Map<String, Object> issue = new HashMap<>();
            issue.put("subject", issueDTO.getSubject());
            issue.put("description", issueDTO.getDescription());
            issue.put("project_id", issueDTO.getProjectId() != null ? issueDTO.getProjectId() : projectConfig.getId());
            issue.put("tracker_id", issueDTO.getTrackerId() != null ? issueDTO.getTrackerId() : projectConfig.getTrackerId());
            issue.put("priority_id", issueDTO.getPriorityId() != null ? issueDTO.getPriorityId() : projectConfig.getPriorityId());
            requestBody.put("issue", issue);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("X-Redmine-API-Key", apiConfig.getKey());
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
            String url = apiConfig.getUrl() + "/issues.json";
            ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);
            if (response.getStatusCode() == HttpStatus.CREATED && response.getBody() != null) {
                Map<String, Object> issueResponse = (Map<String, Object>) response.getBody().get("issue");
                Integer issueId = (Integer) issueResponse.get("id");
                log.info("Redmine 이슈 생성 성공: {}", issueId);
                return issueId;
            } else {
                log.error("Redmine 이슈 생성 실패: {}", response.getStatusCode());
                throw new RedmineException("Redmine 이슈 생성 실패: " + response.getStatusCode());
            }
        } catch (HttpClientErrorException e) {
            log.error("Redmine API 호출 실패: {}", e.getMessage());
            throw new RedmineException("Redmine API 호출 실패", e);
        } catch (Exception e) {
            log.error("Redmine 이슈 생성 중 예외 발생", e);
            throw new RedmineException("Redmine 이슈 생성 중 예외 발생", e);
        }
    }

    @Override
    public boolean testConnection() {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("X-Redmine-API-Key", redmineConfig.getApi().getKey());
            
            HttpEntity<String> request = new HttpEntity<>(headers);
            String url = redmineConfig.getApi().getUrl() + "/projects.json";
            
            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, request, Map.class);
            
            boolean isConnected = response.getStatusCode() == HttpStatus.OK;
            log.info("Redmine 연결 테스트 결과: {}", isConnected);
            return isConnected;
            
        } catch (Exception e) {
            log.error("Redmine 연결 테스트 실패", e);
            throw new RedmineException("Redmine 연결 테스트 실패", e);
        }
    }
} 