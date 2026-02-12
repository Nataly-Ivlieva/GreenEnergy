package com.green.energy.ingestion.entity;

import jakarta.persistence.Embeddable;

@Embeddable
public class WeatherDataEmbeddable {
        private Double temperatureC;
        private Double windSpeedMs;
        private Double solarIrradianceWm2;
        private Double precipitationMm;
        private Double cloudCover;

        public WeatherDataEmbeddable(){}

    public WeatherDataEmbeddable(double temperature_2m, double wind_speed_10m, double shortwave_radiation, double precipitation, double cloud_cover) {
        this.temperatureC = temperature_2m;
        this.windSpeedMs = wind_speed_10m;
        this.solarIrradianceWm2 = shortwave_radiation;
        this.precipitationMm = precipitation;
        this.cloudCover = cloud_cover;
    }

    public Double getTemperatureC() {
        return temperatureC;
    }

    public void setTemperatureC(Double temperatureC) {
        this.temperatureC = temperatureC;
    }

    public Double getWindSpeedMs() {
        return windSpeedMs;
    }

    public void setWindSpeedMs(Double windSpeedMs) {
        this.windSpeedMs = windSpeedMs;
    }

    public Double getSolarIrradianceWm2() {
        return solarIrradianceWm2;
    }

    public void setSolarIrradianceWm2(Double solarIrradianceWm2) {
        this.solarIrradianceWm2 = solarIrradianceWm2;
    }

    public Double getPrecipitationMm() {
        return precipitationMm;
    }

    public void setPrecipitationMm(Double precipitationMm) {
        this.precipitationMm = precipitationMm;
    }

    public Double getCloudCover() {
        return cloudCover;
    }

    public void setCloudCover(Double cloudCover) {
        this.cloudCover = cloudCover;
    }
}

