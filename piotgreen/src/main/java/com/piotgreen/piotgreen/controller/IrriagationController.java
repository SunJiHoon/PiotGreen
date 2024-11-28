package com.piotgreen.piotgreen.controller;

import com.piotgreen.piotgreen.dto.DailyLightData;
import com.piotgreen.piotgreen.dto.DailyMoistureData;
import com.piotgreen.piotgreen.service.IrrigationDataStorageService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@AllArgsConstructor
public class IrriagationController {
    private final IrrigationDataStorageService irrigationDataStorageService;

    @GetMapping("/irrigation/data")
    @ResponseBody
    public List<DailyMoistureData> getAverageLightData(@RequestParam("year") String year, @RequestParam("month") String month) {

        return irrigationDataStorageService.getAverageDailyMoisture(Integer.parseInt(year), Integer.parseInt(month));
    }
}
