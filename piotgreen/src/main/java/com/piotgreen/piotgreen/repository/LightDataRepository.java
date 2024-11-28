package com.piotgreen.piotgreen.repository;

import com.piotgreen.piotgreen.entity.LightData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LightDataRepository extends JpaRepository<LightData, Long> {
    List<LightData> findAllByOrderByTimestampAsc();
    List<LightData> findAllByOrderByTimestampDesc();

    @Query("SELECT l FROM LightData l WHERE YEAR(l.timestamp) = :year AND MONTH(l.timestamp) = :month")
    List<LightData> findAllByYearAndMonth(@Param("year") int year, @Param("month") int month);


}
