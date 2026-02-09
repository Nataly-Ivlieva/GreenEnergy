package com.green.energy.simulator.power;

import com.green.energy.simulator.model.Generator;
import com.green.energy.simulator.model.GeneratorType;
import com.green.energy.simulator.weather.WeatherDataPoint;
import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class PowerModel {

    private final Random random = new Random();

    public PowerOutput calculate(Generator generator, WeatherDataPoint weather) {

        double expectedKw = switch (generator.getType()) {
            case SOLAR -> solarPower(generator, weather);
            case WIND -> windPower(generator, weather);
            case HYDRO -> hydroPower(generator, weather);
        };

        double actualKw = applyNoise(expectedKw, generator.getType());

        return PowerOutput.builder()
                .expectedKw(round(expectedKw))
                .actualKw(round(actualKw))
                .anomalous(false)
                .build();
    }

    // ================= SOLAR =================

    private double solarPower(Generator generator, WeatherDataPoint w) {

        // Night cutoff
        if (w.getSolarIrradianceWm2() < 20) {
            return 0.0;
        }

        double irradianceFactor = clamp(w.getSolarIrradianceWm2() / 1000.0, 0, 1);
        double cloudLoss = 1.0 - clamp(w.getCloudCover(), 0, 1);
        double temperatureLoss = temperatureLoss(w.getTemperatureC());

        return generator.getNominalPowerKw()
                * irradianceFactor
                * cloudLoss
                * temperatureLoss;
    }

    private double temperatureLoss(double tempC) {
        if (tempC <= 25) return 1.0;
        return Math.max(0.7, 1.0 - (tempC - 25) * 0.005);
    }

    // ================= WIND =================

    private double windPower(Generator generator, WeatherDataPoint w) {

        double v = w.getWindSpeedMs();

        // cut-in
        if (v < 3) return 0;

        // ramp cubic
        if (v <= 12) {
            double normalized =
                    (Math.pow(v, 3) - Math.pow(3, 3)) /
                            (Math.pow(12, 3) - Math.pow(3, 3));

            return generator.getNominalPowerKw() * clamp(normalized, 0, 1);
        }

        // rated plateau
        if (v <= 25) {
            return generator.getNominalPowerKw();
        }

        // storm shutdown
        return 0;
    }

    // ================= HYDRO =================

    private double hydroPower(Generator generator, WeatherDataPoint w) {

        double baseFlow = 0.6;
        double rainBoost = clamp(w.getPrecipitationMm() / 50.0, 0, 0.5);

        return generator.getNominalPowerKw() * (baseFlow + rainBoost);
    }

    // ================= Noise =================

    private double applyNoise(double value, GeneratorType type) {

        if (value <= 0) return 0;

        double std = switch (type) {
            case SOLAR -> 0.03;
            case WIND -> 0.06;
            case HYDRO -> 0.02;
        };

        double noise = random.nextGaussian() * std;
        return Math.max(0, value * (1 + noise));
    }

    // ================= Helpers =================

    private double clamp(double v, double min, double max) {
        return Math.max(min, Math.min(max, v));
    }

    private double round(double v) {
        return Math.round(v * 100.0) / 100.0;
    }
}

