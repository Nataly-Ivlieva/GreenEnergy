package com.green.energy.ingestion.ml;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Slf4j
@Component
public class MlClient {

    private final WebClient webClient;

    public MlClient(MlProperties props) {
        this.webClient = WebClient.builder()
                .baseUrl(props.getBaseUrl())
                .defaultHeader(
                        props.getHeaderName(),
                        props.getApiKey()
                )
                .build();
    }
    public List<Double> predictExpectedPowerBatch(List<MlPredictionRequest> batch) {

        return webClient.post()
                .uri("/predict/batch")
                .bodyValue(batch)
                .retrieve()
                .bodyToMono(MlPredictionResponse.class)
                .block()
                .expectedPowerKw();
    }

    public boolean isModelReady() {

        try {
            log.info("Calling ML /model/status...");

            MlModelStatusResponse response = webClient.get()
                    .uri("/model/status")
                    .retrieve()
                    .bodyToMono(MlModelStatusResponse.class)
                    .block();

            log.info("ML response: {}", response);

            return response != null && response.trained();

        } catch (Exception e) {
            log.error("Error calling ML service", e);
            return false;
        }
    }
}
