package com.healthy.gym.auth.component;

public interface Translator {

    String toLocale(String message, Object[] args);

    String toLocale(String message);
}
