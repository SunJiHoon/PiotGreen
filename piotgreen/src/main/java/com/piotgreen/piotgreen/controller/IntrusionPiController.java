package com.piotgreen.piotgreen.controller;

import com.piotgreen.piotgreen.service.IntrusionPiClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/intrusion")
@RequiredArgsConstructor
public class IntrusionPiController {
    private final IntrusionPiClientService intrusionPiClientService;

//    @GetMapping("/send-intrusion-command")
//    public String sendIntrusionCommand(@RequestParam String command) {
//        return intrusionPiClientService.sendCommand(command);
//    }


    @PostMapping("/mode/set")
    public ResponseEntity<String> setMode(@RequestBody Map<String, String> payload) {
        String mode = payload.get("mode");

        String command;
        if (mode.compareTo("on") == 0){
            command = "intrusion_detection:danger:on";
            return ResponseEntity.ok(intrusionPiClientService.sendCommand(command));
        }
        if (mode.compareTo("off") == 0){
            command = "intrusion_detection:danger:off";
            return ResponseEntity.ok(intrusionPiClientService.sendCommand(command));
        }
        return ResponseEntity.badRequest().body("bad request");
    }
}
