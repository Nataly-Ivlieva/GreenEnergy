package com.green.energy.simulator.anomaly;

import com.green.energy.simulator.model.GeneratorType;
import com.green.energy.simulator.power.PowerOutput;
import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class AnomalyInjector {

    private static final double ANOMALY_PROBABILITY = 0.02; // 2%

    private final Random random = new Random();

    public PowerOutput injectIfNeeded(
            PowerOutput normalOutput,
            GeneratorType generatorType
    ) {

        if (random.nextDouble() > ANOMALY_PROBABILITY) {
            return normalOutput;
        }

        AnomalyType type = pickRandomAnomaly();

        double anomalousValue = switch (type) {
            case DROP_TO_ZERO -> 0.0;
            case SPIKE -> normalOutput.getExpectedKw() * (1.8 + random.nextDouble());
            case DEGRADATION -> normalOutput.getExpectedKw() * 0.4;
            case FLATLINE -> normalOutput.getActualKw(); // будет повторяться
            case WEATHER_MISMATCH -> generateWeatherMismatch(generatorType);
        };

        return PowerOutput.builder()
                .expectedKw(normalOutput.getExpectedKw())
                .actualKw(anomalousValue)
                .anomalous(true)
                .anomalyType(type.name())
                .build();

    }

    private AnomalyType pickRandomAnomaly() {
        AnomalyType[] values = AnomalyType.values();
        return values[random.nextInt(values.length)];
    }

    private double generateWeatherMismatch(GeneratorType type) {
        return switch (type) {
            case SOLAR -> randomBetween(0.0, 0.2);
            case WIND -> randomBetween(0.0, 0.3);
            case HYDRO -> randomBetween(0.5, 0.7);
        };
    }

    private double randomBetween(double min, double max) {
        return min + (max - min) * random.nextDouble();
    }
}
