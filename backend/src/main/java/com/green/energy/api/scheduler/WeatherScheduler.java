package com.green.energy.api.scheduler;

import com.green.energy.api.entity.EnergyMeasurementEntity;
import com.green.energy.api.entity.GeneratorEntity;
import com.green.energy.api.entity.WeatherDataEmbeddable;
import com.green.energy.api.repository.EnergyMeasurementRepository;
import com.green.energy.api.repository.GeneratorRepository;
import com.green.energy.api.weather.WeatherClient;
import com.green.energy.simulation.power.PowerOutput;
import com.green.energy.simulation.service.GeneratorService;
import com.green.energy.simulation.weather.WeatherSnapshot;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Slf4j
@Component
public class WeatherScheduler {

    private final GeneratorRepository generatorRepository;
    private final EnergyMeasurementRepository measurementRepository;
    private final WeatherClient weatherClient;
    private final GeneratorService generatorService;

    public WeatherScheduler(
            GeneratorRepository generatorRepository,
            EnergyMeasurementRepository measurementRepository,
            WeatherClient weatherClient,
            GeneratorService generatorService
    ) {
        this.generatorRepository = generatorRepository;
        this.measurementRepository = measurementRepository;
        this.weatherClient = weatherClient;
        this.generatorService = generatorService;
    }

  //  @Scheduled(fixedRate = 5 * 60 * 1000)
    public void fetchWeather() {

        log.info("Starting weather fetch");

        OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);

        for (GeneratorEntity generator : generatorRepository.findAll()) {
            try {
                WeatherDataEmbeddable data = weatherClient.getCurrentWeather(
                        generator.getLatitude(),
                        generator.getLongitude()
                );

                if (data == null) {
                    log.warn("No weather data for generator {}", generator.getName());
                    continue;
                }

                WeatherSnapshot snapshot = new WeatherSnapshot(
                        now.toInstant(),
                        data.getTemperatureC(),
                        data.getWindSpeedMs(),
                        data.getSolarIrradianceWm2(),
                        data.getPrecipitationMm(),
                        data.getCloudCover()
                );

                PowerOutput finalPower = generatorService.generate(
                        snapshot,
                        com.green.energy.simulation.generator.GeneratorType.valueOf(
                                generator.getType().name()
                        ),
                        generator.getMaxCapacityKw()
                );

                WeatherDataEmbeddable weather = new WeatherDataEmbeddable();
                weather.setTemperatureC(data.getTemperatureC());
                weather.setWindSpeedMs(data.getWindSpeedMs());
                weather.setSolarIrradianceWm2(data.getSolarIrradianceWm2());
                weather.setPrecipitationMm(data.getPrecipitationMm());
                weather.setCloudCover(data.getCloudCover());

                EnergyMeasurementEntity measurement = new EnergyMeasurementEntity();
                measurement.setGenerator(generator);
                measurement.setTimestamp(now);
                measurement.setActualPowerKw(finalPower.getActualKw());
                measurement.setWeather(weather);

                measurementRepository.save(measurement);

            } catch (Exception e) {
                log.error(
                        "Failed to process generator {}",
                        generator.getName(),
                        e
                );
            }
        }
    }
}
