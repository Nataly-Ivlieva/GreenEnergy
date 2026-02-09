package com.green.energy.api.dto;

import com.green.energy.api.domain.AnomalyType;
import com.green.energy.api.domain.GeneratorType;

import java.time.OffsetDateTime;
import java.util.UUID;

public record GeneratorStatusResponse(
        UUID generatorId,
        String name,
        GeneratorType type,
        double latitude,
        double longitude,

        double expectedPowerKw,
        Double actualPowerKw,

        boolean anomalous,
        AnomalyType anomalyType,

        OffsetDateTime timestamp
) {}

