package com.green.energy.ingestion.event;

import com.green.energy.ingestion.model.Generator;
import com.green.energy.ingestion.model.GeneratorType;
import com.green.energy.ingestion.power.PowerOutput;
import com.green.energy.ingestion.weather.WeatherSnapshot;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class HistoricalEvent {

    private Instant timestamp;
    private String generatorId;
    private GeneratorType generatorType;
    private double latitude;
    private double longitude;
    private double maxCapacityKw;
    private double temperatureC;
    private double windSpeedMs;
    private double solarIrradianceWm2;
    private double precipitationMm;
    private double cloudCover;
    private double expectedPowerKw;
    private double actualPowerKw;
    private boolean anomalous;
    private String anomalyType;

    public static HistoricalEvent from(
            Generator generator,
            WeatherSnapshot weather,
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
                .temperatureC(weather.temperatureC())
                .windSpeedMs(weather.windSpeedMs())
                .solarIrradianceWm2(weather.solarIrradianceWm2())
                .precipitationMm(weather.precipitationMm())
                .cloudCover(weather.cloudCover())
                .expectedPowerKw(power.getExpectedKw())
                .actualPowerKw(power.getActualKw())
                .anomalous(power.isAnomalous())
                .anomalyType(power.getAnomalyType())

                .build();
    }
}
