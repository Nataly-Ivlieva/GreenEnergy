package com.green.energy.ingestion.noise;

import com.green.energy.ingestion.model.GeneratorType;
import com.green.energy.ingestion.power.PowerOutput;

public class NoiseApplier {

    private final NoiseGenerator generator = new NoiseGenerator();

    public PowerOutput apply(PowerOutput base, GeneratorType type) {

        double noisyActual = generator.apply(
                base.getActualKw(),
                type
        );

        return PowerOutput.builder()
                .expectedKw(base.getExpectedKw())
                .actualKw(round(noisyActual))
                .anomalous(base.isAnomalous())
                .anomalyType(base.getAnomalyType())
                .build();
    }

    private double round(double v) {
        return Math.round(v * 100.0) / 100.0;
    }
}
