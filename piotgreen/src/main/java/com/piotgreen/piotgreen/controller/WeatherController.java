package com.piotgreen.piotgreen.controller;

import com.piotgreen.piotgreen.dto.WeatherGeneralData;
import com.piotgreen.piotgreen.service.WeatherDataService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.UnsupportedEncodingException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
@RequestMapping("/weather")
@AllArgsConstructor
public class WeatherController {
    private final WeatherDataService weatherDataService;
//    TMP (기온): -4°C
//    WSD (풍속): 2.9 m/s
//    SKY (하늘 상태): 3 (구름 많음)
//    POP (강수 확률): 20%

    @GetMapping("/all")
    public String getAllWeather(
            @RequestParam(value = "baseDate", required = false) String baseDate,
            @RequestParam(value = "baseTime",defaultValue = "0200", required = false) String baseTime,
            @RequestParam(value = "nx",defaultValue = "60", required = false) String nx,
            @RequestParam(value = "ny",defaultValue = "127", required = false) String ny,
            Model model) throws UnsupportedEncodingException {
        if (baseDate == null || baseDate.isEmpty()) {
            baseDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        }
        List<WeatherGeneralData> weatherGeneralDataList = weatherDataService.getWeatherGeneralData(baseDate, baseTime, nx, ny);
        model.addAttribute("weatherGeneralDataList", weatherGeneralDataList);
        model.addAttribute("baseDate", baseDate);
        model.addAttribute("baseTime", baseTime);
        model.addAttribute("nx", nx);
        model.addAttribute("ny", ny);
        return "weather/all";
    }
    @GetMapping("/json")
    @ResponseBody
    public List<WeatherGeneralData> getAllWeatherJson(
            @RequestParam(value = "baseDate",defaultValue = "20241129", required = false) String baseDate,
            @RequestParam(value = "baseTime",defaultValue = "0500", required = false) String baseTime,
            @RequestParam(value = "nx",defaultValue = "55", required = false) String nx,
            @RequestParam(value = "ny",defaultValue = "127", required = false) String ny,
            Model model) throws UnsupportedEncodingException {
        List<WeatherGeneralData> weatherGeneralDataList = weatherDataService.getWeatherGeneralData(baseDate, baseTime, nx, ny);
        return weatherGeneralDataList;
    }

}
