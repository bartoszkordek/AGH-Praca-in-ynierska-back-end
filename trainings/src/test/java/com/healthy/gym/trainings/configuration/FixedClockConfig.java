package com.healthy.gym.trainings.configuration;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;

@TestConfiguration
public class FixedClockConfig {

    @Primary
    @Bean
    Clock fixedClock() {
        return Clock.fixed(
                Instant.parse("2020-10-01T10:05:23.653Z"),
                ZoneId.of("Europe/Warsaw"));
    }
}
