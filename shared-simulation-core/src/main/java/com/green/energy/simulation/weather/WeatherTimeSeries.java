package com.green.energy.simulator.weather;

import com.green.energy.simulation.weather.WeatherSnapshot;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class WeatherTimeSeries {

    private List<WeatherSnapshot> points;
}

