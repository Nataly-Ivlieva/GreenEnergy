package com.green.energy.ingestion;
import com.green.energy.ingestion.service.HistoricalGeneratorService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class HistoricalGeneratorApplication {

    public static void main(String[] args) {
        SpringApplication.run(HistoricalGeneratorApplication.class, args);
    }

    @Bean
    CommandLineRunner run(HistoricalGeneratorService service) {
        return args -> {
            service.generate();
        };
    }
}
