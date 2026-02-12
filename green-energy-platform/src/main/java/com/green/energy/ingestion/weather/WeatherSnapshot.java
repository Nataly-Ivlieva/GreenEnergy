package com.green.energy.ingestion.weather;

import java.time.Instant;

public record WeatherSnapshot(
        Instant timestamp,
        double temperatureC,
        double windSpeedMs,
        double solarIrradianceWm2,
        double precipitationMm,
        double cloudCover
) {}

