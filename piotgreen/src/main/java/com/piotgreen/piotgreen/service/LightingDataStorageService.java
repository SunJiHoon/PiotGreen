package com.piotgreen.piotgreen.service;

import com.piotgreen.piotgreen.dto.DailyLightData;
import com.piotgreen.piotgreen.entity.LedData;
import com.piotgreen.piotgreen.entity.LightData;
import com.piotgreen.piotgreen.repository.LedDataRepository;
import com.piotgreen.piotgreen.repository.LightDataRepository;
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

}
