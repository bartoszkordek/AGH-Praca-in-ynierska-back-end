package com.healthy.gym.trainings.configuration;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;



@EnableMongoRepositories(basePackages = "com.healthy.gym.trainings.data.repository")
@Configuration
public class MongoConfig extends AbstractMongoClientConfiguration {

    private final Environment environment;

    @Autowired
    public MongoConfig(Environment environment) {
        this.environment = environment;
    }

    @Override
    protected String getDatabaseName() {
        return environment.getRequiredProperty("spring.data.mongodb.database");
    }

    @Bean
    public MongoClient mongoClient() {
        String uri = environment.getRequiredProperty("spring.data.mongodb.uri");
        return MongoClients.create(uri);
    }
}