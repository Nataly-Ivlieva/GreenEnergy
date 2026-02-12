package com.green.energy.ingestion.service;

import com.green.energy.ingestion.config.GeneratorProperties;
import com.green.energy.ingestion.event.HistoricalEvent;
import com.green.energy.ingestion.power.PowerOutput;
import com.green.energy.ingestion.repository.GeneratorRandomRepository;
import com.green.energy.ingestion.model.Generator;

import com.green.energy.ingestion.weather.OpenMeteoClient;
import com.green.energy.ingestion.weather.WeatherSnapshot;
import com.green.energy.ingestion.weather.WeatherTimeSeries;
import com.green.energy.ingestion.writer.JsonEventWriter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HistoricalGeneratorService {

    private final GeneratorRandomRepository generatorRepository;
    private final OpenMeteoClient weatherClient;
    private final JsonEventWriter writer;
    private final GeneratorProperties props;
    private final GeneratorService generatorService;

    public void generate() throws IOException {
        if (Files.exists(JsonEventWriter.OUTPUT)) {
            return;
        }
        List<Generator> generators =
                generatorRepository.load(props.getGenerators());

        for (Generator g : generators) {

            WeatherTimeSeries weather =
                    weatherClient.fetchHourlyWeather(
                            g.getLatitude(),
                            g.getLongitude(),
                            props.getStartDate(),
                            props.getEndDate()
                    );

            for (WeatherSnapshot w : weather.getPoints()) {
                PowerOutput finalPower = generatorService.generate(w, g.getType(), g.getNominalPowerKw());
                Instant timestamp = w.timestamp();
                HistoricalEvent event = HistoricalEvent.from(g, w, finalPower, timestamp);

                writer.write(event);
            }
        }

        writer.close();
    }
}
