package com.piotgreen.piotgreen.service;

import com.piotgreen.piotgreen.entity.CommandData;
import com.piotgreen.piotgreen.entity.IntrusionData;
import com.piotgreen.piotgreen.repository.CommandDataRepository;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class CommandDataStorageService {
    private final CommandDataRepository commandDataRepository;



    public long getTupleCount() {
        // 테이블의 튜플 개수를 반환
        return commandDataRepository.count();
    }
    public void saveCommandData(String category, String command, String value) {
        CommandData commandData = new CommandData();
        commandData.setCategory(category);
        commandData.setCommand(command);
        commandData.setValue(value);
        commandData.setTimestamp(LocalDateTime.now());
        commandDataRepository.save(commandData);
    }
    @PostConstruct
    public void initializeData() {
        long tupleCount = getTupleCount(); // 개수 가져오기
        System.out.println("Current tuple count: " + tupleCount);

        if (tupleCount <= 30) {
            System.out.println("Initializing command mode data...");

            // 11월 1일부터 30일까지 데이터 생성
            LocalDateTime startDate = LocalDateTime.of(2024, 11, 1, 0, 0);
            LocalDateTime endDate = LocalDateTime.of(2024, 11, 25, 23, 59);
//            LocalDateTime endDate = LocalDateTime.of(2024, 12, 9, 23, 59);
            LocalDateTime currentDate = startDate;

            while (!currentDate.isAfter(endDate)) {
                // irrigation -mode set
                int mode = (int) (Math.random() * 2); // 0~1 범위
                String modeStr = mode == 0 ? "auto" : "pass";

                CommandData commandData = new CommandData();
                commandData.setCategory("irrigation");
                commandData.setCommand("mode");
                commandData.setValue(modeStr);
                commandData.setTimestamp(currentDate);
                commandDataRepository.save(commandData);

                // irrigation -희망 습도 설정
                int wantHumidity = (int) (Math.random() * 101); // 0~100 범위
                commandData = new CommandData();
                commandData.setCategory("irrigation");
                commandData.setCommand("wantHumidity");
                commandData.setValue(String.valueOf(wantHumidity));
                commandData.setTimestamp(currentDate);
                commandDataRepository.save(commandData);


//                // intrusion - mode set
//                mode = (int) (Math.random() * 2); // 0~1 범위
//                modeStr = mode == 0 ? "auto" : "pass";
//                commandData = new CommandData();
//                commandData.setCategory("intrusion");
//                commandData.setCommand("mode");
//                commandData.setValue(modeStr);
//                commandData.setTimestamp(currentDate);
//                commandDataRepository.save(commandData);

                // intrusion - 보안 모드
                mode = (int) (Math.random() * 2); // 0~1 범위
                modeStr = mode == 0 ? "on" : "off";
                commandData = new CommandData();
                commandData.setCategory("intrusion");
                commandData.setCommand("security");
                commandData.setValue(modeStr);
                commandData.setTimestamp(currentDate);
                commandDataRepository.save(commandData);



                // lighting - mode set
                mode = (int) (Math.random() * 2); // 0~1 범위
                modeStr = mode == 0 ? "auto" : "pass";
                commandData = new CommandData();
                commandData.setCategory("lighting");
                commandData.setCommand("mode");
                commandData.setValue(modeStr);
                commandData.setTimestamp(currentDate);
                commandDataRepository.save(commandData);

                // lighting - led 제어
                int mode1 = (int) (Math.random() * 2); // 0~1 범위
                String mode1Str = mode == 0 ? "on" : "off";
                int mode2 = (int) (Math.random() * 2); // 0~1 범위
                String mode2Str = mode == 0 ? "on" : "off";

                commandData = new CommandData();
                commandData.setCategory("lighting");
                commandData.setCommand("led1");
                commandData.setValue(mode1Str);
                commandData.setTimestamp(currentDate);
                commandDataRepository.save(commandData);
                commandData = new CommandData();
                commandData.setCategory("lighting");
                commandData.setCommand("led2");
                commandData.setValue(mode2Str);
                commandData.setTimestamp(currentDate);
                commandDataRepository.save(commandData);

                // 다음 데이터로 넘어가기 (몇 시간 후로 설정)
                currentDate = currentDate.plusHours((int) (Math.random() * 8) + 1); // 1~8시간 후
            }

            System.out.println("command mode data initialization completed.");
        } else {
            System.out.println("Skipping initialization, tuple count exceeds 30.");
        }
    }

    public CommandData getRecentCommandData(String category, String command) {
        if (category.compareTo("irrigation") == 0 && command.compareTo("mode") == 0) {
            return commandDataRepository.findMostRecentIrrigationModeCommandData();
        }
        else if (category.compareTo("irrigation") == 0 && command.compareTo("wantHumidity") == 0) {
            return commandDataRepository.findMostRecentIrrigationWantHumidityCommandData();
        }
        else if (category.compareTo("intrusion") == 0 && command.compareTo("security") == 0) {
            return commandDataRepository.findMostRecentIntrusionSecurityCommandData();
        }
        else if (category.compareTo("lighting") == 0 && command.compareTo("mode") == 0) {
            return commandDataRepository.findMostRecentLightingModeCommandData();
        }
        else if (category.compareTo("lighting") == 0 && command.compareTo("led1") == 0) {
            return commandDataRepository.findMostRecentLightingLed1CommandData();
        }
        else if (category.compareTo("lighting") == 0 && command.compareTo("led2") == 0) {
            return commandDataRepository.findMostRecentLightingLed2CommandData();
        }
        return new CommandData();
    }

}
