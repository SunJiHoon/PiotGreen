package com.piotgreen.piotgreen.service;

import com.piotgreen.piotgreen.entity.IntrusionData;
import com.piotgreen.piotgreen.entity.IrrigationData;
import com.piotgreen.piotgreen.repository.IntrusionDataRepository;
import com.piotgreen.piotgreen.repository.IrrigationDataRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@AllArgsConstructor
public class IntrusionDataStorageService {
    private final IntrusionDataRepository intrusionDataRepository;

    public IntrusionData saveIntrusionData(int dangerLevel) {
        // 유효성 검사 (선택적으로 추가 가능)

        IntrusionData intrusionData = new IntrusionData();
        intrusionData.setDangerLevel(dangerLevel);
        intrusionData.setTimestamp(LocalDateTime.now());
        return intrusionDataRepository.save(intrusionData);
    }

}
