package com.green.energy.ingestion.init;

import com.green.energy.ingestion.entity.GeneratorEntity;
import com.green.energy.ingestion.model.GeneratorType;
import com.green.energy.ingestion.repository.GeneratorRepository;
import com.green.energy.ingestion.service.HistoricalGeneratorService;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Slf4j
@Component
public class DatabaseInitializer {

    private final GeneratorRepository generatorRepository;
    private final HistoricalGeneratorService historicalGeneratorService;

    public DatabaseInitializer(
            GeneratorRepository generatorRepository,
            HistoricalGeneratorService historicalGeneratorService
    ) {
        this.generatorRepository = generatorRepository;
        this.historicalGeneratorService = historicalGeneratorService;
    }

    @PostConstruct
    public void init() throws IOException {
        initGenerators();
        historicalGeneratorService.generate();
    }

    private void initGenerators() {
        if (generatorRepository.count() > 0) {
            return;
        }
        generatorRepository.saveAll(createGenerators());
    }

    private List<GeneratorEntity> createGenerators() {
        return List.of(
                // ===== SOLAR (10) =====
                generator("Solar Munich #1", GeneratorType.SOLAR, 48.1351, 11.5820, 500),
                generator("Solar Stuttgart #1", GeneratorType.SOLAR, 48.7758, 9.1829, 450),
                generator("Solar Augsburg #1", GeneratorType.SOLAR, 48.3705, 10.8978, 400),
                generator("Solar Ulm #1", GeneratorType.SOLAR, 48.4011, 9.9876, 420),
                generator("Solar Regensburg #1", GeneratorType.SOLAR, 49.0134, 12.1016, 380),
                generator("Solar Nuremberg #1", GeneratorType.SOLAR, 49.4521, 11.0767, 460),
                generator("Solar Ingolstadt #1", GeneratorType.SOLAR, 48.7665, 11.4257, 440),
                generator("Solar Passau #1", GeneratorType.SOLAR, 48.5667, 13.4319, 390),
                generator("Solar Rosenheim #1", GeneratorType.SOLAR, 47.8564, 12.1286, 410),
                generator("Solar Landshut #1", GeneratorType.SOLAR, 48.5442, 12.1469, 430),

                // ===== WIND (10) =====
                generator("Wind Hamburg #1", GeneratorType.WIND, 53.5511, 9.9937, 1800),
                generator("Wind Kiel #1", GeneratorType.WIND, 54.3233, 10.1228, 2000),
                generator("Wind Bremen #1", GeneratorType.WIND, 53.0793, 8.8017, 1700),
                generator("Wind Rostock #1", GeneratorType.WIND, 54.0924, 12.0991, 1900),
                generator("Wind Hannover #1", GeneratorType.WIND, 52.3759, 9.7320, 1600),
                generator("Wind Cuxhaven #1", GeneratorType.WIND, 53.8593, 8.7178, 2100),
                generator("Wind Wilhelmshaven #1", GeneratorType.WIND, 53.5290, 8.1126, 2050),
                generator("Wind Emden #1", GeneratorType.WIND, 53.3675, 7.2056, 1950),
                generator("Wind Flensburg #1", GeneratorType.WIND, 54.7937, 9.4469, 1850),
                generator("Wind Bremerhaven #1", GeneratorType.WIND, 53.5396, 8.5809, 1750),

                // ===== HYDRO (10) =====
                generator("Hydro Alps #1", GeneratorType.HYDRO, 47.5596, 10.7498, 3000),
                generator("Hydro Alps #2", GeneratorType.HYDRO, 47.5800, 10.7000, 2800),
                generator("Hydro Alps #3", GeneratorType.HYDRO, 47.6200, 10.6800, 2900),
                generator("Hydro Black Forest #1", GeneratorType.HYDRO, 48.3965, 8.1350, 2500),
                generator("Hydro Black Forest #2", GeneratorType.HYDRO, 48.2000, 8.2000, 2300),
                generator("Hydro Black Forest #3", GeneratorType.HYDRO, 48.4500, 8.0500, 2400),
                generator("Hydro Bavaria #1", GeneratorType.HYDRO, 47.8000, 11.0000, 2600),
                generator("Hydro Bavaria #2", GeneratorType.HYDRO, 47.8500, 11.0500, 2550),
                generator("Hydro Bavaria #3", GeneratorType.HYDRO, 47.9000, 11.1000, 2700),
                generator("Hydro Danube #1", GeneratorType.HYDRO, 48.7000, 13.0000, 3200)
        );
    }

    private GeneratorEntity generator(
            String name,
            GeneratorType type,
            double lat,
            double lon,
            double capacity
    ) {
        GeneratorEntity g = new GeneratorEntity();
        g.setName(name);
        g.setType(type);
        g.setLatitude(lat);
        g.setLongitude(lon);
        g.setMaxCapacityKw(capacity);
        return g;
    }


}
