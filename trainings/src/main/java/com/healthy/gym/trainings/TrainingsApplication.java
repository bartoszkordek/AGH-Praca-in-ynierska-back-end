package com.healthy.gym.trainings;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;

import java.time.Clock;

@SpringBootApplication
@EnableDiscoveryClient
public class TrainingsApplication {

    public static void main(String[] args) {
        SpringApplication.run(TrainingsApplication.class, args);
    }

    @Bean
    public Clock clock() {
        return Clock.systemUTC();
    }
}
