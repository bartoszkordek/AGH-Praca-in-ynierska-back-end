package com.healthy.gym.equipment.configuration;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;



@EnableMongoRepositories(basePackages = "com.healthy.gym.equipment.data.repository")
@Configuration
public class MongoConfiguration extends AbstractMongoClientConfiguration {

    private final Environment environment;

    @Autowired
    public MongoConfiguration(Environment environment) {
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