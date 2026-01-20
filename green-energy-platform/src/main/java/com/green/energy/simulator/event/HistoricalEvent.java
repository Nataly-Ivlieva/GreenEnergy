package com.green.energy.simulator.event;

import com.green.energy.simulator.model.Generator;
import com.green.energy.simulator.model.GeneratorType;
import com.green.energy.simulator.power.PowerOutput;
import com.green.energy.simulator.weather.WeatherDataPoint;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class HistoricalEvent {

    // --- Meta ---
    private Instant timestamp;

    // --- Generator ---
    private String generatorId;
    private GeneratorType generatorType;

    private double latitude;
    private double longitude;

    private double maxCapacityKw;

    // --- Weather ---
    private double temperatureC;
    private double windSpeedMs;
    private double solarIrradianceWm2;
    private double precipitationMm;
    private double cloudCover;

    // --- Power ---
    private double expectedPowerKw;
    private double actualPowerKw;

    // --- Anomaly ---
    private boolean anomalous;
    private String anomalyType;

    /**
     * Удобный фабричный метод
     */
    public static HistoricalEvent from(
            Generator generator,
            WeatherDataPoint weather,
            PowerOutput power,
            Instant timestamp
    ) {

        return HistoricalEvent.builder()
                .timestamp(timestamp)

                .generatorId(generator.getId())
                .generatorType(generator.getType())
                .latitude(generator.getLatitude())
                .longitude(generator.getLongitude())
                .maxCapacityKw(generator.getNominalPowerKw())

                .temperatureC(weather.getTemperatureC())
                .windSpeedMs(weather.getWindSpeedMs())
                .solarIrradianceWm2(weather.getSolarIrradianceWm2())
                .precipitationMm(weather.getPrecipitationMm())
                .cloudCover(weather.getCloudCover())

                .expectedPowerKw(power.getExpectedKw())
                .actualPowerKw(power.getActualKw())

                .anomalous(power.isAnomalous())
                .anomalyType(power.getAnomalyType())

                .build();
    }
}
