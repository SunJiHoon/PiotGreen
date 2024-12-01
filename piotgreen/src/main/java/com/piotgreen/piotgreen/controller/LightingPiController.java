package com.piotgreen.piotgreen.controller;

import com.piotgreen.piotgreen.service.CommandDataStorageService;
import com.piotgreen.piotgreen.service.LightingPiClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/lighting")
@RequiredArgsConstructor
public class LightingPiController {
    private final LightingPiClientService lightingPiClientService;
    private final CommandDataStorageService commandDataStorageService;


    @GetMapping("/send-lighting-command")
    public String sendLightingCommand(@RequestParam String command) {
        return lightingPiClientService.sendCommand(command);
    }
    @PostMapping("/led/toggle")
    public ResponseEntity<String> toggleLed(@RequestBody Map<String, Object> payload) {
        Integer index = (Integer) payload.get("index");
        Integer state = (Integer) payload.get("state");

        System.out.println("LED Index: " + index + ", State는 " + state + "로 변경됩니다.");

        String command;

        if (state == 1){
            commandDataStorageService.saveCommandData("lighting","led" + index,"on");
            command = "LED:on[" + index + "]";
        }
        else{
            commandDataStorageService.saveCommandData("lighting","led" + index,"off");
            command = "LED:off[" + index + "]";
        }
        return ResponseEntity.ok(lightingPiClientService.sendCommand(command));
    }

    @PostMapping("/mode/set")
    public ResponseEntity<String> setMode(@RequestBody Map<String, String> payload) {
        String mode = payload.get("mode");

        String command;
        if (mode.compareTo("manual") == 0){
            command = "mode:pass";
            commandDataStorageService.saveCommandData("lighting","mode","pass");
            return ResponseEntity.ok(lightingPiClientService.sendCommand(command));
        }
        if (mode.compareTo("auto") == 0){
            command = "mode:auto";
            commandDataStorageService.saveCommandData("lighting","mode","auto");
            return ResponseEntity.ok(lightingPiClientService.sendCommand(command));
        }
        return ResponseEntity.badRequest().body("bad request");
    }


}
