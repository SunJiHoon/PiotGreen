package com.piotgreen.piotgreen.controller;

import com.piotgreen.piotgreen.entity.*;
import com.piotgreen.piotgreen.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
@RequestMapping("/manage")
@RequiredArgsConstructor
public class ManagerController {
    private final ManagerDataStorageService managerDataStorageService;
    private final IntrusionDataStorageService intrusionDataStorageService;
    private final IrrigationDataStorageService irrigationDataStorageService;
    private final LightingDataStorageService lightingDataStorageService;
    private final CommandDataStorageService commandDataStorageService;

    @GetMapping("/lighting")
    public String manageLighting(Model model) {
        LightData lightData = lightingDataStorageService.getRecentLightData();
        model.addAttribute("lightData",lightData);
        LedData ledData = lightingDataStorageService.getRecentLedData();
        model.addAttribute("ledData",ledData);
        CommandData commandData = commandDataStorageService.
        return "manage/lighting"; // This should correspond to manageLighting.html or a similar template
    }

    @GetMapping("/irrigation")
    public String manageIrrigation(Model model) {
        IrrigationData irrigationData = irrigationDataStorageService.getRecentIrrigationData();
        model.addAttribute("irrigationData",irrigationData);
        return "manage/irrigation"; // This should correspond to manageIrrigation.html or a similar template
    }

    @GetMapping("/intrusion")
    public String monitorFarm(Model model) {
        return "manage/intrusion"; // This should correspond to monitorFarm.html or a similar template
    }

    @GetMapping("/manager")
    public String registerManager(Model model) {
        model.addAttribute("newManager", new ManagerData()); // 폼에 사용할 빈 객체
        model.addAttribute("managerDataList", managerDataStorageService.getAllManagerData());
        return "manage/manager"; // This should correspond to monitorFarm.html or a similar template
    }

    @PostMapping("/manager")
    public String addManagerData(@ModelAttribute ManagerData newManager) {
        managerDataStorageService.saveUser(newManager); // 새 관리자 저장
        return "redirect:/manage/manager"; // 저장 후 다시 목록 페이지로 리다이렉트
    }

    @PostMapping("/api/send-danger-message")
    public ResponseEntity<String> sendDangerMessage(@RequestBody Map<String, Object> payload) {
        String message = (String) payload.get("message");
        String timestamp = (String) payload.get("timestamp");

        managerDataStorageService.sendMessageToAllManger(message);
        // 로직 처리: 메시지를 로그에 기록하거나 외부 시스템으로 전송
        System.out.println("Danger message received: " + message);
        System.out.println("Timestamp: " + timestamp);

        return ResponseEntity.ok("Danger message sent successfully!");
    }


    @PostMapping("/manager/delete")
    public String deleteManagerData(@RequestParam(value = "phoneNumber") String phoneNumber) {
        managerDataStorageService.deleteByPhoneNumber(phoneNumber); // 전화번호로 삭제 로직 호출
        return "redirect:/manage/manager"; // 삭제 후 목록 페이지로 리다이렉트
    }



}
