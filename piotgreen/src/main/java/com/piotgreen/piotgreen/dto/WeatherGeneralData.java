package com.piotgreen.piotgreen.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;


////    TMP (기온): -4°C
////    WSD (풍속): 2.9 m/s
////    SKY (하늘 상태): 3 (구름 많음)
////    POP (강수 확률): 20%

@Data
//@AllArgsConstructor
@RequiredArgsConstructor
public class WeatherGeneralData {
    private String fcstDate;
    private String fcstTime;
    private String temperature;
    private String windSpeed;
    private String skyCondition;
    private String propabilityOfPrecipitation;
    private String precipitation;
}
