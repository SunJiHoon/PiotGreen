package com.piotgreen.piotgreen.repository;

import com.piotgreen.piotgreen.entity.MessageLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface MessageLogRepository extends JpaRepository<MessageLog, Long> {
    @Query("SELECT COUNT(m) > 0 FROM MessageLog m WHERE m.timestamp >= :startTime")
    boolean existsRecentMessage(@Param("startTime") LocalDateTime startTime);
}
