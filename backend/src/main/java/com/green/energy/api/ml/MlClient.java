package com.green.energy.api.ml;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class MlClient {

    private final WebClient webClient;
    private final MlProperties props;

    public MlClient(MlProperties props) {
        this.props = props;
        this.webClient = WebClient.builder()
                .baseUrl(props.getBaseUrl())
                .defaultHeader(
                        props.getHeaderName(),
                        props.getApiKey()
                )
                .build();
    }

    public double predictExpectedPower(MlPredictionRequest req) {

        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/predict")
                        .queryParam("timestamp", req.timestamp)
                        .queryParam("generatorType", req.generatorType)
                        .queryParam("maxCapacityKw", req.maxCapacityKw)
                        .queryParam("temperatureC", req.temperatureC)
                        .queryParam("windSpeedMs", req.windSpeedMs)
                        .queryParam("solarIrradianceWm2", req.solarIrradianceWm2)
                        .queryParam("precipitationMm", req.precipitationMm)
                        .queryParam("cloudCover", req.cloudCover)
                        .build()
                )
                .retrieve()
                .bodyToMono(MlPredictionResponse.class)
                .block()
                .getExpectedPowerKw();
    }
}
