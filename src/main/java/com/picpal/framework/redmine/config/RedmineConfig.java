package com.picpal.framework.redmine.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "redmine")
public class RedmineConfig {
    private Api api = new Api();
    private Project project = new Project();
    private Tracker tracker = new Tracker();
    private Priority priority = new Priority();

    @Data
    public static class Api {
        private String url;
        private String key;
    }
    @Data
    public static class Project {
        private Integer id;
    }
    @Data
    public static class Tracker {
        private Integer id;
    }
    @Data
    public static class Priority {
        private Integer id;
    }
} 