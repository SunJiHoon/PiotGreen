package com.piotgreen.piotgreen.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
public class DailyLightData {
    private int day;
    private double averageLight;
}
