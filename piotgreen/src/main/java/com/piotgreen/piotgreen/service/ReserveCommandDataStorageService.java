package com.piotgreen.piotgreen.service;

import com.piotgreen.piotgreen.entity.ManagerData;
import com.piotgreen.piotgreen.entity.ReserveCommandData;
import com.piotgreen.piotgreen.repository.ManagerDataRepository;
import com.piotgreen.piotgreen.repository.ReserveCommandDataRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReserveCommandDataStorageService {
    private final ReserveCommandDataRepository reserveCommandDataRepository;
    private final IntrusionPiClientService intrusionPiClientService;
    private final IrrigationPiClientService irrigationPiClientService;
    private final LightingPiClientService lightingPiClientService;
    private final CommandDataStorageService commandDataStorageService;



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

    private void executeCommand(ReserveCommandData reserveCommandData) {
        // 실제 실행 로직 (예: 외부 API 호출, 장비 제어 등)
        System.out.println("Executing command ID: " + reserveCommandData.getId());

        String category = reserveCommandData.getCategory();
        String command = reserveCommandData.getCommand();
        String data = reserveCommandData.getValue();


        //intrusion
        if (category.compareTo("intrusion") == 0) {
            if (command.compareTo("security") == 0) {
                if (data.compareTo("on") == 0) {
                    String paramCommand = "intrusion_detection:danger:on";
                    commandDataStorageService.saveCommandData("intrusion", "security", "on");
                    intrusionPiClientService.sendCommand(paramCommand);
                } else if (data.compareTo("off") == 0) {
                    String paramCommand = "intrusion_detection:danger:off";
                    commandDataStorageService.saveCommandData("intrusion", "security", "off");
                    intrusionPiClientService.sendCommand(paramCommand);
                }
            }
        }
        //irrigation
        else if (category.compareTo("irrigation") == 0) {
            if (command.compareTo("wantHumidity") == 0) {
                String paramCommand = "irrigation_system:pump:" + data;
                commandDataStorageService.saveCommandData("irrigation", "wantHumidity", data);
                irrigationPiClientService.sendCommand(paramCommand);
            } else if (command.compareTo("mode") == 0) {
                String paramCommand;
                if (data.compareTo("manual") == 0) {
                    paramCommand = "mode:pass";
                    commandDataStorageService.saveCommandData("irrigation", "mode", "pass");
                    irrigationPiClientService.sendCommand(paramCommand);
                }
                if (data.compareTo("auto") == 0) {
                    paramCommand = "mode:auto";
                    commandDataStorageService.saveCommandData("irrigation", "mode", "auto");
                    irrigationPiClientService.sendCommand(paramCommand);
                }
            }
        }
        //lighting
        else if (category.compareTo("lighting") == 0) {
            String paramCommand = null;
            if (command.compareTo("led1") == 0) {
                if (data.compareTo("on") == 0) {
                    commandDataStorageService.saveCommandData("lighting", "led1", "on");
                    paramCommand = "LED:on[1]";
                } else {
                    commandDataStorageService.saveCommandData("lighting", "led1", "off");
                    paramCommand = "LED:off[1]";
                }
            } else if (command.compareTo("led2") == 0) {
                if (data.compareTo("on") == 0) {
                    commandDataStorageService.saveCommandData("lighting", "led2", "on");
                    paramCommand = "LED:on[2]";
                } else {
                    commandDataStorageService.saveCommandData("lighting", "led2", "off");
                    paramCommand = "LED:off[2]";
                }
            } else if (command.compareTo("mode") == 0) {
                if (data.compareTo("pass") == 0) {
                    paramCommand = "mode:pass";
                    commandDataStorageService.saveCommandData("lighting", "mode", "pass");
                } else if (data.compareTo("auto") == 0) {
                    paramCommand = "mode:auto";
                    commandDataStorageService.saveCommandData("lighting", "mode", "auto");
                }
            }

            if (paramCommand != null) {
                log.info(paramCommand);
                lightingPiClientService.sendCommand(paramCommand);
            } else {
                log.info("paramCommand is null");
            }
        }
    }
    public void cancelCommandById(String id){
        Optional<ReserveCommandData> reserveCommandData = reserveCommandDataRepository.findById(Long.valueOf(id));
        if (reserveCommandData.isPresent()) {
            reserveCommandData.get().setStatus("CANCELLED");
            reserveCommandDataRepository.save(reserveCommandData.get());
        }
    }
}
