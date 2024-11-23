package com.piotgreen.piotgreen.controller;

import com.piotgreen.piotgreen.service.LightingPiClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/lighting")
public class LightingPiController {
    @Autowired
    private LightingPiClientService lightingPiClientService;

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
            command = "LED:on[" + index + "]";
        }
        else{
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
            return ResponseEntity.ok(lightingPiClientService.sendCommand(command));
        }
        if (mode.compareTo("auto") == 0){
            command = "mode:auto";
            return ResponseEntity.ok(lightingPiClientService.sendCommand(command));
        }
        return ResponseEntity.badRequest().body("bad request");
    }


}
