package com.piotgreen.piotgreen.repository;

import com.piotgreen.piotgreen.entity.LightData;
import com.piotgreen.piotgreen.entity.ManagerData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ManagerDataRepository extends JpaRepository<ManagerData, Long> {
//    List<ManagerData> findAll();
//    void deleteByPhoneNumber(String phoneNumber);
    @Modifying
    @Query("DELETE FROM ManagerData m WHERE m.phoneNumber = :phoneNumber")
    void deleteByPhoneNumber(@Param("phoneNumber") String phoneNumber);

}
