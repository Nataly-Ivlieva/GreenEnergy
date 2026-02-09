package com.green.energy.api.weather;

import com.green.energy.api.entity.WeatherDataEmbeddable;
import com.green.energy.api.weather.dto.OpenMeteoResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class WeatherClient {

    private final WebClient webClient;

    public WeatherClient(WebClient.Builder builder) {
        this.webClient = builder
                .baseUrl("https://api.open-meteo.com/v1")
                .build();
    }

    public WeatherDataEmbeddable getCurrentWeather(double lat, double lon) {

        OpenMeteoResponse resp = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/forecast")
                        .queryParam("latitude", lat)
                        .queryParam("longitude", lon)
                        .queryParam(
                                "current",
                                "temperature_2m,wind_speed_10m,cloud_cover,precipitation,shortwave_radiation"
                        )
                        .build()
                )
                .retrieve()
                .bodyToMono(OpenMeteoResponse.class)
                .block();

        if (resp == null || resp.current == null) {
            return null;
        }

        return new WeatherDataEmbeddable(
                resp.current.temperature_2m,
                resp.current.wind_speed_10m,
                resp.current.shortwave_radiation,
                resp.current.precipitation,
                resp.current.cloud_cover
        );
    }
}