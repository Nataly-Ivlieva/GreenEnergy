package com.green.energy.simulator.power;

import com.green.energy.simulator.model.Generator;
import com.green.energy.simulator.weather.WeatherDataPoint;
import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class PowerModel {

    private static final double NOISE_STD_DEV = 0.05; // 5% шум
    private final Random random = new Random();

    public PowerOutput calculate(Generator generator, WeatherDataPoint weather) {

        double expectedKw = switch (generator.getType()) {
            case SOLAR -> solarPower(generator, weather);
            case WIND -> windPower(generator, weather);
            case HYDRO -> hydroPower(generator, weather);
        };

        double actualKw = applyNoise(expectedKw);

        return PowerOutput.builder()
                .expectedKw(round(expectedKw))
                .actualKw(round(actualKw))
                .anomalous(false)
                .build();
    }

    // ---------- SOLAR ----------
    private double solarPower(Generator generator, WeatherDataPoint w) {
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
        return 1.0 - (tempC - 25) * 0.005; // −0.5% per °C
    }

    // ---------- WIND ----------
    private double windPower(Generator generator, WeatherDataPoint w) {
        double v = w.getWindSpeedMs();

        if (v < 3 || v > 25) return 0.0;

        double normalized =
                (Math.pow(v, 3) - Math.pow(3, 3)) /
                        (Math.pow(12, 3) - Math.pow(3, 3));

        return generator.getNominalPowerKw() * clamp(normalized, 0, 1);
    }

    // ---------- HYDRO ----------
    private double hydroPower(Generator generator, WeatherDataPoint w) {
        double baseFlow = 0.6;
        double rainBoost = clamp(w.getPrecipitationMm() / 50.0, 0, 0.4);

        return generator.getNominalPowerKw() * (baseFlow + rainBoost);
    }

    // ---------- Helpers ----------
    private double applyNoise(double value) {
        double noise = random.nextGaussian() * NOISE_STD_DEV;
        return Math.max(0, value * (1 + noise));
    }

    private double clamp(double v, double min, double max) {
        return Math.max(min, Math.min(max, v));
    }

    private double round(double v) {
        return Math.round(v * 100.0) / 100.0;
    }
}
