package com.picpal.framework.monitoring.repository.mapper;

import com.picpal.framework.monitoring.repository.model.TransactionData;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Mapper
@Repository
public interface TransactionMapper {

    /**
     * 특정 기간의 모든 거래 데이터 조회
     */
    List<TransactionData> selectTransactionsByDateRange(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);


} 