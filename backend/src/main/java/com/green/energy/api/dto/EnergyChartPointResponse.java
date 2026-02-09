package com.green.energy.api.dto;

import java.time.OffsetDateTime;

public record EnergyChartPointResponse(
        OffsetDateTime timestamp,
        double expectedPowerKw,
        Double actualPowerKw,
        Boolean anomalous
) {}

