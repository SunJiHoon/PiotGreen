package com.piotgreen.piotgreen.repository;

import com.piotgreen.piotgreen.entity.LedData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface LedDataRepository extends JpaRepository<LedData, Long> {
    List<LedData> findAllByOrderByTimestampAsc();
    List<LedData> findAllByOrderByTimestampDesc();
}
