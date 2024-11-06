package com.piotgreen.piotgreen.controller;

import com.piotgreen.piotgreen.service.LightingPiClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LightingPiController {
    @Autowired
    private LightingPiClientService lightingPiClientService;

    @GetMapping("/send-lighting-command")
    public String sendLightingCommand(@RequestParam String command) {
        return lightingPiClientService.sendCommand(command);
    }
}
