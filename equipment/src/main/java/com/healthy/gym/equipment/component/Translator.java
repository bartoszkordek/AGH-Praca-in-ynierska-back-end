package com.healthy.gym.equipment.component;

public interface Translator {

    String toLocale(String message, Object[] args);

    String toLocale(String message);
}