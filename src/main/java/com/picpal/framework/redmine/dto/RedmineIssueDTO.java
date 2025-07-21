package com.picpal.framework.redmine.dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RedmineIssueDTO {
    private String subject;
    private String description;
    private Integer projectId;
    private Integer trackerId;
    private Integer priorityId;
    private String customFields;
    private String notes;
} 