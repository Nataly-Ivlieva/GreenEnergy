package com.green.energy.ingestion.ml;

import java.util.List;

    public record MlPredictionResponse(
            List<Double> expectedPowerKw
    ) {}

