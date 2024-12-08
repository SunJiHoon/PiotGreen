package com.piotgreen.piotgreen.repository;

import com.piotgreen.piotgreen.entity.IntrusionData;
import com.piotgreen.piotgreen.entity.IrrigationData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface IntrusionDataRepository extends JpaRepository<IntrusionData, Long> {
    List<IntrusionData> findAllByOrderByTimestampAsc();
    List<IntrusionData> findAllByOrderByTimestampDesc();

    @Query("SELECT l FROM IntrusionData l WHERE YEAR(l.timestamp) = :year AND MONTH(l.timestamp) = :month")
    List<IntrusionData> findAllByYearAndMonth(@Param("year") int year, @Param("month") int month);

    @Query("SELECT i FROM IntrusionData i ORDER BY i.timestamp DESC LIMIT 1")
    IntrusionData findMostRecentIntrusionData();

    @Query("SELECT COUNT(i) > 0 FROM IntrusionData i WHERE i.dangerLevel = 'danger' AND i.timestamp >= :startTime")
    boolean existsDangerWithinLast10Minutes(@Param("startTime") LocalDateTime startTime);

}
