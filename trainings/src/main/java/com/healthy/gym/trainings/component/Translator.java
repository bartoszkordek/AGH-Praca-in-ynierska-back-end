package com.healthy.gym.trainings.component;

public interface Translator {

    String toLocale(String message, Object[] args);

    String toLocale(String message);
}