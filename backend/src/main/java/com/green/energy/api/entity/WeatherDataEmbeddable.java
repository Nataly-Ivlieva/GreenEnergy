package com.green.energy.api.entity;

import jakarta.persistence.*;
import java.time.OffsetDateTime;

@Embeddable
public class WeatherDataEmbeddable {
        private Double temperatureC;
        private Double windSpeedMs;
        private Double solarIrradianceWm2;
        private Double precipitationMm;
        private Double cloudCover;

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

