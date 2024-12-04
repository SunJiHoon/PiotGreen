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
                reserveCommandDataRepository.findByCategoryAndTimestampAfterOrderByTimestampAsc("intrusion", timestamp);
        return reserveCommandDataList;
    }

    public List<ReserveCommandData> getLightingDataList(LocalDateTime timestamp) {
        List<ReserveCommandData> reserveCommandDataList =
                reserveCommandDataRepository.findByCategoryAndTimestampAfterOrderByTimestampAsc("lighting", timestamp);
        return reserveCommandDataList;
    }

    public List<ReserveCommandData> getIrrigationDataList(LocalDateTime timestamp) {
        List<ReserveCommandData> reserveCommandDataList =
                reserveCommandDataRepository.findByCategoryAndTimestampAfterOrderByTimestampAsc("irrigation", timestamp);
        return reserveCommandDataList;
    }


    public void executeScheduledCommands() {
        LocalDateTime now = LocalDateTime.now();

        // 현재 시간이 지난 SCHEDULED 상태의 예약 찾기
        List<ReserveCommandData> scheduledCommands = reserveCommandDataRepository.findByStatusAndTimestampBefore(
                "SCHEDULED", now);

        for (ReserveCommandData command : scheduledCommands) {
            // 예약 실행 로직
            executeCommand(command);

            // 상태를 COMPLETED로 변경
            command.setStatus("COMPLETED");
            reserveCommandDataRepository.save(command);
        }
    }

    private void executeCommand(ReserveCommandData command) {
        // 실제 실행 로직 (예: 외부 API 호출, 장비 제어 등)
        System.out.println("Executing command ID: " + command.getId());
    }


}
