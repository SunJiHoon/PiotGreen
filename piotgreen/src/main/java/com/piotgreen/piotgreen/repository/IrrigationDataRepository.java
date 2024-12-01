package com.piotgreen.piotgreen.repository;

import com.piotgreen.piotgreen.entity.IrrigationData;
import com.piotgreen.piotgreen.entity.LightData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IrrigationDataRepository extends JpaRepository<IrrigationData, Long>{
    List<IrrigationData> findAllByOrderByTimestampAsc();
    List<IrrigationData> findAllByOrderByTimestampDesc();

    @Query("SELECT l FROM IrrigationData l WHERE YEAR(l.timestamp) = :year AND MONTH(l.timestamp) = :month")
    List<IrrigationData> findAllByYearAndMonth(@Param("year") int year, @Param("month") int month);

    @Query("SELECT i FROM IrrigationData i ORDER BY i.timestamp DESC LIMIT 1")
    IrrigationData findMostRecentIrrigationData();
}

