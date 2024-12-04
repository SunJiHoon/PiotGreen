package com.piotgreen.piotgreen.service;

import com.piotgreen.piotgreen.entity.ManagerData;
import com.piotgreen.piotgreen.entity.ReserveCommandData;
import com.piotgreen.piotgreen.repository.ReserveCommandDataRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReserveCommandDataStorageService {
    private final ReserveCommandDataRepository reserveCommandDataRepository;

    public void saveData(ReserveCommandData reserveCommandData){
        reserveCommandDataRepository.save(reserveCommandData);
    }
    public List<ReserveCommandData> getIntrusionDataList(LocalDateTime timestamp) {
        List<ReserveCommandData> reserveCommandDataList =
                reserveCommandDataRepository.findByCategoryAndTimestampAfter("intrusion", timestamp);
        return reserveCommandDataList;
    }

    public List<ReserveCommandData> getLightingDataList(LocalDateTime timestamp) {
        List<ReserveCommandData> reserveCommandDataList =
                reserveCommandDataRepository.findByCategoryAndTimestampAfter("lighting", timestamp);
        return reserveCommandDataList;
    }

    public List<ReserveCommandData> getIrrigationDataList(LocalDateTime timestamp) {
        List<ReserveCommandData> reserveCommandDataList =
                reserveCommandDataRepository.findByCategoryAndTimestampAfter("irrigation", timestamp);
        return reserveCommandDataList;
    }


}
