package com.healthy.gym.account.component;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.i18n.LocaleContextHolder;

import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class TranslatorTest {
    private final Locale poland = new Locale("pl", "PL");
    private final String property = "password.change.success";

    @Autowired
    private Translator translator;

    @Test
    void shouldReturnEnglishMessage() {
        LocaleContextHolder.setLocale(Locale.ENGLISH);
        String messageAccordingToLocale = translator.toLocale(property);
        assertThat(messageAccordingToLocale).isEqualTo("Password changed successfully.");
    }

    @Test
    void shouldReturnPolishMessage() {
        LocaleContextHolder.setLocale(poland);
        String messageAccordingToLocale = translator.toLocale(property);
        assertThat(messageAccordingToLocale).isEqualTo("Hasło zostało pomyślnie zmienione.");
    }
}