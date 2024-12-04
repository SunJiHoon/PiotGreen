package com.piotgreen.piotgreen.controller;


import com.piotgreen.piotgreen.entity.*;
import com.piotgreen.piotgreen.service.ReserveCommandDataStorageService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/reserve")
@RequiredArgsConstructor
public class ReserveController {
    private final ReserveCommandDataStorageService reserveCommandDataStorageService;

    @GetMapping("/lighting")
    public String getCommandListLightingData(
            Model model) {
        List<ReserveCommandData> lightingReserveCommandDataList =
                reserveCommandDataStorageService.getLightingDataList(LocalDateTime.now());
        model.addAttribute("lightingReserveCommandDataList", lightingReserveCommandDataList);

        return "reserve/lighting";
    }

    @PostMapping("/lighting")
    public String saveLightingCommand(
            @RequestParam("category") String category,
            @RequestParam("commandValue") String commandValue,
            @RequestParam("timestamp") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime timestamp,
            Model model) {

        // commandValue를 ":"로 분리하여 command와 value 추출
        String[] commandValueParts = commandValue.split(":");
        String command = commandValueParts[0];
        String value = commandValueParts[1];

        // ReserveCommandData 객체 생성 및 저장
        ReserveCommandData reserveCommandData = new ReserveCommandData();
        reserveCommandData.setCategory(category);
        reserveCommandData.setCommand(command);
        reserveCommandData.setValue(value);
        reserveCommandData.setTimestamp(timestamp);

        reserveCommandDataStorageService.saveData(reserveCommandData);

        return "redirect:/reserve/lighting";
    }


    @GetMapping("/irrigation")
    public String getCommandListIrrigation(
            Model model) {
        List<ReserveCommandData> irrigationReserveCommandDataList =
                reserveCommandDataStorageService.getIrrigationDataList(LocalDateTime.now());
        model.addAttribute("irrigationReserveCommandDataList", irrigationReserveCommandDataList);

        return "reserve/irrigation";
    }
    @PostMapping("/irrigation")
    public String saveIrrigationCommand(
            @RequestParam("category") String category,
            @RequestParam("commandValue") String commandValue,
            @RequestParam("timestamp") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime timestamp,
            Model model) {

        // commandValue를 ":"로 분리하여 command와 value 추출
        String[] commandValueParts = commandValue.split(":");
        String command = commandValueParts[0];
        String value = commandValueParts[1];

        // ReserveCommandData 객체 생성 및 저장
        ReserveCommandData reserveCommandData = new ReserveCommandData();
        reserveCommandData.setCategory(category);
        reserveCommandData.setCommand(command);
        reserveCommandData.setValue(value);
        reserveCommandData.setTimestamp(timestamp);

        reserveCommandDataStorageService.saveData(reserveCommandData);

        return "redirect:/reserve/irrigation";
    }

    @GetMapping("/intrusion")
    public String getCommandListIntrusion(
            Model model
    ) {
        List<ReserveCommandData> intrusionReserveCommandDataList =
                reserveCommandDataStorageService.getIntrusionDataList(LocalDateTime.now());
        model.addAttribute("intrusionReserveCommandDataList", intrusionReserveCommandDataList);
        return "reserve/intrusion";
    }
    @PostMapping("/intrusion")
    public String saveIntrusionCommand(
            @RequestParam("category") String category,
            @RequestParam("commandValue") String commandValue,
            @RequestParam("timestamp") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime timestamp,
            Model model) {

        // commandValue를 ":"로 분리하여 command와 value 추출
        String[] commandValueParts = commandValue.split(":");
        String command = commandValueParts[0];
        String value = commandValueParts[1];

        // ReserveCommandData 객체 생성 및 저장
        ReserveCommandData reserveCommandData = new ReserveCommandData();
        reserveCommandData.setCategory(category);
        reserveCommandData.setCommand(command);
        reserveCommandData.setValue(value);
        reserveCommandData.setTimestamp(timestamp);

        reserveCommandDataStorageService.saveData(reserveCommandData);

        return "redirect:/reserve/intrusion";
    }

}
