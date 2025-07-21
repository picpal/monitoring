package com.picpal.framework.redmine.service;

import com.picpal.framework.redmine.dto.RedmineIssueDTO;

public interface RedmineService {
    /**
     * Redmine에 이슈를 생성합니다.
     * @param issueDTO 이슈 정보
     * @return 생성된 이슈 ID
     */
    Integer createIssue(RedmineIssueDTO issueDTO);
    
    /**
     * 모니터링 결과를 기반으로 Redmine 이슈를 생성합니다.
     * @param monitoringResultId 모니터링 결과 ID
     * @return 생성된 이슈 ID
     */
    Integer createMonitoringIssue(Long monitoringResultId);
    
    /**
     * Redmine 연결 상태를 확인합니다.
     * @return 연결 성공 여부
     */
    boolean testConnection();
} 