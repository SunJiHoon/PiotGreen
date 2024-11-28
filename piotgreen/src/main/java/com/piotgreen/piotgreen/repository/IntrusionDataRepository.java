package com.piotgreen.piotgreen.repository;

import com.piotgreen.piotgreen.entity.IntrusionData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IntrusionDataRepository extends JpaRepository<IntrusionData, Long> {
    List<IntrusionData> findAllByOrderByTimestampAsc();
    List<IntrusionData> findAllByOrderByTimestampDesc();
}
