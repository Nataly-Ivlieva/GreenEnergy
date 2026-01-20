package com.green.energy.simulator.weather;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.Instant;

@Data
@AllArgsConstructor
public class WeatherDataPoint {

    private Instant timestamp;

    private double temperatureC;
    private double windSpeedMs;
    private double solarIrradianceWm2;
    private double precipitationMm;
    private double cloudCover;
}
