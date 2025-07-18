package com.picpal.framework.monitoring.repository.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "monitoring_config")
@EntityListeners(AuditingEntityListener.class)
public class MonitoringConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "config_name", nullable = false, length = 100)
    private String configName;

    @Column(name = "config_value", columnDefinition = "TEXT")
    private String configValue;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Builder 패턴을 위한 정적 내부 클래스
    public static class Builder {
        private String configName;
        private String configValue;
        private Boolean isActive;

        public Builder configName(String configName) {
            this.configName = configName;
            return this;
        }

        public Builder configValue(String configValue) {
            this.configValue = configValue;
            return this;
        }

        public Builder isActive(Boolean isActive) {
            this.isActive = isActive;
            return this;
        }

        public MonitoringConfig build() {
            MonitoringConfig config = new MonitoringConfig();
            config.configName = this.configName;
            config.configValue = this.configValue;
            config.isActive = this.isActive != null ? this.isActive : true;
            return config;
        }
    }

    public static Builder builder() {
        return new Builder();
    }
} 