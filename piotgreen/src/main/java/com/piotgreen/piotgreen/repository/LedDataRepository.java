package com.piotgreen.piotgreen.repository;

import com.piotgreen.piotgreen.entity.LedData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface LedDataRepository extends JpaRepository<LedData, Long> {
}