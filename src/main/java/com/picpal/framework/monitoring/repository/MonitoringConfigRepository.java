package com.picpal.framework.monitoring.repository;

import com.picpal.framework.monitoring.repository.model.MonitoringConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MonitoringConfigRepository extends JpaRepository<MonitoringConfig, Long> {

    /**
     * 활성화된 설정 조회
     */
    List<MonitoringConfig> findByIsActiveTrue();

    /**
     * 설정명으로 설정 조회
     */
    Optional<MonitoringConfig> findByConfigName(String configName);

    /**
     * 활성화된 설정명으로 설정 조회
     */
    Optional<MonitoringConfig> findByConfigNameAndIsActiveTrue(String configName);

    /**
     * 설정명 패턴으로 설정 조회
     */
    @Query("SELECT mc FROM MonitoringConfig mc WHERE mc.configName LIKE %:pattern% AND mc.isActive = true")
    List<MonitoringConfig> findByConfigNamePattern(@Param("pattern") String pattern);

    /**
     * 특정 설정 그룹 조회 (예: redmine.*, email.*)
     */
    @Query("SELECT mc FROM MonitoringConfig mc WHERE mc.configName LIKE :groupPattern% AND mc.isActive = true")
    List<MonitoringConfig> findByConfigGroup(@Param("groupPattern") String groupPattern);

    /**
     * 설정 존재 여부 확인
     */
    boolean existsByConfigNameAndIsActiveTrue(String configName);

    /**
     * 모든 설정명 조회
     */
    @Query("SELECT mc.configName FROM MonitoringConfig mc WHERE mc.isActive = true")
    List<String> findAllActiveConfigNames();
} 