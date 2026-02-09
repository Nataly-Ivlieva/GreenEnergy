package com.green.energy.simulation.weather;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class WeatherTimeSeries {

    private List<WeatherSnapshot> points;
}

