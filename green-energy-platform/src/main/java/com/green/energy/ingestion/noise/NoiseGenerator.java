package com.green.energy.ingestion.noise;

import com.green.energy.ingestion.model.GeneratorType;

import java.util.Random;

public class NoiseGenerator {

    private final Random random = new Random();

    public double apply(double value, GeneratorType type) {

        if (value <= 0) return 0.0;

        double stdDev = switch (type) {
            case SOLAR -> 0.03;
            case WIND -> 0.07;
            case HYDRO -> 0.02;
        };

        double noise = random.nextGaussian() * stdDev;
        return Math.max(0, value * (1 + noise));
    }

}
