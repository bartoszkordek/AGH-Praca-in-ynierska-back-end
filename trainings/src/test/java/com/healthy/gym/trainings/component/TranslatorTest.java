package com.healthy.gym.trainings.component;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@SpringBootTest
@ActiveProfiles(value = "test")
class TranslatorTest {

    @Container
    static GenericContainer<?> rabbitMQContainer =
            new GenericContainer<>(DockerImageName.parse("gza73/agh-praca-inzynierska-rabbitmq"))
                    .withExposedPorts(5672);
    private final Locale poland = new Locale("pl", "PL");
    private final String property = "field.required";
    @Autowired
    private Translator translator;

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.rabbitmq.port", rabbitMQContainer::getFirstMappedPort);
    }

    @Test
    void shouldReturnEnglishMessage() {
        LocaleContextHolder.setLocale(Locale.ENGLISH);
        String messageAccordingToLocale = translator.toLocale(property);
        assertThat(messageAccordingToLocale).isEqualTo("Field is required.");
    }

    @Test
    void shouldReturnPolishMessage() {
        LocaleContextHolder.setLocale(poland);
        String messageAccordingToLocale = translator.toLocale(property);
        assertThat(messageAccordingToLocale).isEqualTo("Pole jest wymagane.");
    }
}