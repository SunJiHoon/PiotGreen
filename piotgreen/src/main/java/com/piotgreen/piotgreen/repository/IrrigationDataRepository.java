package com.piotgreen.piotgreen.repository;

import com.piotgreen.piotgreen.entity.IrrigationData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IrrigationDataRepository extends JpaRepository<IrrigationData, Long>{
    List<IrrigationData> findAllByOrderByTimestampAsc();
    List<IrrigationData> findAllByOrderByTimestampDesc();

}

