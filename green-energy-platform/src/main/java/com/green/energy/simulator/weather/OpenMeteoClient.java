package com.green.energy.simulator.weather;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDate;

@Component
public class OpenMeteoClient {
    private final RestTemplate restTemplate;
    public OpenMeteoClient(RestTemplateBuilder builder) {
        this.restTemplate = builder.build();
    }

    private void throttle() {
        try {
            Thread.sleep(1200);
        } catch (InterruptedException ignored) {
        }
    }

    public WeatherTimeSeries fetchHourlyWeather(
            double lat,
            double lon,
            LocalDate start,
            LocalDate end
    ) {
        while (true) {
            try {
                throttle();
                String url = UriComponentsBuilder
                        .fromHttpUrl("https://archive-api.open-meteo.com/v1/archive")
                        .queryParam("latitude", lat)
                        .queryParam("longitude", lon)
                        .queryParam("start_date", start)
                        .queryParam("end_date", end)
                        .queryParam("hourly",
                                "temperature_2m,wind_speed_10m,cloudcover,precipitation,shortwave_radiation")
                        .queryParam("timezone", "UTC")
                        .toUriString();

                ResponseEntity<OpenMeteoResponse> response =
                        restTemplate.getForEntity(url, OpenMeteoResponse.class);

                return WeatherMapper.map(response.getBody());
            } catch (HttpClientErrorException.TooManyRequests e) {
                try {
                    Thread.sleep(60000);
                } catch (InterruptedException ignored) {
                }
            }
        }
    }
}
