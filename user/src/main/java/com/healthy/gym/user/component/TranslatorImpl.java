package com.healthy.gym.user.component;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
public class TranslatorImpl implements Translator {

    private final MessageSource messageSource;

    @Autowired
    public TranslatorImpl(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @Override
    public String toLocale(String message, Object[] args) {
        Locale locale = LocaleContextHolder.getLocale();
        return messageSource.getMessage(message, args, locale);
    }

    @Override
    public String toLocale(String message) {
        return toLocale(message, null);
    }
}
