package com.piotgreen.piotgreen.repository;

import com.piotgreen.piotgreen.entity.LedData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface LedDataRepository extends JpaRepository<LedData, Long> {
    List<LedData> findAllByOrderByTimestampAsc();
    List<LedData> findAllByOrderByTimestampDesc();

    @Query("SELECT l FROM LedData l WHERE YEAR(l.timestamp) = :year AND MONTH(l.timestamp) = :month")
    List<LedData> findAllByYearAndMonth(@Param("year") int year, @Param("month") int month);

    @Query("SELECT i FROM LedData i ORDER BY i.timestamp DESC LIMIT 1")
    LedData findMostRecentLedData();
}
