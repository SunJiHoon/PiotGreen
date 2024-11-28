package com.piotgreen.piotgreen.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/history")
public class HistoryController {
    @GetMapping("/lighting")
    public String getHistoryLighting() {
        return "history/lighting";
    }

    @GetMapping("/irrigation")
    public String getHistoryIrrigation() {
        return "history/irrigation";
    }

    @GetMapping("/intrusion")
    public String getHistoryIntrusion() {
        return "history/intrusion";
    }

}
