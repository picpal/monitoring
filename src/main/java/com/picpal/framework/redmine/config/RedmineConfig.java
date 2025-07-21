package com.picpal.framework.redmine.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Data
@Configuration
@ConfigurationProperties(prefix = "redmine")
public class RedmineConfig {
    private Api api = new Api();
    private Map<String, Project> projects = new HashMap<>();

    @Data
    public static class Api {
        private String url;
        private String key;
    }
    @Data
    public static class Project {
        private Integer id;
        private Integer trackerId;
        private Integer priorityId;
    }
} 