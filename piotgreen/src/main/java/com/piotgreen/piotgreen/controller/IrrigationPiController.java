package com.piotgreen.piotgreen.controller;

import com.piotgreen.piotgreen.service.CommandDataStorageService;
import com.piotgreen.piotgreen.service.IrrigationPiClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/irrigation")
@RequiredArgsConstructor
public class IrrigationPiController {
//    @Autowired
    private final IrrigationPiClientService irrigationPiClientService;
    private final CommandDataStorageService commandDataStorageService;

    @GetMapping("/send-irrigation-command")
    public String sendCommand(@RequestParam String command) {
        return irrigationPiClientService.sendCommand(command);
    }

    @PostMapping("/humidity/set")
    public ResponseEntity<String> setTargetHumidity(@RequestBody Map<String, Integer> payload) {
        Integer newTargetHumidity = payload.get("targetHumidity");

        System.out.println("습도는 " + newTargetHumidity + "로 변경됩니다.");

        String command = "irrigation_system:pump:" +newTargetHumidity;
        commandDataStorageService.saveCommandData("irrigation","wantHumidity",newTargetHumidity.toString());

        return ResponseEntity.ok(irrigationPiClientService.sendCommand(command));
    }


    @PostMapping("/mode/set")
    public ResponseEntity<String> setMode(@RequestBody Map<String, String> payload) {
        String mode = payload.get("mode");

        String command;
        if (mode.compareTo("manual") == 0){
            command = "mode:pass";
            commandDataStorageService.saveCommandData("irrigation","mode","pass");
            return ResponseEntity.ok(irrigationPiClientService.sendCommand(command));
        }
        if (mode.compareTo("auto") == 0){
            command = "mode:auto";
            commandDataStorageService.saveCommandData("irrigation","mode","auto");
            return ResponseEntity.ok(irrigationPiClientService.sendCommand(command));
        }
        return ResponseEntity.badRequest().body("bad request");
    }

}
