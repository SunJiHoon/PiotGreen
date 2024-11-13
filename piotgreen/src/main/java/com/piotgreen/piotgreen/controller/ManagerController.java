package com.piotgreen.piotgreen.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequestMapping("/manage")
public class ManagerController {
    @GetMapping("/lighting")
    public String manageLighting() {
        return "manageLighting"; // This should correspond to manageLighting.html or a similar template
    }

    @GetMapping("/irrigation")
    public String manageIrrigation() {
        return "manageIrrigation"; // This should correspond to manageIrrigation.html or a similar template
    }

    @GetMapping("/intrusion")
    public String monitorFarm() {
        return "monitorFarm"; // This should correspond to monitorFarm.html or a similar template
    }

}
