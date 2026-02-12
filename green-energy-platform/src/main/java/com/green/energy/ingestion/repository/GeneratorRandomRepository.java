package com.green.energy.ingestion.repository;

import com.green.energy.ingestion.model.Generator;
import com.green.energy.ingestion.model.GeneratorType;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Component
public class GeneratorRandomRepository {

    private static final Random RANDOM = new Random();
    private static final double MIN_LAT = 47.27;
    private static final double MAX_LAT = 55.06;
    private static final double MIN_LON = 5.87;
    private static final double MAX_LON = 15.04;

    public List<Generator> load(int count) {

        List<Generator> generators = new ArrayList<>();

        for (int i = 1; i <= count; i++) {
            GeneratorType type = randomType();

            generators.add(new Generator(
                    generateId(type, i),
                    type,
                    randomLat(),
                    randomLon(),
                    nominalPower(type)
            ));
        }

        return generators;
    }

    private GeneratorType randomType() {
        GeneratorType[] types = GeneratorType.values();
        return types[RANDOM.nextInt(types.length)];
    }

    private String generateId(GeneratorType type, int index) {
        return "DE-" + type.name() + "-" + String.format("%03d", index);
    }

    private double randomLat() {
        return MIN_LAT + (MAX_LAT - MIN_LAT) * RANDOM.nextDouble();
    }

    private double randomLon() {
        return MIN_LON + (MAX_LON - MIN_LON) * RANDOM.nextDouble();
    }

    private double nominalPower(GeneratorType type) {
        return switch (type) {
            case SOLAR -> 500 + RANDOM.nextInt(1500);   // 0.5–2 MW
            case WIND  -> 2000 + RANDOM.nextInt(3000);  // 2–5 MW
            case HYDRO -> 1000 + RANDOM.nextInt(4000);  // 1–5 MW
        };
    }
}
