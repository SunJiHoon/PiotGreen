package com.piotgreen.piotgreen.repository;

import com.piotgreen.piotgreen.entity.CommandData;
import com.piotgreen.piotgreen.entity.IntrusionData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CommandDataRepository extends JpaRepository<CommandData, Long> {

    @Query("SELECT c " +
            "FROM CommandData c " +
            "where c.category = 'irrigation' and c.command = 'mode' " +
            "ORDER BY c.timestamp DESC " +
            "LIMIT 1")
    CommandData findMostRecentIrrigationModeCommandData();

    @Query("SELECT c " +
            "FROM CommandData c " +
            "where c.category = 'irrigation' and c.command = 'wantHumidity' " +
            "ORDER BY c.timestamp DESC " +
            "LIMIT 1")
    CommandData findMostRecentIrrigationWantHumidityCommandData();

    @Query("SELECT c " +
            "FROM CommandData c " +
            "where c.category = 'intrusion' and c.command = 'mode' " +
            "ORDER BY c.timestamp DESC " +
            "LIMIT 1")
    CommandData findMostRecentIntrusionModeCommandData();

    @Query("SELECT c " +
            "FROM CommandData c " +
            "where c.category = 'intrusion' and c.command = 'security' " +
            "ORDER BY c.timestamp DESC " +
            "LIMIT 1")
    CommandData findMostRecentIntrusionSecurityCommandData();

    @Query("SELECT c " +
            "FROM CommandData c " +
            "where c.category = 'lighting' and c.command = 'mode' " +
            "ORDER BY c.timestamp DESC " +
            "LIMIT 1")
    CommandData findMostRecentLightingModeCommandData();

    @Query("SELECT c " +
            "FROM CommandData c " +
            "where c.category = 'lighting' and c.command = 'led1' " +
            "ORDER BY c.timestamp DESC " +
            "LIMIT 1")
    CommandData findMostRecentLightingLed1CommandData();

    @Query("SELECT c " +
            "FROM CommandData c " +
            "where c.category = 'lighting' and c.command = 'led2' " +
            "ORDER BY c.timestamp DESC " +
            "LIMIT 1")
    CommandData findMostRecentLightingLed2CommandData();
}
