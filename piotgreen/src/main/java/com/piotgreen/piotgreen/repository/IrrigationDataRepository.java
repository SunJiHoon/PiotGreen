package com.piotgreen.piotgreen.repository;

import com.piotgreen.piotgreen.entity.IrrigationData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IrrigationDataRepository extends JpaRepository<IrrigationData, Long>{
}

