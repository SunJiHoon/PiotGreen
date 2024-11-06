package com.piotgreen.piotgreen.controller;

import com.piotgreen.piotgreen.service.IntrusionPiClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class IntrusionPiController {
    @Autowired
    private IntrusionPiClientService intrusionPiClientService;

    @GetMapping("/send-intrusion-command")
    public String sendIntrusionCommand(@RequestParam String command) {
        return intrusionPiClientService.sendCommand(command);
    }

}
