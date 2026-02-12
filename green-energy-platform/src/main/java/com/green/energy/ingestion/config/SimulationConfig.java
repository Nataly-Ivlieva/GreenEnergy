package com.green.energy.ingestion.config;

import com.green.energy.ingestion.anomaly.AnomalyInjector;
import com.green.energy.ingestion.power.PowerModel;
import com.green.energy.ingestion.service.GeneratorService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SimulationConfig {

    @Bean
    public PowerModel powerModel() {
        return new PowerModel();
    }
    @Bean
    public AnomalyInjector anomalyInjector() {
        return new AnomalyInjector();
    }
    @Bean
    public GeneratorService generatorService(
            PowerModel powerModel,
            AnomalyInjector anomalyInjector
    ) {
        return new GeneratorService(powerModel, anomalyInjector);
    }
}

