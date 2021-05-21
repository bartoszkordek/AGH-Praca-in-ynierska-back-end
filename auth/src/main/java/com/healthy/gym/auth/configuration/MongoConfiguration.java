package com.healthy.gym.auth.configuration;

import com.mongodb.MongoClientSettings;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import java.util.Collections;

@EnableMongoRepositories(basePackages = "com.healthy.gym.auth.data.repository")
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

    @Override
    protected void configureClientSettings(MongoClientSettings.Builder builder) {
        MongoCredential mongoCredential = MongoCredential
                .createCredential(getUsername(), getDatabaseName(), getPassword());

        ServerAddress serverAddress = new ServerAddress(getHost(), getPort());

        builder.credential(mongoCredential)
                .applyToClusterSettings(settings -> settings.hosts(Collections.singletonList(serverAddress)));
    }

    private String getHost() {
        return environment.getRequiredProperty("spring.data.mongodb.host");
    }

    private String getUsername() {
        return environment.getRequiredProperty("spring.data.mongodb.username");
    }

    private char[] getPassword() {
        String password = environment.getRequiredProperty("spring.data.mongodb.password");
        return password.toCharArray();
    }

    private int getPort() {
        String port = environment.getRequiredProperty("spring.data.mongodb.port");
        return Integer.parseInt(port);
    }
}