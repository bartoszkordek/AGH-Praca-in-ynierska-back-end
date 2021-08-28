package com.healthy.gym.equipment.component;

import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class ImageUrlCreatorImpl implements ImageUrlCreator {

    private final Environment environment;

    public ImageUrlCreatorImpl(Environment environment) {
        this.environment = environment;
    }

    @Override
    public String createImageUrl(String imageId) {
        String gateway = environment.getRequiredProperty("gateway");
        String service = environment.getRequiredProperty("spring.application.name");
        String controller = "/image/" + imageId;

        return gateway + "/" + service + controller;
    }
}
