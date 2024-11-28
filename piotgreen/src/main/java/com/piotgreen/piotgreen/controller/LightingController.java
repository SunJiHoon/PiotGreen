package com.piotgreen.piotgreen.controller;

import com.piotgreen.piotgreen.dto.DailyLightData;
import com.piotgreen.piotgreen.service.LightingDataStorageService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@AllArgsConstructor
public class LightingController {
    private final LightingDataStorageService lightingDataStorageService;

    @GetMapping("/lighting/data")
    @ResponseBody
    public List<DailyLightData> getAverageLightData(@RequestParam("year") String year, @RequestParam("month") String month) {

        return lightingDataStorageService.getAverageDailyLight(Integer.parseInt(year), Integer.parseInt(month));
    }
}
