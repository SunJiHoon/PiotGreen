package com.piotgreen.piotgreen.service;

import com.piotgreen.piotgreen.dto.DailyLightData;
import com.piotgreen.piotgreen.dto.DailyMoistureData;
import com.piotgreen.piotgreen.entity.IrrigationData;
import com.piotgreen.piotgreen.entity.LightData;
import com.piotgreen.piotgreen.repository.IrrigationDataRepository;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor
public class IrrigationDataStorageService {
    private final IrrigationDataRepository irrigationDataRepository;

    public IrrigationData saveIrrigationData(int moistureLevel) {
        // 유효성 검사 (선택적으로 추가 가능)

        IrrigationData irrigationData = new IrrigationData();
        irrigationData.setMoistureLevel(moistureLevel);
        irrigationData.setTimestamp(LocalDateTime.now());
        return irrigationDataRepository.save(irrigationData);
    }
    public List<IrrigationData> getAllIrrigationDataSorted() {
//        return irrigationDataRepository.findAllByOrderByTimestampAsc();
        return irrigationDataRepository.findAllByOrderByTimestampDesc();
    }


    public List<DailyMoistureData> getAverageDailyMoisture(int year, int month) {
        // 데이터를 조회
        List<IrrigationData> irrigationData = irrigationDataRepository.findAllByYearAndMonth(year, month);

        // 누적 합계와 개수를 저장
        Map<Integer, int[]> dailyIrrigationStats = new HashMap<>();

        for (IrrigationData data : irrigationData) {
            int day = data.getTimestamp().getDayOfMonth();
            int avgLevel = (data.getMoistureLevel());

            dailyIrrigationStats
                    .computeIfAbsent(day, k -> new int[2])[0] += avgLevel; // 합계 누적
            dailyIrrigationStats
                    .computeIfAbsent(day, k -> new int[2])[1] += 1;        // 개수 증가
        }

        // 1일부터 31일까지 기본 데이터 채우기
        List<DailyMoistureData> results = new ArrayList<>();
        for (int day = 1; day <= 31; day++) {
            int[] stats = dailyIrrigationStats.getOrDefault(day, new int[]{0, 0});
            double average = stats[1] > 0 ? (double) stats[0] / stats[1] : 0; // 평균 계산
            results.add(new DailyMoistureData(day, average));
        }

        // 결과 반환
        return results;
    }

    public long getTupleCount() {
        // 테이블의 튜플 개수를 반환
        return irrigationDataRepository.count();
    }

    @PostConstruct
    public void initializeData() {
        long tupleCount = getTupleCount(); // 개수 가져오기
        System.out.println("Current tuple count: " + tupleCount);

        if (tupleCount <= 30) {
            System.out.println("Initializing irrigation data...");

            // 11월 1일부터 30일까지 데이터 생성
            LocalDateTime startDate = LocalDateTime.of(2024, 5, 1, 0, 0);
            LocalDateTime endDate = LocalDateTime.of(2024, 12, 9, 23, 59);
//            LocalDateTime endDate = LocalDateTime.of(2024, 12, 9, 23, 59);
            LocalDateTime currentDate = startDate;

            while (!currentDate.isAfter(endDate)) {
                // 랜덤한 lightLevel 값 생성
                int moistureLevel = (int) (Math.random() * 101); // 0~100 범위

                // LightData 객체 생성 및 저장
                IrrigationData irrigationData = new IrrigationData();
                irrigationData.setMoistureLevel(moistureLevel);
                irrigationData.setTimestamp(currentDate);
                irrigationDataRepository.save(irrigationData); // 데이터베이스에 저장

                System.out.println("Saved moisture data: " + irrigationData);

                // 다음 데이터로 넘어가기 (몇 시간 후로 설정)
                currentDate = currentDate.plusHours((int) (Math.random() * 8) + 1); // 1~8시간 후
            }

            System.out.println("IrrigationData data initialization completed.");
        } else {
            System.out.println("Skipping initialization, tuple count exceeds 30.");
        }
    }

    public List<IrrigationData> getIrrigationDataByYearAndMonth(int year, int month) {
        return irrigationDataRepository.findAllByYearAndMonth(year, month);
    }

    public IrrigationData getRecentIrrigationData(){
        return irrigationDataRepository.findMostRecentIrrigationData();
    }

}
