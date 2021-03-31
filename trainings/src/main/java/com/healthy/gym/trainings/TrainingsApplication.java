package com.healthy.gym.trainings;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class TrainingsApplication {

    public static void main(String[] args) {
        SpringApplication.run(TrainingsApplication.class, args);
    }

}
