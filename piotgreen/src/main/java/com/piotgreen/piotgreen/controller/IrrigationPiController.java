package com.piotgreen.piotgreen.controller;

import com.piotgreen.piotgreen.service.IrrigationPiClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class IrrigationPiController {
    @Autowired
    private IrrigationPiClientService irrigationPiClientService;

    @GetMapping("/send-command")
    public String sendCommand(@RequestParam String command) {
        return irrigationPiClientService.sendCommand(command);
    }
}
