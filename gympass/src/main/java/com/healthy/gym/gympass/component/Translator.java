package com.healthy.gym.gympass.component;

public interface Translator {

    String toLocale(String message, Object[] args);

    String toLocale(String message);
}