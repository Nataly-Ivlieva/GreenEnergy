package com.green.energy.api.entity;

import jakarta.persistence.*;
import java.time.OffsetDateTime;

@Entity
@Table(name = "energy_measurements")
public class EnergyMeasurementEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "generator_id")
    private GeneratorEntity generator;
    @Embedded
    private WeatherDataEmbeddable weather;
    private OffsetDateTime timestamp;
    private Double actualPowerKw;
    private Double expectedPowerKw;

    public Double getExpectedPowerKw() {
        return expectedPowerKw;
    }

    public void setExpectedPowerKw(Double expectedPowerKw) {
        this.expectedPowerKw = expectedPowerKw;
    }

    public WeatherDataEmbeddable getWeather() {
        return weather;
    }

    public void setWeather(WeatherDataEmbeddable weather) {
        this.weather = weather;
    }

    public GeneratorEntity getGenerator() {
        return generator;
    }

    public void setGenerator(GeneratorEntity generator) {
        this.generator = generator;
    }

    public OffsetDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(OffsetDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public Double getActualPowerKw() {
        return actualPowerKw;
    }

    public void setActualPowerKw(Double actualPowerKw) {
        this.actualPowerKw = actualPowerKw;
    }


}
