package com.piotgreen.piotgreen.service;

import com.piotgreen.piotgreen.entity.IrrigationData;
import com.piotgreen.piotgreen.repository.IrrigationDataRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

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
        return irrigationDataRepository.findAllByOrderByTimestampAsc();
    }

}
