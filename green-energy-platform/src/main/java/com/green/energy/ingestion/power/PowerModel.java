package com.green.energy.ingestion.power;

import com.green.energy.ingestion.model.GeneratorType;
import com.green.energy.ingestion.noise.NoiseGenerator;
import com.green.energy.ingestion.season.SeasonFactorCalculator;
import com.green.energy.ingestion.weather.WeatherSnapshot;


public class PowerModel {

    private final SolarPowerModel solar = new SolarPowerModel();
    private final WindPowerModel wind = new WindPowerModel();
    private final HydroPowerModel hydro = new HydroPowerModel();

    private final NoiseGenerator noise = new NoiseGenerator();
    private final SeasonFactorCalculator season = new SeasonFactorCalculator();

    public PowerOutput calculate(
            GeneratorType type,
            double maxCapacityKw,
            WeatherSnapshot weather
    ) {

        double expected = switch (type) {
            case SOLAR -> solar.expected(maxCapacityKw, weather);
            case WIND  -> wind.expected(maxCapacityKw, weather);
            case HYDRO -> hydro.expected(maxCapacityKw, weather);
        };

        expected *= season.seasonalFactor(weather.timestamp());

        expected = Math.max(0.0, Math.min(expected, maxCapacityKw));

        return PowerOutput.builder()
                .expectedKw(round(expected))
                .actualKw(round(expected)) // пока равно expected
                .anomalous(false)
                .build();
    }

    private double round(double v) {
        return Math.round(v * 100.0) / 100.0;
    }
}
