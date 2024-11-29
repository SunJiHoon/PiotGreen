package com.piotgreen.piotgreen.controller;

import com.piotgreen.piotgreen.entity.IntrusionData;
import com.piotgreen.piotgreen.entity.IrrigationData;
import com.piotgreen.piotgreen.entity.LedData;
import com.piotgreen.piotgreen.entity.LightData;
import com.piotgreen.piotgreen.service.IntrusionDataStorageService;
import com.piotgreen.piotgreen.service.IrrigationDataStorageService;
import com.piotgreen.piotgreen.service.LightingDataStorageService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/history")
@AllArgsConstructor
public class HistoryController {
    private final LightingDataStorageService lightingDataStorageService;
    private final IrrigationDataStorageService irrigationDataStorageService;
    private final IntrusionDataStorageService intrusionDataStorageService;

//    @GetMapping("/lighting")
//    public String getHistoryLighting(Model model) {
//        // 서비스 계층을 통해 정렬된 데이터 요청
//        List<LedData> ledDataList = lightingDataStorageService.getAllLedDataSorted();
//        List<LightData> lightDataList = lightingDataStorageService.getAllLightDataSorted();
//
//        // 모델에 데이터 추가
//        model.addAttribute("ledDataList", ledDataList);
//        model.addAttribute("lightDataList", lightDataList);
//
//        return "history/lighting";
//    }
    @GetMapping("/lighting")
    public String getLightingData(
            @RequestParam(value = "year", required = false) Integer year,
            @RequestParam(value = "month", required = false) Integer month,
            Model model) {

        // 현재 날짜로 기본값 설정
        if (year == null || month == null) {
            LocalDate now = LocalDate.now();
            year = (year == null) ? now.getYear() : year;
            month = (month == null) ? now.getMonthValue() : month;
        }


        List<LedData> ledDataList = lightingDataStorageService.getLedDataByYearAndMonth(year, month);
        List<LightData> lightDataList = lightingDataStorageService.getLightDataByYearAndMonth(year, month);

        model.addAttribute("ledDataList", ledDataList);
        model.addAttribute("lightDataList", lightDataList);
        model.addAttribute("year", year);
        model.addAttribute("month", month);

        return "history/lighting";
    }

    @GetMapping("/irrigation")
    public String getHistoryIrrigation(
            @RequestParam(value = "year", required = false) Integer year,
            @RequestParam(value = "month", required = false) Integer month,
            Model model) {

        // 현재 날짜로 기본값 설정
        if (year == null || month == null) {
            LocalDate now = LocalDate.now();
            year = (year == null) ? now.getYear() : year;
            month = (month == null) ? now.getMonthValue() : month;
        }


        List<IrrigationData> irrigationDataList = irrigationDataStorageService.getIrrigationDataByYearAndMonth(year, month);
        model.addAttribute("irrigationDataList", irrigationDataList);
        model.addAttribute("year", year);
        model.addAttribute("month", month);
        return "history/irrigation";
    }

    @GetMapping("/intrusion")
    public String getHistoryIntrusion(
            @RequestParam(value = "year", required = false) Integer year,
            @RequestParam(value = "month", required = false) Integer month,
            Model model
    ) {
        // 현재 날짜로 기본값 설정
        if (year == null || month == null) {
            LocalDate now = LocalDate.now();
            year = (year == null) ? now.getYear() : year;
            month = (month == null) ? now.getMonthValue() : month;
        }

        // 서비스 계층에서 데이터 가져오기
        List<IntrusionData> intrusionDataList = intrusionDataStorageService.getIntrusionDataByYearAndMonth(year, month);

        // 모델에 데이터 추가
        model.addAttribute("intrusionDataList", intrusionDataList);
        model.addAttribute("year", year);
        model.addAttribute("month", month);

        return "history/intrusion";
    }

}
