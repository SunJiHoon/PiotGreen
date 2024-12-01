package com.piotgreen.piotgreen.service;

import com.piotgreen.piotgreen.entity.IntrusionData;
import com.piotgreen.piotgreen.entity.IrrigationData;
import com.piotgreen.piotgreen.repository.IntrusionDataRepository;
import com.piotgreen.piotgreen.repository.IrrigationDataRepository;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
public class IntrusionDataStorageService {
    private final IntrusionDataRepository intrusionDataRepository;

    public IntrusionData saveIntrusionData(String dangerLevel) {
        // 유효성 검사 (선택적으로 추가 가능)

        IntrusionData intrusionData = new IntrusionData();
        intrusionData.setDangerLevel(dangerLevel);
        intrusionData.setTimestamp(LocalDateTime.now());
        return intrusionDataRepository.save(intrusionData);
    }
    public List<IntrusionData> getAllIntrusionDataSorted() {
//        return intrusionDataRepository.findAllByOrderByTimestampAsc();
        return intrusionDataRepository.findAllByOrderByTimestampDesc();
    }




    public long getTupleCount() {
        // 테이블의 튜플 개수를 반환
        return intrusionDataRepository.count();
    }
    @PostConstruct
    public void initializeData() {
        long tupleCount = getTupleCount(); // 개수 가져오기
        System.out.println("Current tuple count: " + tupleCount);

        if (tupleCount <= 30) {
            System.out.println("Initializing intrusion data...");

            // 11월 1일부터 30일까지 데이터 생성
            LocalDateTime startDate = LocalDateTime.of(2024, 5, 1, 0, 0);
            LocalDateTime endDate = LocalDateTime.of(2024, 11, 25, 23, 59);
//            LocalDateTime endDate = LocalDateTime.of(2024, 12, 9, 23, 59);
            LocalDateTime currentDate = startDate;

            while (!currentDate.isAfter(endDate)) {
                // 랜덤한 lightLevel 값 생성
                int danger = (int) (Math.random() * 2); // 0~1 범위
                String dangerStatus = danger == 0 ? "safe" : "danger";

                // IntrusionData 객체 생성 및 저장
                IntrusionData intrusionData = new IntrusionData();
                intrusionData.setDangerLevel(dangerStatus);
                intrusionData.setTimestamp(currentDate);
                intrusionDataRepository.save(intrusionData); // 데이터베이스에 저장

                System.out.println("Saved intrusion data: " + intrusionData);

                // 다음 데이터로 넘어가기 (몇 시간 후로 설정)
                currentDate = currentDate.plusHours((int) (Math.random() * 8) + 1); // 1~8시간 후
            }

            System.out.println("Intrusion data initialization completed.");
        } else {
            System.out.println("Skipping initialization, tuple count exceeds 30.");
        }
    }


    public List<IntrusionData> getIntrusionDataByYearAndMonth(int year, int month) {
        return intrusionDataRepository.findAllByYearAndMonth(year, month);
    }

    public IntrusionData getRecentIntrusionData() {
        return intrusionDataRepository.findMostRecentIntrusionData();
    }

}
