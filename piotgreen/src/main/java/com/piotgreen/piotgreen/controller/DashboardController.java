package com.piotgreen.piotgreen.controller;

import com.piotgreen.piotgreen.service.IntrusionPiClientService;
import com.piotgreen.piotgreen.service.IrrigationPiClientService;
import com.piotgreen.piotgreen.service.LightingPiClientService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("")
@AllArgsConstructor
public class DashboardController {
    final private LightingPiClientService lightingPiClientService;
    final private IrrigationPiClientService irrigationPiClientService;
    final private IntrusionPiClientService intrusionPiClientService;

    @GetMapping("")
    public String index(Model model) {
        return "index";
    }

    @PostMapping("/initialize-connection/lightingPiClient")
    @ResponseBody
    public String initializeConnectionLightingPiClient() {
        try {
            lightingPiClientService.initializeConnection(); // LightingService의 메서드 호출
            return "Connection initialized successfully.";
        } catch (Exception e) {
            return "Failed to initialize connection: " + e.getMessage();
        }
    }
    @PostMapping("/initialize-connection/irrigationPiClient")
    @ResponseBody
    public String initializeConnectionIrrigationPiClient() {
        try {
            irrigationPiClientService.initializeConnection(); // LightingService의 메서드 호출
            return "Connection initialized successfully.";
        } catch (Exception e) {
            return "Failed to initialize connection: " + e.getMessage();
        }
    }
    @PostMapping("/initialize-connection/intrusionPiClient")
    @ResponseBody
    public String initializeConnectionIntrusionPiClient() {
        try {
            intrusionPiClientService.initializeConnection(); // LightingService의 메서드 호출
            return "Connection initialized successfully.";
        } catch (Exception e) {
            return "Failed to initialize connection: " + e.getMessage();
        }
    }

}
