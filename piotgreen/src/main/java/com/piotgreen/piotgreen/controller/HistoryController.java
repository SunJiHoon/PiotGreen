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

import java.util.List;

@Controller
@RequestMapping("/history")
@AllArgsConstructor
public class HistoryController {
    private final LightingDataStorageService lightingDataStorageService;
    private final IrrigationDataStorageService irrigationDataStorageService;
    private final IntrusionDataStorageService intrusionDataStorageService;

    @GetMapping("/lighting")
    public String getHistoryLighting(Model model) {
        // 서비스 계층을 통해 정렬된 데이터 요청
        List<LedData> ledDataList = lightingDataStorageService.getAllLedDataSorted();
        List<LightData> lightDataList = lightingDataStorageService.getAllLightDataSorted();

        // 모델에 데이터 추가
        model.addAttribute("ledDataList", ledDataList);
        model.addAttribute("lightDataList", lightDataList);

        return "history/lighting";
    }

    @GetMapping("/irrigation")
    public String getHistoryIrrigation(Model model) {  // 서비스 계층에서 데이터 가져오기
        List<IrrigationData> irrigationDataList = irrigationDataStorageService.getAllIrrigationDataSorted();

        // 모델에 데이터 추가
        model.addAttribute("irrigationDataList", irrigationDataList);

        return "history/irrigation";
    }

    @GetMapping("/intrusion")
    public String getHistoryIntrusion(Model model) {
        // 서비스 계층에서 데이터 가져오기
        List<IntrusionData> intrusionDataList = intrusionDataStorageService.getAllIntrusionDataSorted();

        // 모델에 데이터 추가
        model.addAttribute("intrusionDataList", intrusionDataList);

        return "history/intrusion";
    }

}
