package com.piotgreen.piotgreen.repository;

import com.piotgreen.piotgreen.entity.ReserveCommandData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ReserveCommandDataRepository extends JpaRepository<ReserveCommandData, Long> {
    List<ReserveCommandData> findByCategoryAndStatus(String category, String status);
    List<ReserveCommandData> findByCategoryAndTimestampAfter(String category, LocalDateTime timestamp);
}
