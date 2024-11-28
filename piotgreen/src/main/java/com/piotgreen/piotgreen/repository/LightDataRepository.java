package com.piotgreen.piotgreen.repository;

import com.piotgreen.piotgreen.entity.LightData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LightDataRepository extends JpaRepository<LightData, Long> {
    List<LightData> findAllByOrderByTimestampAsc();
    List<LightData> findAllByOrderByTimestampDesc();

}
