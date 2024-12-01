package com.piotgreen.piotgreen.repository;

import com.piotgreen.piotgreen.entity.LightData;
import com.piotgreen.piotgreen.entity.ManagerData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ManagerDataRepository extends JpaRepository<ManagerData, Long> {
//    List<ManagerData> findAll();

}
