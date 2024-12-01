package com.piotgreen.piotgreen.controller;

import com.piotgreen.piotgreen.entity.ManagerData;
import com.piotgreen.piotgreen.service.ManagerDataStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequestMapping("/manage")
@RequiredArgsConstructor
public class ManagerController {
    private final ManagerDataStorageService managerDataStorageService;
    @GetMapping("/lighting")
    public String manageLighting() {
        return "manage/lighting"; // This should correspond to manageLighting.html or a similar template
    }

    @GetMapping("/irrigation")
    public String manageIrrigation() {
        return "manage/irrigation"; // This should correspond to manageIrrigation.html or a similar template
    }

    @GetMapping("/intrusion")
    public String monitorFarm() {
        return "manage/intrusion"; // This should correspond to monitorFarm.html or a similar template
    }

    @GetMapping("/manager")
    public String registerManager(Model model) {
        model.addAttribute("newManager", new ManagerData()); // 폼에 사용할 빈 객체
        model.addAttribute("managerDataList", managerDataStorageService.getAllManagerData());
        return "manage/manager"; // This should correspond to monitorFarm.html or a similar template
    }
}
