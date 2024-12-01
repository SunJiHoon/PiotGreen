package com.piotgreen.piotgreen.service;

import com.piotgreen.piotgreen.dto.DailyLightData;
import com.piotgreen.piotgreen.entity.LedData;
import com.piotgreen.piotgreen.entity.LightData;
import com.piotgreen.piotgreen.repository.LedDataRepository;
import com.piotgreen.piotgreen.repository.LightDataRepository;
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
public class LightingDataStorageService {

    private final LedDataRepository ledDataRepository;
    private final LightDataRepository lightDataRepository;

    public LedData saveLedData(String led1, String led2) {
        LedData ledData = new LedData();
        ledData.setLed1(led1);
        ledData.setLed2(led2);
        ledData.setTimestamp(LocalDateTime.now());
        return ledDataRepository.save(ledData);
    }


    public LightData saveLightData(int lightLevel1, int lightLevel2) {
        LightData lightData = new LightData();
        lightData.setLightLevel1(lightLevel1);
        lightData.setLightLevel2(lightLevel2);
        lightData.setTimestamp(LocalDateTime.now());
        return lightDataRepository.save(lightData);
    }

    public List<LedData> getAllLedDataSorted() {
        return ledDataRepository.findAllByOrderByTimestampAsc();
    }

    public List<LightData> getAllLightDataSorted() {
//        return lightDataRepository.findAllByOrderByTimestampAsc();
        return lightDataRepository.findAllByOrderByTimestampDesc();
    }
    public List<DailyLightData> getAverageDailyLight(int year, int month) {
        // 데이터를 조회
        List<LightData> lightData = lightDataRepository.findAllByYearAndMonth(year, month);

        // 누적 합계와 개수를 저장
        Map<Integer, int[]> dailyLightStats = new HashMap<>();

        for (LightData data : lightData) {
            int day = data.getTimestamp().getDayOfMonth();
            int avgLevel = (data.getLightLevel1() + data.getLightLevel2()) / 2;

            dailyLightStats
                    .computeIfAbsent(day, k -> new int[2])[0] += avgLevel; // 합계 누적
            dailyLightStats
                    .computeIfAbsent(day, k -> new int[2])[1] += 1;        // 개수 증가
        }

        // 1일부터 31일까지 기본 데이터 채우기
        List<DailyLightData> results = new ArrayList<>();
        for (int day = 1; day <= 31; day++) {
            int[] stats = dailyLightStats.getOrDefault(day, new int[]{0, 0});
            double average = stats[1] > 0 ? (double) stats[0] / stats[1] : 0; // 평균 계산
            results.add(new DailyLightData(day, average));
        }

        // 결과 반환
        return results;
    }

    public long getLightTupleCount() {
        // 테이블의 튜플 개수를 반환
        return lightDataRepository.count();
    }

    @PostConstruct
    public void initializeLightData() {
        long tupleCount = getLightTupleCount(); // 개수 가져오기
        System.out.println("Current tuple count: " + tupleCount);

        if (tupleCount <= 30) {
            System.out.println("Initializing light data...");

            // 11월 1일부터 30일까지 데이터 생성
            LocalDateTime startDate = LocalDateTime.of(2024, 5, 1, 0, 0);
            LocalDateTime endDate = LocalDateTime.of(2024, 11, 25, 23, 59);
//            LocalDateTime endDate = LocalDateTime.of(2024, 12, 9, 23, 59);
            LocalDateTime currentDate = startDate;

            while (!currentDate.isAfter(endDate)) {
                // 랜덤한 lightLevel 값 생성
                int lightLevel1 = (int) (Math.random() * 101); // 0~100 범위
                int lightLevel2 = (int) (Math.random() * 101); // 0~100 범위

                // LightData 객체 생성 및 저장
                LightData lightData = new LightData();
                lightData.setLightLevel1(lightLevel1);
                lightData.setLightLevel2(lightLevel2);
                lightData.setTimestamp(currentDate);
                lightDataRepository.save(lightData); // 데이터베이스에 저장

                System.out.println("Saved light data: " + lightData);

                // 다음 데이터로 넘어가기 (몇 시간 후로 설정)
                currentDate = currentDate.plusHours((int) (Math.random() * 8) + 1); // 1~8시간 후
            }

            System.out.println("Light data initialization completed.");
        } else {
            System.out.println("Skipping initialization, tuple count exceeds 30.");
        }
    }

    public long getLedTupleCount() {
        // 테이블의 튜플 개수를 반환
        return ledDataRepository.count();
    }
    @PostConstruct
    public void initializeLedData() {
        long tupleCount = getLedTupleCount(); // 개수 가져오기
        System.out.println("Current tuple count: " + tupleCount);

        if (tupleCount <= 30) {
            System.out.println("Initializing led data...");

            // 11월 1일부터 30일까지 데이터 생성
            LocalDateTime startDate = LocalDateTime.of(2024, 5, 1, 0, 0);
            LocalDateTime endDate = LocalDateTime.of(2024, 11, 25, 23, 59);
//            LocalDateTime endDate = LocalDateTime.of(2024, 12, 9, 23, 59);
            LocalDateTime currentDate = startDate;

            while (!currentDate.isAfter(endDate)) {
                // 랜덤한 lightLevel 값 생성
                int led1 = (int) (Math.random() * 2); // 0~1 범위
                String led1Status = led1 == 0 ? "on" : "off";
                int led2 = (int) (Math.random() * 2); // 0~1 범위
                String led2Status = led1 == 0 ? "on" : "off";

                // LightData 객체 생성 및 저장
                LedData ledData = new LedData();
                ledData.setLed1(led1Status);
                ledData.setLed2(led2Status);
                ledData.setTimestamp(currentDate);
                ledDataRepository.save(ledData); // 데이터베이스에 저장

                System.out.println("Saved led data: " + ledData);

                // 다음 데이터로 넘어가기 (몇 시간 후로 설정)
                currentDate = currentDate.plusHours((int) (Math.random() * 8) + 1); // 1~8시간 후
            }

            System.out.println("led data initialization completed.");
        } else {
            System.out.println("Skipping initialization, tuple count exceeds 30.");
        }
    }



    public List<LedData> getLedDataByYearAndMonth(int year, int month) {
        return ledDataRepository.findAllByYearAndMonth(year, month);
    }

    public List<LightData> getLightDataByYearAndMonth(int year, int month) {
        return lightDataRepository.findAllByYearAndMonth(year, month);
    }

    public LightData getRecentLightData(){
        return lightDataRepository.findMostRecentLightData();
    }

}
