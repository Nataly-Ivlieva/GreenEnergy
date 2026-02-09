package com.green.energy.simulation.extreme;

import com.green.energy.simulation.weather.WeatherSnapshot;

import java.util.Random;

public class ExtremeEventDetector {

    private final Random random = new Random();

    public ExtremeCondition detect(
            WeatherSnapshot w,
            int hoursHeavyRain
    ) {

        // Temperature extremes
        if (w.temperatureC() > 40) return ExtremeCondition.HEAT_WAVE;
        if (w.temperatureC() < -20) return ExtremeCondition.EXTREME_COLD;

        // Storm wind
        if (w.windSpeedMs() > 25) return ExtremeCondition.STORM;
        if (w.windSpeedMs() > 35) return ExtremeCondition.HURRICANE;

        // Rain duration
        if (hoursHeavyRain > 24) return ExtremeCondition.LONG_HEAVY_RAIN;
        if (hoursHeavyRain > 48) return ExtremeCondition.FLOOD_RISK;

        // Rare random events
        if (random.nextDouble() < 0.0005) return ExtremeCondition.DUST_STORM;
        if (random.nextDouble() < 0.0005) return ExtremeCondition.ICING_RISK;

        return ExtremeCondition.NONE;
    }
}
