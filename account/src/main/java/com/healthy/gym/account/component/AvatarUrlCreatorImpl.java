package com.healthy.gym.account.component;

import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class AvatarUrlCreatorImpl implements AvatarUrlCreator {

    private final Environment environment;

    public AvatarUrlCreatorImpl(Environment environment) {
        this.environment = environment;
    }

    @Override
    public String createAvatarUrl(String userId) {
        String gateway = environment.getRequiredProperty("gateway");
        String service = environment.getRequiredProperty("spring.application.name");
        String controller = "/photos/" + userId + "/avatar";

        return gateway + "/" + service + controller;
    }
}
