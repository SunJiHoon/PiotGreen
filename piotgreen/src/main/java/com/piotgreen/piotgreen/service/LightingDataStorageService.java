package com.piotgreen.piotgreen.service;

import com.piotgreen.piotgreen.entity.LedData;
import com.piotgreen.piotgreen.entity.LightData;
import com.piotgreen.piotgreen.repository.LedDataRepository;
import com.piotgreen.piotgreen.repository.LightDataRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

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
        return lightDataRepository.findAllByOrderByTimestampAsc();
    }

}
