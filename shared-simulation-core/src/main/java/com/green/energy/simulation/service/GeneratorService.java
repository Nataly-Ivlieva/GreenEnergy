package com.green.energy.simulation.service;

import com.green.energy.simulation.anomaly.AnomalyInjector;
import com.green.energy.simulation.extreme.ExtremeCondition;
import com.green.energy.simulation.extreme.ExtremeEventDetector;
import com.green.energy.simulation.extreme.ExtremePowerModifier;
import com.green.energy.simulation.generator.GeneratorType;
import com.green.energy.simulation.noise.NoiseApplier;
import com.green.energy.simulation.power.PowerModel;
import com.green.energy.simulation.power.PowerOutput;
import com.green.energy.simulation.weather.WeatherSnapshot;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.time.Instant;

@RequiredArgsConstructor
public class GeneratorService {

    private final PowerModel powerModel;
    private final AnomalyInjector anomalyInjector;
    private final ExtremeEventDetector extremeDetector = new ExtremeEventDetector();
    private final ExtremePowerModifier extremeModifier = new ExtremePowerModifier();
    private final NoiseApplier noiseApplier = new NoiseApplier();

    public PowerOutput generate(WeatherSnapshot w, GeneratorType gt, double nominalPowerKw) throws IOException {
        int heavyRainHours = 0;
        Instant timestamp = w.timestamp();

        // =============================
        // Rain accumulation tracking
        // =============================
        if (w.precipitationMm() > 5) {
            heavyRainHours++;
        } else {
            heavyRainHours = 0;
        }

        // =============================
        // Extreme detection
        // =============================
        ExtremeCondition extreme =
                extremeDetector.detect(w, heavyRainHours);

        // =============================
        // Base physics model
        // =============================
        PowerOutput base =
                powerModel.calculate(
                        gt,
                        nominalPowerKw,
                        w
                );

        // =============================
        // Apply extreme physics impact
        // =============================
        PowerOutput extremeAdjusted =
                extremeModifier.apply(
                        base,
                        gt,
                        extreme,
                        nominalPowerKw
                );

        // =============================
        // Apply noise
        // =============================
        PowerOutput noisy =
                noiseApplier.apply(extremeAdjusted);

        // =============================
        // Inject anomalies (faults)
        // =============================
        PowerOutput finalPower =
                anomalyInjector.injectIfNeeded(
                        noisy,
                        gt,
                        nominalPowerKw
                );
        return finalPower;
    }
}
