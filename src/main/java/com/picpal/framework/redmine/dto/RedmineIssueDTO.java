package com.picpal.framework.redmine.dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.List;

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
    private List<CustomField> customFields;
    private String notes;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CustomField {
        private Integer id;
        private String value;
        private String name;
    }
} 