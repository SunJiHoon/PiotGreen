package com.piotgreen.piotgreen.repository;

import com.piotgreen.piotgreen.entity.IntrusionData;
import com.piotgreen.piotgreen.entity.IrrigationData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IntrusionDataRepository extends JpaRepository<IntrusionData, Long> {
    List<IntrusionData> findAllByOrderByTimestampAsc();
    List<IntrusionData> findAllByOrderByTimestampDesc();

    @Query("SELECT l FROM IntrusionData l WHERE YEAR(l.timestamp) = :year AND MONTH(l.timestamp) = :month")
    List<IntrusionData> findAllByYearAndMonth(@Param("year") int year, @Param("month") int month);

}
