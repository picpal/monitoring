package com.picpal.framework.monitoring.repository.model;

import com.picpal.framework.common.enums.MonitoringStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "monitoring_result")
@EntityListeners(AuditingEntityListener.class)
public class MonitoringResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "monitoring_date", nullable = false)
    private LocalDateTime monitoringDate;

    @Column(name = "analysis_period", nullable = false, length = 20)
    private String analysisPeriod; // '09:00', '13:00', '18:00'

    @Column(name = "total_transactions")
    private Integer totalTransactions;

    @Column(name = "total_amount", precision = 15, scale = 2)
    private BigDecimal totalAmount;

    @Column(name = "abnormal_count")
    private Integer abnormalCount;

    @Column(name = "html_content", columnDefinition = "TEXT")
    private String htmlContent;

    @Column(name = "redmine_issue_id", length = 50)
    private String redmineIssueId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20)
    private MonitoringStatus status = MonitoringStatus.COMPLETED;

    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Builder 패턴을 위한 정적 내부 클래스
    public static class Builder {
        private LocalDateTime monitoringDate;
        private String analysisPeriod;
        private Integer totalTransactions;
        private BigDecimal totalAmount;
        private Integer abnormalCount;
        private String htmlContent;
        private String redmineIssueId;
        private MonitoringStatus status;
        private String errorMessage;

        public Builder monitoringDate(LocalDateTime monitoringDate) {
            this.monitoringDate = monitoringDate;
            return this;
        }

        public Builder analysisPeriod(String analysisPeriod) {
            this.analysisPeriod = analysisPeriod;
            return this;
        }

        public Builder totalTransactions(Integer totalTransactions) {
            this.totalTransactions = totalTransactions;
            return this;
        }

        public Builder totalAmount(BigDecimal totalAmount) {
            this.totalAmount = totalAmount;
            return this;
        }

        public Builder abnormalCount(Integer abnormalCount) {
            this.abnormalCount = abnormalCount;
            return this;
        }

        public Builder htmlContent(String htmlContent) {
            this.htmlContent = htmlContent;
            return this;
        }

        public Builder redmineIssueId(String redmineIssueId) {
            this.redmineIssueId = redmineIssueId;
            return this;
        }

        public Builder status(MonitoringStatus status) {
            this.status = status;
            return this;
        }

        public Builder errorMessage(String errorMessage) {
            this.errorMessage = errorMessage;
            return this;
        }

        public MonitoringResult build() {
            MonitoringResult result = new MonitoringResult();
            result.monitoringDate = this.monitoringDate;
            result.analysisPeriod = this.analysisPeriod;
            result.totalTransactions = this.totalTransactions;
            result.totalAmount = this.totalAmount;
            result.abnormalCount = this.abnormalCount;
            result.htmlContent = this.htmlContent;
            result.redmineIssueId = this.redmineIssueId;
            result.status = this.status != null ? this.status : MonitoringStatus.COMPLETED;
            result.errorMessage = this.errorMessage;
            return result;
        }
    }

    public static Builder builder() {
        return new Builder();
    }
} 