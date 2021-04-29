package com.healthy.gym.user.configuration;

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
    public RedisStandaloneConfiguration redisStandaloneConfiguration() {
        return new RedisStandaloneConfiguration();
    }

    @Bean
    public LettuceConnectionFactory connectionFactory() {
        var redisStandaloneConfiguration = new RedisStandaloneConfiguration();

        String database = environment.getRequiredProperty("spring.redis.database");
        String host = environment.getRequiredProperty("spring.redis.host");
        String port = environment.getRequiredProperty("spring.redis.port");
        String password = environment.getRequiredProperty("spring.redis.password");

        redisStandaloneConfiguration.setDatabase(Integer.parseInt(database));
        redisStandaloneConfiguration.setHostName(host);
        redisStandaloneConfiguration.setPort(Integer.parseInt(port));
        redisStandaloneConfiguration.setPassword(password);

        return new LettuceConnectionFactory(redisStandaloneConfiguration);
    }
}
