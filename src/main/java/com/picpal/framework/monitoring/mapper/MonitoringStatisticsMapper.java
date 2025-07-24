package com.picpal.framework.monitoring.mapper;

import com.picpal.framework.monitoring.dto.MonitoringStatisticsDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface MonitoringStatisticsMapper {
    
    /**
     * 전체 요청 건수 조회 (70일간)
     */
    List<MonitoringStatisticsDTO.RequestCount> getAllRequestCounts(MonitoringStatisticsDTO.QueryParams params);
    
    /**
     * 승인 요청 건수 조회
     */
    List<MonitoringStatisticsDTO.RequestCount> getApprovalRequests(MonitoringStatisticsDTO.QueryParams params);
    
    /**
     * 취소 요청 건수 조회
     */
    List<MonitoringStatisticsDTO.RequestCount> getCancelRequests(MonitoringStatisticsDTO.QueryParams params);
    
    /**
     * 요청 실패 건수 조회
     */
    List<MonitoringStatisticsDTO.RequestCount> getFailRequests(MonitoringStatisticsDTO.QueryParams params);
}