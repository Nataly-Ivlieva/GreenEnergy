package com.green.energy.ingestion.scheduler;

import com.green.energy.ingestion.ml.MlClient;
import com.green.energy.ingestion.entity.EnergyMeasurementEntity;
import com.green.energy.ingestion.entity.GeneratorEntity;
import com.green.energy.ingestion.entity.WeatherDataEmbeddable;
import com.green.energy.ingestion.model.GeneratorType;
import com.green.energy.ingestion.power.PowerOutput;
import com.green.energy.ingestion.repository.EnergyMeasurementRepository;
import com.green.energy.ingestion.repository.GeneratorRepository;
import com.green.energy.ingestion.service.GeneratorService;
import com.green.energy.ingestion.service.PredictionService;
import com.green.energy.ingestion.weather.WeatherSnapshot;
import com.green.energy.ingestion.weather.dto.WeatherClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class WeatherScheduler {

    private final GeneratorRepository generatorRepository;
    private final EnergyMeasurementRepository measurementRepository;
    private final WeatherClient weatherClient;
    private final GeneratorService generatorService;
    private final PredictionService predictionService;
    private final MlClient mlClient;

    public WeatherScheduler(
            GeneratorRepository generatorRepository,
            EnergyMeasurementRepository measurementRepository,
            WeatherClient weatherClient,
            GeneratorService generatorService,
            PredictionService predictionService,
            MlClient mlClient
    ) {
        this.generatorRepository = generatorRepository;
        this.measurementRepository = measurementRepository;
        this.weatherClient = weatherClient;
        this.generatorService = generatorService;
        this.predictionService = predictionService;
        this.mlClient = mlClient;
    }

    @Scheduled(fixedRate = 5 * 60 * 1000)
    public void fetchWeather() {
        if (!mlClient.isModelReady()) {
            log.info("Model not ready yet...");
            return;
        }
        log.info("Starting weather fetch");

        OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);
        List<EnergyMeasurementEntity> measurementData = new ArrayList<>();
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
                        GeneratorType.valueOf(
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

                measurementData.add(measurement);


            } catch (Exception e) {
                log.error(
                        "Failed to process generator {}",
                        generator.getName(),
                        e
                );
            }

        }

        List<Double> results  = predictionService.predictBatch(measurementData);
        for (int i = 0; i < measurementData.size(); i++) {
            EnergyMeasurementEntity measurement = measurementData.get(i);
            Double prediction = results.get(i);

            measurement.setExpectedPowerKw(prediction);
        }
         measurementRepository.saveAll(measurementData);
    }
}
