package com.green.energy.api.weather.dto;

public class OpenMeteoResponse {

    public Current current;

    public static class Current {
        public double temperature_2m;
        public double wind_speed_10m;
        public double cloud_cover;
        public double precipitation;
        public double shortwave_radiation;
    }
}
