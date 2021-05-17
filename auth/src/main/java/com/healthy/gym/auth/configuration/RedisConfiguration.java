package com.healthy.gym.auth.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

@Configuration
public class RedisConfiguration {
    private final Environment environment;

    @Autowired
    public RedisConfiguration(Environment environment) {
        this.environment = environment;
    }

    @Bean
    public RedisTemplate<String, String> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, String> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        return template;
    }

    @Bean
    public RedisStandaloneConfiguration getRedisStandaloneConfiguration() {
        return new RedisStandaloneConfiguration();
    }

    @Bean
    public LettuceConnectionFactory connectionFactory(RedisStandaloneConfiguration configuration) {
        configuration.setPassword(getRedisPassword());
        configuration.setHostName(getHostName());
        configuration.setPort(getPort());
        configuration.setDatabase(getDatabase());
        return new LettuceConnectionFactory(configuration);
    }

    private String getRedisPassword() {
        return environment.getRequiredProperty("spring.redis.password");
    }

    private String getHostName() {
        return environment.getRequiredProperty("spring.redis.host");
    }

    private int getPort() {
        String port = environment.getRequiredProperty("spring.redis.port");
        return Integer.parseInt(port);
    }

    private int getDatabase() {
        String database = environment.getRequiredProperty("spring.redis.database");
        return Integer.parseInt(database);
    }
}
