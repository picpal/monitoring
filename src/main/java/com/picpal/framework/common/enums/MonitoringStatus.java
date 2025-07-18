package com.picpal.framework.common.enums;

public enum MonitoringStatus {
    PENDING("대기중"),
    PROCESSING("처리중"),
    COMPLETED("완료"),
    FAILED("실패"),
    ERROR("오류");

    private final String description;

    MonitoringStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
} 