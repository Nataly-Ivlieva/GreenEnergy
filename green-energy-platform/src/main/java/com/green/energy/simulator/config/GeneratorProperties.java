package com.green.energy.simulator.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;

@Data
@Configuration
@ConfigurationProperties(prefix = "generator")
public class GeneratorProperties {

    private LocalDate startDate;
    private LocalDate endDate;

    private int generators;

    private String outputFile;

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }
}
