package com.healthy.gym.auth.configuration;

import org.springframework.context.annotation.Lazy;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import redis.embedded.RedisServer;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@Lazy
@Component
public class EmbeddedRedisServer {

    private final Environment environment;
    private final RedisServer redisServer;

    public EmbeddedRedisServer(Environment environment) {
        this.environment = environment;
        this.redisServer = RedisServer.builder()
                .port(getRedisTestPort())
                .bind("127.0.0.1")
                .setting("requirepass " + getRedisPassword())
                .build();
    }

    private int getRedisTestPort() {
        String port = environment.getRequiredProperty("spring.redis.test.port");
        return Integer.parseInt(port);
    }

    private String getRedisPassword() {
        return environment.getRequiredProperty("spring.redis.password");
    }

    @PostConstruct
    private void postConstruct() {
        redisServer.start();
    }

    @PreDestroy
    private void preDestroy() {
        redisServer.stop();
    }
}
