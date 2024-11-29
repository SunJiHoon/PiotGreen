package com.piotgreen.piotgreen.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DailyMoistureData {
    private int day;
    private double averageMoisture;
}
