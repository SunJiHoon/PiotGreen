package com.piotgreen.piotgreen.controller;

import com.piotgreen.piotgreen.entity.IntrusionData;
import com.piotgreen.piotgreen.entity.LightData;
import com.piotgreen.piotgreen.repository.IntrusionDataRepository;
import com.piotgreen.piotgreen.service.*;
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
    private final LightingPiClientService lightingPiClientService;
    private final IrrigationPiClientService irrigationPiClientService;
    private final IntrusionPiClientService intrusionPiClientService;
    private final IntrusionDataStorageService intrusionDataStorageService;
    private final IrrigationDataStorageService irrigationDataStorageService;
    private final LightingDataStorageService lightingDataStorageService;


    @GetMapping("")
    public String index(Model model) {
        IntrusionData intrusionData = intrusionDataStorageService.getRecentIntrusionData();
        model.addAttribute("intrusionData", intrusionData);
        LightData lightData = lightingDataStorageService.getRecentLightData();
        model.addAttribute("lightData", lightData);
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
