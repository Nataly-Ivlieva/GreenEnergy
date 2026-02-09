package com.green.energy.simulator.weather;

import lombok.Data;
import java.util.List;

@Data
public class OpenMeteoResponse {
    private Hourly hourly;

    @Data
    public static class Hourly {
        private List<String> time;
        private List<Double> temperature_2m;
        private List<Double> wind_speed_10m;
        private List<Integer> cloudcover;
        private List<Double> precipitation;
        private List<Double> shortwave_radiation;
    }
}
