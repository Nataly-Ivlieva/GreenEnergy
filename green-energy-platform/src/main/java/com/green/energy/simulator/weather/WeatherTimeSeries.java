package com.green.energy.simulator.weather;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class WeatherTimeSeries {

    private List<WeatherDataPoint> points;
}

